package ru.practicum.explore_with_me.comment.repository;

import io.micrometer.core.lang.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore_with_me.comment.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndEventId(long commentId, long eventId);

    Optional<Comment> findByIdAndAuthorId(long commentId, long userId);

    @Query("SELECT c FROM Comment AS c " +
            "WHERE 1=1 " +
            "AND (:text IS NULL OR LOWER(c.text) LIKE concat('%',:text,'%')) " +
            "AND (:eventId IS NULL OR c.event.id = :eventId)")
    List<Comment> searchByText(@Nullable String text, @Nullable Long eventId, Pageable pageable);

    List<Comment> findAllByEventId(long eventId, Pageable pageable);
}
