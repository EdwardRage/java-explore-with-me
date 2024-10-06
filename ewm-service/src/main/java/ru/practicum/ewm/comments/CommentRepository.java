package ru.practicum.ewm.comments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findByIdAndUserId(long commentId, long userId);

    @Query("select cm from Comment as cm " +
            "join fetch cm.user " +
            "join fetch cm.event as ev " +
            "join fetch ev.initiator " +
            "join fetch ev.category " +
            "where cm.id = :commentId")
    Optional<Comment> findByCommentId(long commentId);

    @Query("select cm from Comment as cm " +
            "join fetch cm.user " +
            "join fetch cm.event as ev " +
            "join fetch ev.initiator " +
            "join fetch ev.category " +
            "where ev.id = :eventId")
    List<Comment> findAllByEventId(long eventId);
}
