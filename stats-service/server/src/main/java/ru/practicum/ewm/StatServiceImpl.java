package ru.practicum.ewm;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository hitRepository;
    private final StatMapper mapper;

    private final EntityManager entityManager;

    @Override
    public StatResponseDto create(StatRequestDto hitRequestDto) {

        LocalDateTime dateTime = encodeTime(hitRequestDto.getTimestamp());
        StatModel statModel = mapper.mapToEndpointHit(hitRequestDto, dateTime);
        hitRepository.save(statModel);
        StatModel statsFull = hitRepository.save(statModel);
        return mapper.mapToEndpointHitResponse(statsFull);
    }

    private LocalDateTime encodeTime(String timeStr) {
        String time = timeStr.replace(" ", "T");
        LocalDateTime dateTime = LocalDateTime.parse(time);
        return dateTime;
    }

    @Override
    public List<StatResponseDto> get(String start, String end, String[] uri, Boolean unique) {
        LocalDateTime startTime = encodeTime(start);
        LocalDateTime endTime = encodeTime(end);

        if (uri == null || uri.length == 0) {
            return hitRepository.getStatsWithoutUri(startTime, endTime);
        }
        List<String> urisList = Arrays.asList(uri);
        if (unique) {
            return hitRepository.getStatsWithUniqueIp(startTime, endTime, urisList);
        }
        return hitRepository.getStats(startTime, endTime, urisList);
    }


    @Override
    public List<StatResponseDto> getStat(String start, String end, String[] uris, Boolean unique) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<StatResponseDto> criteriaQuery = criteriaBuilder.createQuery(StatResponseDto.class);
        Root<StatModel> root = criteriaQuery.from(StatModel.class);

        Predicate predicate = criteriaBuilder.conjunction();

        LocalDateTime startTime;
        LocalDateTime endTime;

        if (start != null && end != null) {
            startTime = encodeTime(start);
            endTime = encodeTime(end);
        } else {
            throw new CustomDateTimeException("Start и end не должны быть пустыми");
        }

        if (startTime.isAfter(endTime)) {
            throw new CustomDateTimeException("Start должен быть раньше End");
        }

        predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get("timestamp"), startTime, endTime));

        if (uris != null) {
            List<String> urisList = Arrays.asList(uris);
            predicate = criteriaBuilder.and(predicate, root.get("uri").in(urisList));
        }

        if (unique) {
            criteriaBuilder.countDistinct(root.get("uri"));
            criteriaQuery.distinct(true);
        }

        criteriaQuery.groupBy(root.get("uri"), root.get("ip"));
        Expression<Long> hits;
        if (unique) {
            hits = criteriaBuilder.countDistinct(root.get("uri"));
        } else {
            hits = criteriaBuilder.count(root.get("uri"));
        }

        criteriaQuery.select(criteriaBuilder.construct(StatResponseDto.class,
                root.get("ip"),
                root.get("uri"),
                hits));

        criteriaQuery.where(predicate);
        criteriaQuery.orderBy(criteriaBuilder.desc(hits));
        return entityManager.createQuery(criteriaQuery).getResultList()
                .stream()
                .toList();
    }
}
