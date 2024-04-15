package ru.practicum.explore_with_me.request.service;

import ru.practicum.explore_with_me.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore_with_me.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto add(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);

    EventRequestStatusUpdateResult updateRequestsStatus(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest request
    );

    List<ParticipationRequestDto> getOwnerRequests(long userId);

    List<ParticipationRequestDto> getOwnerEventRequests(long userId, long eventId);
}
