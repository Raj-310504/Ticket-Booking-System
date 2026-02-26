package com.example.TicketBooking.repository;

import com.example.TicketBooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByPnrNumber(String pnrNumber);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Booking> findByIdAndUserId(Long bookingId, Long userId);
}
