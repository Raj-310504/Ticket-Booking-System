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
}
