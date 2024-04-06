package ru.practicum.explore_with_me.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.State;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.exception.ConflictException;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.mapper.RequestMapper;
import ru.practicum.explore_with_me.request.model.ParticipationRequest;
import ru.practicum.explore_with_me.request.model.RequestStatus;
import ru.practicum.explore_with_me.request.repository.RequestRepository;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import java.time.LocalDateTime;

import static ru.practicum.explore_with_me.request.model.RequestStatus.CANCELED;
import static ru.practicum.explore_with_me.utils.Const.*;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto add(long userId, long eventId) {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, USER, userId
                )));
        Event savedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, EVENT, eventId
                )));
//         инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
//         возможно existsByIdAndEventUserId
        if (eventRepository.existsByIdAndInitiatorId(eventId, userId))
            throw new ConflictException("Initiator can't send request for own event");

        //  нельзя добавить повторный запрос (Ожидается код ошибки 409)
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId))
            throw new ConflictException("Request already exists");

//          нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
        if (!eventRepository.existsByIdAndState(eventId, State.PUBLISHED))
            throw new ConflictException("Event is not published yet");

//          если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
        int limit = savedEvent.getParticipantLimit();
        if (limit == 0 || limit == requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED))
            throw new ConflictException("Participant limit is already reached");

        ParticipationRequest request = new ParticipationRequest();
        request.setRequester(savedUser);
        request.setEvent(savedEvent);
        request.setCreated(LocalDateTime.now());

//  если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
        if (savedEvent.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        ParticipationRequest savedRequest = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, REQUEST, requestId
                )));
        savedRequest.setStatus(CANCELED);
        return RequestMapper.toRequestDto(savedRequest);
    }
}

