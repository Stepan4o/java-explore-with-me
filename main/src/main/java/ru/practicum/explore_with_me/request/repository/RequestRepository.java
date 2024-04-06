package ru.practicum.explore_with_me.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore_with_me.request.model.ParticipationRequest;
import ru.practicum.explore_with_me.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    boolean existsByEventIdAndRequesterId(long eventId, long userId);

    long countByEventIdAndStatus(long eventId, RequestStatus status);

    Optional<ParticipationRequest> findByIdAndRequesterId(long id, long requesterId);

    List<ParticipationRequest> findAllByEventIdAndIdIn(long eventId, List<Long> ids);
}
