package ru.practicum.explore_with_me.event.service;

import ru.practicum.explore_with_me.event.dto.AdminSearchEventsParams;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.event.dto.UpdateEventRequest;
import ru.practicum.explore_with_me.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore_with_me.request.dto.EventRequestStatusUpdateResult;

import java.util.List;

public interface EventService {
    EventFullDto add(NewEventDto newEventDto, Long userId);

    List<EventFullDto> getFullInfo(AdminSearchEventsParams params);

    EventFullDto updateAdminInfo(long eventId, UpdateEventRequest requestForUpdate);

    EventFullDto updateUserInfo(long userId, long eventId, UpdateEventRequest requestForUpdate);

    EventRequestStatusUpdateResult updateRequestsStatus(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest request
    );
}