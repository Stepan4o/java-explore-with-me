package ru.practicum.explore_with_me.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.State;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByIdAndInitiatorId(long eventId, long userId);

    boolean existsByIdAndState(long eventId, State state);

    List<Event> findAll(Specification<Event> specification, Pageable pageable);
}
