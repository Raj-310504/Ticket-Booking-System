package com.example.TicketBooking.repository;

import com.example.TicketBooking.entity.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, Long> {

    @Query(value = """
    SELECT ts.*
    FROM train_schedule ts
    JOIN train t ON ts.train_id = t.id
    JOIN station ss ON t.source_station_id = ss.id
    JOIN station ds ON t.destination_station_id = ds.id
    WHERE ss.id = :sourceStationId
    AND ds.id = :destinationStationId
    AND ts.departure_date_time BETWEEN :startOfDay AND :endOfDay
    ORDER BY ts.departure_date_time
    """, nativeQuery = true)
    List<TrainSchedule> searchSchedules(
            Long sourceStationId,
            Long destinationStationId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );
}
