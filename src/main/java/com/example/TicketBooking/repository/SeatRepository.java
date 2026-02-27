package com.example.TicketBooking.repository;

import com.example.TicketBooking.entity.Seat;
import com.example.TicketBooking.enums.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query(value = """
            SELECT *
            FROM seat s
            WHERE s.schedule_id = :scheduleId
              AND s.status = :status
              AND UPPER(s.coach_number) = UPPER(:coachNumber)
            ORDER BY regexp_replace(s.coach_number, '\\d', '', 'g'),
                     CAST(regexp_replace(s.coach_number, '\\D', '', 'g') AS INTEGER),
                     CAST(regexp_replace(s.seat_number, '\\D', '', 'g') AS INTEGER)
            FOR UPDATE SKIP LOCKED
            LIMIT :limitCount
            """, nativeQuery = true)
    List<Seat> findAvailableSeatsByScheduleAndCoachForUpdate(@Param("scheduleId") Long scheduleId,
                                                             @Param("status") String status,
                                                             @Param("coachNumber") String coachNumber,
                                                             @Param("limitCount") int limitCount);

    @Query(value = """
            SELECT *
            FROM seat s
            WHERE s.schedule_id = :scheduleId
              AND s.status = :status
            ORDER BY regexp_replace(s.coach_number, '\\d', '', 'g'),
                     CAST(regexp_replace(s.coach_number, '\\D', '', 'g') AS INTEGER),
                     CAST(regexp_replace(s.seat_number, '\\D', '', 'g') AS INTEGER)
            FOR UPDATE SKIP LOCKED
            LIMIT :limitCount
            """, nativeQuery = true)
    List<Seat> findAvailableSeatsByScheduleForUpdate(@Param("scheduleId") Long scheduleId,
                                                      @Param("status") String status,
                                                      @Param("limitCount") int limitCount);

    @Query(value = """
            SELECT *
            FROM seat s
            WHERE s.schedule_id = :scheduleId
              AND s.status = :status
              AND s.id NOT IN (:excludedSeatIds)
            ORDER BY regexp_replace(s.coach_number, '\\d', '', 'g'),
                     CAST(regexp_replace(s.coach_number, '\\D', '', 'g') AS INTEGER),
                     CAST(regexp_replace(s.seat_number, '\\D', '', 'g') AS INTEGER)
            FOR UPDATE SKIP LOCKED
            LIMIT :limitCount
            """, nativeQuery = true)
    List<Seat> findAvailableSeatsByScheduleExcludingIdsForUpdate(@Param("scheduleId") Long scheduleId,
                                                                  @Param("status") String status,
                                                                  @Param("excludedSeatIds") List<Long> excludedSeatIds,
                                                                  @Param("limitCount") int limitCount);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM Passenger p
            JOIN p.seat s
            WHERE p.booking.id = :bookingId
            AND p.booking.status = :status
            """)
    List<Seat> findBookedSeatsByBookingIdForUpdate(@Param("bookingId") Long bookingId,
                                                   @Param("status") com.example.TicketBooking.enums.BookingStatus status);

    long countByTrainScheduleIdAndStatus(Long trainScheduleId, SeatStatus status);
}
