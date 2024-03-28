package ru.practicum.explore_with_me.stats.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.stats.dto.EndpointHitDto;
import ru.practicum.explore_with_me.stats.dto.ViewStatsDto;
import ru.practicum.explore_with_me.stats.server.exception.InvalidDateTimeException;
import ru.practicum.explore_with_me.stats.server.service.StatsServiceImpl;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsServiceImpl service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@Valid @RequestBody EndpointHitDto hitDto) {
        log.info("POST: /hit ");
        service.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getViewStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {
        log.info("GET: /stats ");
        if (start.isAfter(end)) throw new InvalidDateTimeException();

        return service.getStats(start, end, uris, unique);
    }
}
