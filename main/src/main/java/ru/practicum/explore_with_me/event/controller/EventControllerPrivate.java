package ru.practicum.explore_with_me.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.EventShortDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.event.dto.UpdateEventRequest;
import ru.practicum.explore_with_me.event.service.EventService;
import ru.practicum.explore_with_me.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore_with_me.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventControllerPrivate {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addNewEvent(
            @PathVariable @Min(1) Long userId,
            @RequestBody @Valid NewEventDto newEventDto
    ) {
        log.info("POST: /users/{}/events", userId);
        return eventService.add(newEventDto, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateOwnerEventById(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long eventId,
            @RequestBody @Valid UpdateEventRequest requestForUpdate
    ) {
        log.info("PATCH: /users/{}/events/{}", userId, eventId);
        return eventService.updateOwnerEvent(userId, eventId, requestForUpdate);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsStatusByOwner(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long eventId,
            @RequestBody EventRequestStatusUpdateRequest request
    ) {
        log.info("GET: /users/{}/events/{}/requests", userId, eventId);
        return requestService.updateRequestsStatus(userId, eventId, request);
    }

    @GetMapping
    public List<EventShortDto> getOwnerEvents(
            @PathVariable @Min(1) Long userId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size
    ) {
        log.debug("GET: /users/{}/events?from={}&size={}", userId, from, size);
        return eventService.getOwnerEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getOwnerEventById(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long eventId
    ) {
        log.debug("GET: /users/{}/events/{}", userId, eventId);
        return eventService.getOwnerEventById(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getOwnerEventRequests(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long eventId
    ) {
        log.debug("GET: /users/{}/events/{}/requests", userId, eventId);
        return requestService.getOwnerEventRequests(userId, eventId);
    }
}
