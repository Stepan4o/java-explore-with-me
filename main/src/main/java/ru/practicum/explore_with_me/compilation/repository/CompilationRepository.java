package ru.practicum.explore_with_me.compilation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore_with_me.compilation.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
//    @Query("SELECT c FROM Compilation c WHERE 1 = 1 " +
//            "AND (:pinned IS NULL OR c.pinned = :pinned)")
    List<Compilation> findAllByPinnedEquals(boolean pinned, Pageable pageable);
//    @Query("SELECT c FROM Compilation c WHERE 1 = 1 " +
//            "AND (:pinned IS NULL OR c.pinned = :pinned)")
//    List<Compilation> findAllByPinnedEquals(Boolean pinned, Pageable pageable);
}
