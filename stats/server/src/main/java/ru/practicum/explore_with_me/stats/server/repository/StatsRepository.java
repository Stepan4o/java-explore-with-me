package ru.practicum.explore_with_me.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore_with_me.stats.dto.ViewStatsDto;
import ru.practicum.explore_with_me.stats.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.explore_with_me.stats.dto.ViewStatsDto(app, uri, COUNT(DISTINCT ip)) " +
            "FROM EndpointHit " +
            "WHERE uri IN (?1) AND timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY app, uri " +
            "ORDER BY COUNT(DISTINCT ip) DESC")
    List<ViewStatsDto> findAllUniqueIpWithUris(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.explore_with_me.stats.dto.ViewStatsDto(app, uri, COUNT(DISTINCT ip)) " +
            "FROM EndpointHit " +
            "WHERE timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY app, uri " +
            "ORDER BY COUNT(DISTINCT ip) DESC")
    List<ViewStatsDto> findAllUniqueIpWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.explore_with_me.stats.dto.ViewStatsDto(app, uri, COUNT(uri)) " +
            "FROM EndpointHit " +
            "WHERE uri IN (?1) AND timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY app, uri " +
            "ORDER BY COUNT(uri) DESC")
    List<ViewStatsDto> findAllWithUris(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.explore_with_me.stats.dto.ViewStatsDto(app, uri, COUNT(uri)) " +
            "FROM EndpointHit " +
            "WHERE timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY app, uri " +
            "ORDER BY COUNT (uri) DESC")
    List<ViewStatsDto> findAllWithoutUris(LocalDateTime start, LocalDateTime end);
}
