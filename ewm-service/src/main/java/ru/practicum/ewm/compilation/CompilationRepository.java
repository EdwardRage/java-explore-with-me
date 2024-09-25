package ru.practicum.ewm.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("select comp from Compilation as comp " +
            "left join fetch comp.events as ev " +
            "left join fetch ev.initiator as init " +
            "left join fetch ev.category as cat " +
            "where comp.pinned = :pinned")
    List<Compilation> findByPinned(boolean pinned);

    @Query("select comp from Compilation as comp " +
            "left join fetch comp.events as ev " +
            "left join fetch ev.initiator as init " +
            "left join fetch ev.category as cat ")
    List<Compilation> findAllCompilations();

    @Query("select comp from Compilation as comp " +
            "left join fetch comp.events as ev " +
            "left join fetch ev.initiator as init " +
            "left join fetch ev.category as cat " +
            "where comp.id = :id")
    Optional<Compilation> findByCompId(long id);

}
