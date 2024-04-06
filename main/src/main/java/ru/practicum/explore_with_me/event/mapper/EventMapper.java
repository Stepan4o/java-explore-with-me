package ru.practicum.explore_with_me.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore_with_me.category.mapper.CategoryMapper;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.location.LocationMapper;
import ru.practicum.explore_with_me.user.mapper.UserMapper;

@UtilityClass
public class EventMapper {
    public EventFullDto toEventFullDto(Event event, long confirmedRequests) {
        return EventFullDto.builder()
                .id(event.getId())
                .description(event.getDescription())
                .annotation(event.getAnnotation())
                .category((CategoryMapper.toCategoryDto(event.getCategory())))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .description(event.getDescription())
                .annotation(event.getAnnotation())
                .category((CategoryMapper.toCategoryDto(event.getCategory())))
//                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .build();
    }

    public Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(LocationMapper.toLocation(newEventDto.getLocation()))
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .build();
    }
}
