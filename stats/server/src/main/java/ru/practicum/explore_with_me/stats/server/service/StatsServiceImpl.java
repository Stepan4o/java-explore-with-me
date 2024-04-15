package ru.practicum.explore_with_me.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.stats.dto.EndpointHitDto;
import ru.practicum.explore_with_me.stats.dto.ViewStatsDto;
import ru.practicum.explore_with_me.stats.server.mapper.HitMapper;
import ru.practicum.explore_with_me.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Override
    public void saveHit(EndpointHitDto hitDto) {
        repository.save(HitMapper.toHit(hitDto));
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            if (uris == null)
                return repository.findAllUniqueIpWithoutUris(start, end);
            else
                return repository.findAllUniqueIpWithUris(uris, start, end);
        } else {
            if (uris == null)
                return repository.findAllWithoutUris(start, end);
            else
                return repository.findAllWithUris(uris, start, end);
        }
    }
}
