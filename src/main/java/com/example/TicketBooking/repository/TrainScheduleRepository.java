package com.example.TicketBooking.repository;

import com.example.TicketBooking.entity.TrainSchedule;
import com.example.TicketBooking.enums.ScheduleStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ts FROM TrainSchedule ts WHERE ts.id = :scheduleId")
    java.util.Optional<TrainSchedule> findByIdForUpdate(@Param("scheduleId") Long scheduleId);

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
    """,
            countQuery = """
    SELECT count(*)
    FROM train_schedule ts
    JOIN train t ON ts.train_id = t.id
    JOIN station ss ON t.source_station_id = ss.id
    JOIN station ds ON t.destination_station_id = ds.id
    WHERE ss.id = :sourceStationId
    AND ds.id = :destinationStationId
    AND ts.departure_date_time BETWEEN :startOfDay AND :endOfDay
    """,
            nativeQuery = true)
    Page<TrainSchedule> searchSchedules(
            Long sourceStationId,
            Long destinationStationId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            Pageable pageable
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE TrainSchedule ts
            SET ts.status = :completedStatus
            WHERE ts.departureDateTime < :now
            AND (ts.status IS NULL OR ts.status = :activeStatus)
            """)
    int markPastSchedulesAsCompleted(@Param("now") LocalDateTime now,
                                     @Param("activeStatus") ScheduleStatus activeStatus,
                                     @Param("completedStatus") ScheduleStatus completedStatus);
}
