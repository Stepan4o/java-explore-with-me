package ru.practicum.explore_with_me.event.service;

import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.EventShortDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.event.dto.UpdateEventRequest;
import ru.practicum.explore_with_me.event.dto.search.AdminSearchEventsParams;
import ru.practicum.explore_with_me.event.dto.search.PublicSearchEventsParams;

import java.util.List;

public interface EventService {
    EventFullDto add(NewEventDto newEventDto, Long userId);

    List<EventFullDto> getFullInfo(AdminSearchEventsParams params);

    EventFullDto updateAdminInfo(long eventId, UpdateEventRequest requestForUpdate);

    EventFullDto updateUserInfo(long userId, long eventId, UpdateEventRequest requestForUpdate);

    // не нужен тут
//    EventRequestStatusUpdateResult updateRequestsStatus(
//            long userId,
//            long eventId,
//            EventRequestStatusUpdateRequest request
//    );
    List<EventShortDto> searchEventsByPublicParams(PublicSearchEventsParams params);
    EventFullDto getEventById(long eventId, String ip);
}