package com.example.TicketBooking.repository;

import com.example.TicketBooking.entity.Passenger;
import com.example.TicketBooking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    @Query("""
            SELECT p.seatNumber
            FROM Passenger p
            WHERE p.booking.trainSchedule.id = :scheduleId
            AND p.booking.status = :status
            """)
    List<String> findSeatNumbersByScheduleIdAndBookingStatus(@Param("scheduleId") Long scheduleId,
                                                             @Param("status") BookingStatus status);

    @Query("""
            SELECT COUNT(p)
            FROM Passenger p
            WHERE p.booking.trainSchedule.id = :scheduleId
            AND p.booking.status = :status
            AND p.seat IS NULL
            AND p.seatNumber LIKE 'RAC-%'
            """)
    long countRacPassengersByScheduleAndBookingStatus(@Param("scheduleId") Long scheduleId,
                                                      @Param("status") BookingStatus status);

    @Query(value = """
            SELECT p.*
            FROM passenger p
            JOIN bookings b ON b.id = p.booking_id
            WHERE b.schedule_id = :scheduleId
              AND b.status = :status
              AND p.seat_id IS NULL
              AND p.seat_number LIKE 'RAC-%'
            ORDER BY b.created_at, p.id
            FOR UPDATE SKIP LOCKED
            LIMIT :limitCount
            """, nativeQuery = true)
    List<Passenger> findRacPassengersForUpdate(@Param("scheduleId") Long scheduleId,
                                               @Param("status") String status,
                                               @Param("limitCount") int limitCount);
}
