package ru.practicum.explore_with_me.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.service.RequestService;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestControllerPrivate {
    private final RequestService requestService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addNewRequest(
            @PathVariable @Min(1) Long userId,
            @RequestParam @Min(1) Long eventId
    ) {
        log.info("POST: /users/{}/requests?eventId={}", userId, eventId);
        return requestService.add(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long requestId
    ) {
        log.info("PATCH: /users/{}/requests/{}/cancel", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getOwnerRequests(
            @PathVariable @Min(1) Long userId
    ) {
        log.debug("GET: /users/{}/requests", userId);
        return requestService.getOwnerRequests(userId);
    }
}
