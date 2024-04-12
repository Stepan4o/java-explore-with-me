package ru.practicum.explore_with_me.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.UpdateEventRequest;
import ru.practicum.explore_with_me.event.dto.search.AdminSearchEventsParams;
import ru.practicum.explore_with_me.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.explore_with_me.stats.dto.consts.Constants.TIME_PATTERN;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class EventControllerAdmin {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getFullEventsInfoByAdminParams(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern =
                    TIME_PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern =
                    TIME_PATTERN) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size
    ) {
        log.debug("GET: /admin/events?users={}&states={}&categories={}&rangeStart={}&rangeEnd={}&from={}&size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        AdminSearchEventsParams params = AdminSearchEventsParams.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();

        return eventService.getFullInfoByAdminParams(params);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventInfoByAdmin(
            @PathVariable @Min(1) Long eventId,
            @RequestBody @Valid UpdateEventRequest requestForUpdate
    ) {
        log.debug("PATCH: /admin/events/{}", eventId);
        return eventService.updateAdminInfo(eventId, requestForUpdate);
    }
}
