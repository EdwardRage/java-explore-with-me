package ru.practicum.ewm.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.events.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e from Event as e " +
            "join fetch e.initiator as u " +
            "join fetch e.category " +
            "where e.id = :eventId")
    Optional<Event> findById(long eventId);

    @Query("select e from Event as e " +
            "join fetch e.initiator as u " +
            "join fetch e.category " +
            "where u.id = :initiatorId")
    List<Event> findAllByInitiatorId(long initiatorId);

    @Query("select e from Event as e " +
            "join fetch e.initiator as u " +
            "join fetch e.category " +
            "where u.id = :initiatorId and e.id = :eventId")
    Optional<Event> findAllByInitiatorIdAndEventId(long initiatorId, long eventId);

    @Query("select ev from Event as ev " +
            "join fetch ev.category as cat " +
            "join fetch ev.initiator as init " +
            "where ev.id in :eventList")
    List<Event> findAllEventsById(List<Integer> eventList);
}
