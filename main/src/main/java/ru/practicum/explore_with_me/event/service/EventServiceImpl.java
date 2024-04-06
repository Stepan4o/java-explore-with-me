package ru.practicum.explore_with_me.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.category.repository.CategoryRepository;
import ru.practicum.explore_with_me.event.dto.AdminSearchEventsParams;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.event.dto.UpdateEventRequest;
import ru.practicum.explore_with_me.event.mapper.EventMapper;
import ru.practicum.explore_with_me.event.model.AdminStateAction;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.UserStateAction;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.exception.ConflictException;
import ru.practicum.explore_with_me.exception.InvalidEventDateException;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.location.Location;
import ru.practicum.explore_with_me.location.LocationMapper;
import ru.practicum.explore_with_me.location.LocationRepository;
import ru.practicum.explore_with_me.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore_with_me.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.mapper.RequestMapper;
import ru.practicum.explore_with_me.request.model.ParticipationRequest;
import ru.practicum.explore_with_me.request.model.RequestStatus;
import ru.practicum.explore_with_me.request.repository.RequestRepository;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static ru.practicum.explore_with_me.event.model.AdminStateAction.PUBLISH_EVENT;
import static ru.practicum.explore_with_me.event.model.AdminStateAction.REJECT_EVENT;
import static ru.practicum.explore_with_me.event.model.State.*;
import static ru.practicum.explore_with_me.event.model.UserStateAction.CANCEL_REVIEW;
import static ru.practicum.explore_with_me.event.model.UserStateAction.SEND_TO_REVIEW;
import static ru.practicum.explore_with_me.request.model.RequestStatus.CONFIRMED;
import static ru.practicum.explore_with_me.request.model.RequestStatus.REJECTED;
import static ru.practicum.explore_with_me.utils.Const.*;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;

    @Override
    public EventFullDto add(NewEventDto newEventDto, Long userId) {
        validatedEventDate(newEventDto.getEventDate());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, USER, userId
                )));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, CATEGORY, newEventDto.getCategory()
                )));

        Location location = locationRepository.save(LocationMapper.toLocation(newEventDto.getLocation()));

        Event event = EventMapper.toEvent(newEventDto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setLocation(location);
        event.setState(PENDING);
        event.setCreatedOn(LocalDateTime.now());
        return EventMapper.toEventFullDto(eventRepository.save(event), 0);
    }

    @Override
    public List<EventFullDto> getFullInfo(AdminSearchEventsParams params) {

        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        Specification<Event> spec = Specification.where(null);
        if (params.getRangeStart() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), params.getRangeStart()));
        }
        if (params.getRangeEnd() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), params.getRangeEnd()));
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
        Event savedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, EVENT, eventId
                )));

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

                // TODO нужно корректное сообщение написать
            else throw new ConflictException("eventDate cant be before then after one hour of start event");
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
    public EventFullDto updateUserInfo(long userId, long eventId, UpdateEventRequest requestForUpdate) {
        Event savedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, EVENT, eventId
                )));
        if (savedEvent.getInitiator().getId() != userId) {
            throw new ConflictException("Owner only can update event info");
        }

        //TODO зарефакторить
        if (savedEvent.getRequestModeration()) {
            if (requestForUpdate.getStateAction() != null) {
                UserStateAction action = UserStateAction.toEnum(requestForUpdate.getStateAction());
                if (action.equals(SEND_TO_REVIEW)) savedEvent.setState(PENDING);
                if (action.equals(CANCEL_REVIEW)) savedEvent.setState(CANCELED);
            }
        }
        //TODO зарефакторить
        if (!savedEvent.getRequestModeration() && savedEvent.getState() != PUBLISHED) {
            if (requestForUpdate.getStateAction() != null) {
                UserStateAction action = UserStateAction.toEnum(requestForUpdate.getStateAction());
                if (action.equals(SEND_TO_REVIEW)) savedEvent.setState(PENDING);
                if (action.equals(CANCEL_REVIEW)) savedEvent.setState(CANCELED);
            }
        }

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
            if (actualEventTime.plusHours(2).isAfter(requestForUpdate.getEventDate()) ||
                    actualEventTime.plusHours(2) != requestForUpdate.getEventDate())
                savedEvent.setEventDate(requestForUpdate.getEventDate());
                // TODO нужно корректное сообщение написать
            else throw new ConflictException("eventDate cant be before then after two hours of start event");
        }


        updateEventFields(savedEvent, requestForUpdate);

        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest request
    ) {
        Event savedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, EVENT, eventId
                )));
        long requestLimit = requestRepository.countByEventIdAndStatus(eventId, CONFIRMED);
        if (savedEvent.getParticipantLimit() <= requestLimit && savedEvent.getParticipantLimit() > 0) {
            throw new ConflictException("participant limit was reached");
        }
        List<ParticipationRequest> requests = requestRepository.findAllByEventIdAndIdIn(
                eventId,
                request.getRequestIds()
        );
        RequestStatus newStatus = RequestStatus.convertStatus(request.getStatus());
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        for (ParticipationRequest req : requests) {
            if (req.getStatus() != RequestStatus.PENDING) throw new ConflictException(
                    "Заявка долбжна быть в статусе PENDING"
            );
            if (requestLimit < savedEvent.getParticipantLimit()) {
                req.setStatus(newStatus);
                if (newStatus == CONFIRMED) {
                    confirmedRequests.add(RequestMapper.toDto(req));
                } else if (newStatus == REJECTED) {
                    rejectedRequests.add(RequestMapper.toDto(req));
                }
                requestLimit++;
            } else {
                req.setStatus(REJECTED);
                rejectedRequests.add(RequestMapper.toDto(req));
            }
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
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

    private void validatedEventDate(LocalDateTime actualDateTime) {
        if (actualDateTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidEventDateException(actualDateTime);
        }
    }
}
