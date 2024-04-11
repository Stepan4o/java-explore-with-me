package ru.practicum.explore_with_me.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.dto.NewCompilationDto;
import ru.practicum.explore_with_me.compilation.dto.UpdateCompilationRequest;
import ru.practicum.explore_with_me.compilation.mapper.CompilationMapper;
import ru.practicum.explore_with_me.compilation.model.Compilation;
import ru.practicum.explore_with_me.compilation.repository.CompilationRepository;
import ru.practicum.explore_with_me.event.dto.EventShortDto;
import ru.practicum.explore_with_me.event.mapper.EventMapper;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.explore_with_me.utils.Const.COMPILATION;
import static ru.practicum.explore_with_me.utils.Const.ENTITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public CompilationDto addNewCompilation(NewCompilationDto newCompilationDto) {
        List<Event> eventsForCompilation = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        Compilation compilation = compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto));

        return CompilationMapper.toDto(compilation, eventsForCompilation.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList())
        );
    }

    @Override
    public CompilationDto updateCompilationInfo(long compId, UpdateCompilationRequest updateRequest) {
        Compilation savedComp = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, COMPILATION, compId
                ))
        );
        List<Event> savedEventsForUpdate = eventRepository.findAllByIdIn(updateRequest.getEvents());
        savedComp.setEvents(savedEventsForUpdate.stream()
                .map(Event::getId)
                .collect(Collectors.toList())
        );
        if (updateRequest.getTitle() != null) {
            savedComp.setTitle(updateRequest.getTitle());
        }
        savedComp.setPinned(updateRequest.getPinned());

        return CompilationMapper.toDto(compilationRepository.save(savedComp), savedEventsForUpdate.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(long compilationId) {
        Compilation savedCompilation = compilationRepository.findById(compilationId).orElseThrow(
                () -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, COMPILATION, compilationId
                ))
        );
        List<Event> compilationEvents = eventRepository.findAllByIdIn(savedCompilation.getEvents());
        return CompilationMapper.toDto(savedCompilation, compilationEvents.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList())
        );
    }

    @Override
    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.findAllByPinnedEquals(pinned, pageable);
        List<CompilationDto> res = new ArrayList<>();
        for (Compilation compilation : compilations) {
            List<EventShortDto> events = eventRepository.findAllByIdIn(compilation.getEvents()).stream()
                    .map(EventMapper::toShortDto)
                    .collect(Collectors.toList());
            res.add(CompilationMapper.toDto(compilation, events));
        }

        return res;
    }

    @Override
    public void deleteCompilationById(long compId) {
        if (compilationRepository.existsById(compId)) compilationRepository.deleteById(compId);
        else throw new NotFoundException(
                String.format(ENTITY_NOT_FOUND, COMPILATION, compId)
        );

//        compilationRepository.deleteById(compId);
    }
}
