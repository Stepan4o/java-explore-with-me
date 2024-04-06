package ru.practicum.explore_with_me.request.service;

import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;

public interface RequestService {
    ParticipationRequestDto add(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);
}
