package ru.practicum.ewm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<StatModel, Long> {

    @Query("select new ru.practicum.ewm.StatResponseDto(eh.app, eh.uri, COUNT(eh.ip)) from StatModel as eh " +
            "where eh.uri in :uri and eh.timestamp between :start and :end " +
            "group by eh.app " +
            "order by count(eh.ip) desc")
    List<StatResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uri);

    @Query("select new ru.practicum.ewm.StatResponseDto(eh.app, eh.uri, COUNT(distinct eh.ip)) from StatModel as eh " +
            "where eh.uri in :uri and eh.timestamp between :start and :end " +
            "group by eh.uri, eh.uri " +
            "order by count (distinct eh.ip) desc")
    List<StatResponseDto> getStatsWithUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uri);

    @Query("select new ru.practicum.ewm.StatResponseDto(eh.app, eh.uri, COUNT(distinct eh.ip)) from StatModel as eh " +
            "where eh.timestamp between :start and :end " +
            "group by eh.uri, eh.uri " +
            "order by count (distinct eh.ip) desc")
    List<StatResponseDto> getStatsWithoutUri(LocalDateTime start, LocalDateTime end);
}
