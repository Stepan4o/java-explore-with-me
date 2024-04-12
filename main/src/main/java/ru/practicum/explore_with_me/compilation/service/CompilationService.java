package ru.practicum.explore_with_me.compilation.service;

import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.dto.NewCompilationDto;
import ru.practicum.explore_with_me.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto addNewCompilation(NewCompilationDto compilationDto);

    CompilationDto updateCompilationInfo(long compilationId, UpdateCompilationRequest updateRequest);

    CompilationDto getCompilationById(long compilationId);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    void deleteCompilationById(long id);
}
