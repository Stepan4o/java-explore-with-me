package ru.practicum.explore_with_me.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.dto.NewCompilationDto;
import ru.practicum.explore_with_me.compilation.dto.UpdateCompilationRequest;
import ru.practicum.explore_with_me.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationControllerAdmin {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addNewCompilation(
            @RequestBody @Valid NewCompilationDto compilationDto
    ) {
        log.info("POST: /admin/compilations");
        return compilationService.addNewCompilation(compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationById(
            @PathVariable @Min(1) Long compId
    ) {
        log.info("DELETE: /admin/compilations/{}", compId);
        compilationService.deleteCompilationById(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilationInfo(
            @RequestBody @Valid UpdateCompilationRequest updateRequest,
            @PathVariable @Min(1) Long compId
    ) {
        log.info("PATCH: /admin/compilations/{}", compId);
        return compilationService.updateCompilationInfo(compId, updateRequest);
    }
}
