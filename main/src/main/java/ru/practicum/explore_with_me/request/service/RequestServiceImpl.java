package ru.practicum.explore_with_me.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.State;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.exception.ConflictException;
import ru.practicum.explore_with_me.exception.NotFoundException;
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
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.explore_with_me.request.model.RequestStatus.*;
import static ru.practicum.explore_with_me.request.model.RequestStatus.REJECTED;
import static ru.practicum.explore_with_me.utils.Const.*;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto add(long userId, long eventId) {
        User savedUser = getUserIfExists(userId);
        Event savedEvent = getEventIfExists(eventId);

        if (eventRepository.existsByIdAndInitiatorId(eventId, userId))
            throw new ConflictException("Initiator can't send request for own event");

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId))
            throw new ConflictException("Request already exists");

        if (!eventRepository.existsByIdAndState(eventId, State.PUBLISHED))
            throw new ConflictException("Event is not published yet");

        int limit = savedEvent.getParticipantLimit();
        if (limit != 0 && limit == requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED))
            throw new ConflictException("Participant limit is already reached");

        ParticipationRequest request = new ParticipationRequest();

        if (savedEvent.getRequestModeration() && savedEvent.getParticipantLimit() != 0) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        request.setRequester(savedUser);
        request.setEvent(savedEvent);
        request.setCreated(LocalDateTime.now());

        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest request
    ) {
        Event savedEvent = getEventIfExists(eventId);
        long currentCount = requestRepository.countByEventIdAndStatus(eventId, CONFIRMED);
        if (savedEvent.getParticipantLimit() <= currentCount && savedEvent.getParticipantLimit() > 0) {
            throw new ConflictException("Participant limit was reached");
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
                    "Request should be pending"
            );
            if (currentCount < savedEvent.getParticipantLimit()) {
                req.setStatus(newStatus);
                if (newStatus == CONFIRMED) {
                    confirmedRequests.add(RequestMapper.toDto(req));
                    savedEvent.setConfirmedRequest(savedEvent.getConfirmedRequest() + 1);
                } else if (newStatus == REJECTED) {
                    rejectedRequests.add(RequestMapper.toDto(req));
                }
                currentCount++;
            } else {
                req.setStatus(REJECTED);
                rejectedRequests.add(RequestMapper.toDto(req));
            }
            eventRepository.save(savedEvent);
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Override
    public List<ParticipationRequestDto> getOwnerRequests(long userId) {
        User owner = getUserIfExists(userId);
        return requestRepository.findAllByRequesterId(owner.getId()).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getOwnerEventRequests(long userId, long eventId) {
        User owner = getUserIfExists(userId);
        Event ownerEvent = eventRepository.findByIdAndInitiatorId(eventId, owner.getId())
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, EVENT, eventId
                )));

        return requestRepository.findAllByEventId(ownerEvent.getId()).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        ParticipationRequest savedRequest = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, REQUEST, requestId
                )));
        savedRequest.setStatus(CANCELED);
        requestRepository.save(savedRequest);
        return RequestMapper.toRequestDto(savedRequest);
    }

    private User getUserIfExists(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, USER, userId
                )));
    }

    private Event getEventIfExists(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, EVENT, eventId
                )));
    }
}

