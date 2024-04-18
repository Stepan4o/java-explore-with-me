package ru.practicum.explore_with_me.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.category.repository.CategoryRepository;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.EventShortDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.event.dto.UpdateEventRequest;
import ru.practicum.explore_with_me.event.dto.search.AdminSearchEventsParams;
import ru.practicum.explore_with_me.event.dto.search.PublicSearchEventsParams;
import ru.practicum.explore_with_me.event.mapper.EventMapper;
import ru.practicum.explore_with_me.event.model.AdminStateAction;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.UserStateAction;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.exception.ConflictException;
import ru.practicum.explore_with_me.exception.IncorrectDateTime;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.location.Location;
import ru.practicum.explore_with_me.location.LocationMapper;
import ru.practicum.explore_with_me.location.LocationRepository;
import ru.practicum.explore_with_me.stats.client.StatsClient;
import ru.practicum.explore_with_me.stats.dto.EndpointHitDto;
import ru.practicum.explore_with_me.stats.dto.ViewStatsDto;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static ru.practicum.explore_with_me.event.model.AdminStateAction.PUBLISH_EVENT;
import static ru.practicum.explore_with_me.event.model.AdminStateAction.REJECT_EVENT;
import static ru.practicum.explore_with_me.event.model.State.*;
import static ru.practicum.explore_with_me.event.model.UserStateAction.CANCEL_REVIEW;
import static ru.practicum.explore_with_me.event.model.UserStateAction.SEND_TO_REVIEW;
import static ru.practicum.explore_with_me.utils.Const.*;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;
    @Value("${app}")
    String app;

    @Override
    public EventFullDto add(NewEventDto newEventDto, Long userId) {
        validatedEventDate(newEventDto.getEventDate());
        User savedUser = getUserIfExists(userId);
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, CATEGORY, newEventDto.getCategory()
                )));

        Location location = locationRepository.save(LocationMapper.toLocation(newEventDto.getLocation()));

        Event event = EventMapper.toEvent(newEventDto);
        event.setCategory(category);
        event.setInitiator(savedUser);
        event.setLocation(location);
        event.setState(PENDING);
        event.setCreatedOn(LocalDateTime.now());
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> getFullInfoByAdminParams(AdminSearchEventsParams params) {
        LocalDateTime start = params.getRangeStart();
        LocalDateTime end = params.getRangeEnd();

        if (start == null) params.setRangeStart(LocalDateTime.now());

        if (end != null && Objects.requireNonNull(start).isAfter(end))
            throw new ConflictException("Start time can't be after End time");

        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        Specification<Event> spec = Specification.where(null);

        spec = spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), params.getRangeStart()));

        if (end != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), end));
        }
        if (params.getUsers() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(params.getUsers()));
        }

        if (params.getCategories() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    root.get("category").in(params.getCategories()));
        }

        return eventRepository.findAll(spec, pageable).stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateAdminInfo(long eventId, UpdateEventRequest requestForUpdate) {
        Event savedEvent = getEventIfExists(eventId);

        if (requestForUpdate.getCategory() != null) {
            long catId = requestForUpdate.getCategory();
            Category category = categoryRepository.findById(catId)
                    .orElseThrow(() -> new NotFoundException(String.format(
                            ENTITY_NOT_FOUND, CATEGORY, catId)
                    ));
            savedEvent.setCategory(category);
        }
        if (requestForUpdate.getEventDate() != null) {
            LocalDateTime actualEventTime = savedEvent.getEventDate();
            if (actualEventTime.plusHours(1).isAfter(requestForUpdate.getEventDate()) ||
                    actualEventTime.plusHours(1) != requestForUpdate.getEventDate())
                savedEvent.setEventDate(requestForUpdate.getEventDate());

            else throw new ConflictException("eventDate can't be earlier than one hour from the date of publication.");
        }

        if (requestForUpdate.getLocation() != null) {
            savedEvent.setLocation(locationRepository.save(LocationMapper.toLocation(requestForUpdate.getLocation())));
        }

        if (requestForUpdate.getStateAction() != null) {
            if (savedEvent.getState() == PENDING) {
                AdminStateAction action = AdminStateAction.toEnum(requestForUpdate.getStateAction());
                if (action == PUBLISH_EVENT) {
                    savedEvent.setState(PUBLISHED);
                    savedEvent.setPublishedOn(LocalDateTime.now());
                } else if (action == REJECT_EVENT) {
                    savedEvent.setState(CANCELED);
                }
            } else {
                throw new ConflictException(String.format(
                        "Cannot publish the event because it's not in the right state: %S",
                        savedEvent.getState())
                );
            }
        }
        updateEventFields(savedEvent, requestForUpdate);
        return EventMapper.toEventFullDto(eventRepository.save(savedEvent));
    }

    @Override
    public EventFullDto updateOwnerEvent(long userId, long eventId, UpdateEventRequest requestForUpdate) {
        Event savedEvent = getEventIfExists(eventId);
        if (savedEvent.getInitiator().getId() != userId) {
            throw new ConflictException(OWNER_ONLY);
        }

        if (savedEvent.getState() == PUBLISHED) throw new ConflictException("Published event can't be update");

        if (requestForUpdate.getStateAction() != null) {
            UserStateAction action = UserStateAction.toEnum(requestForUpdate.getStateAction());
            if (action.equals(SEND_TO_REVIEW)) savedEvent.setState(PENDING);
            if (action.equals(CANCEL_REVIEW)) savedEvent.setState(CANCELED);
        }

        if (requestForUpdate.getCategory() != null) {
            Category category = getCategoryIfExists(requestForUpdate.getCategory());
            savedEvent.setCategory(category);
        }
        if (requestForUpdate.getEventDate() != null) {
            LocalDateTime futureEventTime = requestForUpdate.getEventDate();
            validatedEventDate(futureEventTime);
            savedEvent.setEventDate(requestForUpdate.getEventDate());
        }

        updateEventFields(savedEvent, requestForUpdate);

        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public List<EventShortDto> searchEventsByPublicParams(PublicSearchEventsParams params) {

        LocalDateTime start = params.getRangeStart();
        LocalDateTime end = params.getRangeEnd();

        if (start == null) params.setRangeStart(LocalDateTime.now());

        if (end != null && Objects.requireNonNull(start).isAfter(end))
            throw new IncorrectDateTime("Start time can't be after End time");

        Specification<Event> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> row = new ArrayList<>();

            if (params.getCategories() != null) {
                row.add(root.get("category").get("id").in(params.getCategories()));
            }

            if (params.getPaid() != null) {
                row.add(criteriaBuilder.equal(root.get("paid"), params.getPaid()));
            }

            if (params.getOnlyAvailable() != null && params.getOnlyAvailable()) {
                row.add(criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0),
                        criteriaBuilder.lessThan(root.get("participantLimit"), root.get("confirmedRequest"))
                ));
            }
            LocalDateTime current = LocalDateTime.now();
            LocalDateTime startDateTime = Objects.requireNonNullElse(params.getRangeStart(), current);
            row.add(criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));
            if (params.getRangeEnd() != null) {
                row.add(criteriaBuilder.lessThan(root.get("eventDate"), params.getRangeEnd()));
            }
            if (params.getText() != null && !params.getText().isBlank()) {
                String likeText = "%" + params.getText() + "%";
                row.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("annotation"), likeText),
                        criteriaBuilder.like(root.get("description"), likeText)
                ));
            }
            return criteriaBuilder.and(row.toArray(new Predicate[0]));
        };

        int from = params.getFrom(), size = params.getSize();
        Pageable pageable = PageRequest.of(from / size, size);
        if (params.getSort() != null) {
            switch (params.getSort()) {
                case "EVENT_DATE":
                    pageable = PageRequest.of(from / size, size, Sort.Direction.ASC, "eventDate");
                    break;
                case "VIEWS":
                    pageable = PageRequest.of(from / size, size, Sort.Direction.DESC, "views");
                    break;
            }
        }
        List<Event> events = eventRepository.findAll(spec, pageable);
        if (events.isEmpty()) return List.of();

        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());
        Optional<LocalDateTime> startTime = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo);
        saveStats(params.getUri(), params.getIp());

        Map<Long, Event> eventsMap = new HashMap<>();
        for (Event event : events) eventsMap.put(event.getId(), event);
        List<ViewStatsDto> stats = statsClient.getStats(
                startTime.get(),
                LocalDateTime.now(),
                uris,
                true
        );
        addViewsToEvents(eventsMap, stats);

        return eventsMap.values().stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(long eventId, String ip) {
        Event savedEvent = eventRepository.findByIdAndState(eventId, PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, EVENT, eventId
                )));
        String uri = String.format("/events/%d", eventId);
        saveStats(uri, ip);
        List<ViewStatsDto> stats = statsClient.getStats(
                savedEvent.getCreatedOn(),
                LocalDateTime.now(),
                List.of(uri),
                true
        );
        if (stats.size() == 1) {
            savedEvent.setViews(stats.get(0).getHits());
            eventRepository.save(savedEvent);
        }
        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto getOwnerEventById(long userId, long eventId) {
        User owner = getUserIfExists(userId);
        Event ownerEvent = eventRepository.findByIdAndInitiatorId(eventId, owner.getId()).orElseThrow(
                () -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, EVENT, eventId
                ))
        );
        return EventMapper.toEventFullDto(ownerEvent);
    }

    @Override
    public List<EventShortDto> getOwnerEvents(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        User owner = getUserIfExists(userId);
        List<Event> ownerEvents = eventRepository.findAllByInitiatorId(owner.getId(), pageable);
        return ownerEvents.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    private void addViewsToEvents(Map<Long, Event> eventsMap, List<ViewStatsDto> stats) {
        long eventId, views;
        List<Event> listOfSavedEvents = new ArrayList<>();
        for (ViewStatsDto stat : stats) {
            String uri = stat.getUri();
            eventId = Long.parseLong(uri.substring(uri.lastIndexOf("/") + 1));
            views = stat.getHits();
            if (eventsMap.containsKey(views)) {
                Event savedEvent = eventsMap.get(eventId);
                savedEvent.setViews(views);
                listOfSavedEvents.add(savedEvent);
                eventsMap.put(eventId, savedEvent);
            }
        }
        eventRepository.saveAll(listOfSavedEvents);
    }

    private void saveStats(String uri, String ip) {
        statsClient.saveHit(new EndpointHitDto(
                app, uri, ip, LocalDateTime.now()
        ));
    }

    private void updateEventFields(Event savedEvent, UpdateEventRequest newEvent) {
        Map<String, BiConsumer<Event, UpdateEventRequest>> fieldsUpdaters = new HashMap<>();
        fieldsUpdaters.put("annotation",
                (event, eventForUpdate) -> event.setAnnotation(eventForUpdate.getAnnotation()));
        fieldsUpdaters.put("description",
                (event, eventForUpdate) -> event.setDescription(eventForUpdate.getDescription()));
        fieldsUpdaters.put("paid",
                ((event, eventForUpdate) -> event.setPaid(eventForUpdate.getPaid())));
        fieldsUpdaters.put("participantLimit",
                ((event, eventForUpdate) -> event.setParticipantLimit(eventForUpdate.getParticipantLimit())));
        fieldsUpdaters.put("requestModeration",
                ((event, eventForUpdate) -> event.setRequestModeration(eventForUpdate.getRequestModeration())));
        fieldsUpdaters.put("title",
                ((event, eventForUpdate) -> event.setTitle(eventForUpdate.getTitle())));

        fieldsUpdaters.forEach((field, updater) -> {
            switch (field) {
                case "annotation":
                    if (newEvent.getAnnotation() != null) updater.accept(savedEvent, newEvent);
                    break;
                case "description":
                    if (newEvent.getDescription() != null) updater.accept(savedEvent, newEvent);
                    break;
                case "paid":
                    if (newEvent.getPaid() != null) updater.accept(savedEvent, newEvent);
                    break;
                case "participantLimit":
                    if (newEvent.getParticipantLimit() != null) updater.accept(savedEvent, newEvent);
                    break;
                case "requestModeration":
                    if (newEvent.getRequestModeration() != null) updater.accept(savedEvent, newEvent);
                    break;
                case "title":
                    if (newEvent.getTitle() != null) updater.accept(savedEvent, newEvent);
                    break;
            }
        });
    }

    private User getUserIfExists(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, USER, userId
                ))
        );
    }

    private Category getCategoryIfExists(long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, CATEGORY, catId)
                ));
    }

    private void validatedEventDate(LocalDateTime actualDateTime) {
        if (actualDateTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectDateTime("Event date can't be earlier than two hours from the current moment");
        }
    }

    private Event getEventIfExists(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, EVENT, eventId
                )));
    }
}