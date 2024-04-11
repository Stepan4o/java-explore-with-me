package ru.practicum.explore_with_me.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.service.CompilationService;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class CompilationControllerPublic {
    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(
            @PathVariable @Min(1) long compId
    ) {
        log.info("GET: /compilations/{}", compId);
        return compilationService.getCompilationById(compId);
    }

    @GetMapping
    public List<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size
    ) {
        log.info("GET: /compilations?pinned={}&from={}&size={}", pinned, from, size);
        return compilationService.getCompilations(pinned, from, size);
    }
}
