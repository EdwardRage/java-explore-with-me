package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.user.User;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("select req from Request as req " +
            "join fetch req.user as us " +
            "where us.id = :userId")
    List<Request> findAllByRequesterId(long userId);

    @Query("select req from Request as req " +
            "join fetch req.user as us " +
            "join fetch req.event as ev " +
            "where req.id = :requestId")
    Optional<Request> findRequestById(Long requestId);

    List<Request> findRequestByEventId(long eventId);

    @Query("select req from Request as req " +
            "join fetch req.event as ev " +
            "join fetch req.user as us " +
            "where req.id in :requestIds and ev.id = :eventId " /*+
            "and req.status = ru.practicum.ewm.request.model.RequestStatus.PENDING"*/)
    List<Request> findRequests(List<Integer> requestIds, long eventId);

    Request findRequestByUser(User user);
}
