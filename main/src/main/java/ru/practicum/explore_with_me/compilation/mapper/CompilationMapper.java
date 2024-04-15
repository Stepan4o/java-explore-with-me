package ru.practicum.explore_with_me.compilation.mapper;


import lombok.experimental.UtilityClass;
import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.dto.NewCompilationDto;
import ru.practicum.explore_with_me.compilation.model.Compilation;
import ru.practicum.explore_with_me.event.dto.EventShortDto;

import java.util.List;

@UtilityClass
public class CompilationMapper {
    public Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return new Compilation(
                newCompilationDto.isPinned(),
                newCompilationDto.getTitle()
        );
    }

    public CompilationDto toDto(Compilation compilation, List<EventShortDto> eventsShortDto) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setEvents(eventsShortDto);
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setPinned(compilation.getPinned());

        return compilationDto;
    }
}
