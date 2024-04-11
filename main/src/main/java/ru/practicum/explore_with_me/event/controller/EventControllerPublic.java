package ru.practicum.explore_with_me.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.EventShortDto;
import ru.practicum.explore_with_me.event.dto.search.PublicSearchEventsParams;
import ru.practicum.explore_with_me.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.explore_with_me.stats.dto.consts.Constants.TIME_PATTERN;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventControllerPublic {
    private final EventService eventService;
    private final HttpServletRequest request;

    @GetMapping
    public List<EventShortDto> searchEventsByPublicParams(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size
    ) {
        log.info("GET: /events&text={}&categories={}&paid={}" +
                        "&rangeStart={}&rangeEnd={}&onlyAvailable={}&sort={}&from={}&size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        PublicSearchEventsParams params = PublicSearchEventsParams.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
//                .rangeStart(LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(TIME_PATTERN)))
//                .rangeEnd(LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(TIME_PATTERN)))
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .build();

        return eventService.searchEventsByPublicParams(params);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable @Min(1) Long eventId) {
        log.info("GET: /events/{}", eventId);
        return eventService.getEventById(eventId, request.getRemoteAddr());
    }
}
