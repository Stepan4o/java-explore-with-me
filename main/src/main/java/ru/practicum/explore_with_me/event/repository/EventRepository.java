package ru.practicum.explore_with_me.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.State;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByIdAndInitiatorId(long eventId, long userId);

    boolean existsByIdAndState(long eventId, State state);

    List<Event> findAll(Specification<Event> specification, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE 1=1 " +
            "AND :ids IS NULL OR e.id IN :ids")
    List<Event> findAllByIdIn(List<Long> ids);

    List<Event> findAllByInitiatorId(long ownerId, Pageable pageable);

    Optional<Event> findByIdAndState(long eventId, State state);

    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    boolean existsByCategoryId(long categoryId);
}
