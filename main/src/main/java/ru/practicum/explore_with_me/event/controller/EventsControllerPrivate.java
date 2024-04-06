package ru.practicum.explore_with_me.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.event.dto.UpdateEventRequest;
import ru.practicum.explore_with_me.event.service.EventService;
import ru.practicum.explore_with_me.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore_with_me.request.dto.EventRequestStatusUpdateResult;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventsControllerPrivate {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addNewEvent(
            @PathVariable Long userId,
            @RequestBody @Valid NewEventDto newEventDto
    ) {
        log.info("POST: /users/{}/events", userId);
        return eventService.add(newEventDto, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long eventId,
            @RequestBody @Valid UpdateEventRequest requestForUpdate
    ) {
        log.info("PATCH: /users/{}/events/{}", userId, eventId);
        return eventService.updateUserInfo(userId, eventId, requestForUpdate);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsStatus(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long eventId,
            @RequestBody EventRequestStatusUpdateRequest request
    ) {
        return eventService.updateRequestsStatus(userId, eventId, request);
    }

    @GetMapping
    public void getAllEvents() {

    }

    @GetMapping("/{eventId}")
    public void getEventById() {

    }

    @GetMapping("/{eventId}/requests")
    public void getEventRequests() {

    }
}
