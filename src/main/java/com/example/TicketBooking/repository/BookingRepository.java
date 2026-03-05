package com.example.TicketBooking.repository;

import com.example.TicketBooking.entity.Booking;
import com.example.TicketBooking.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByPnrNumber(String pnrNumber);
    long countByStatus(BookingStatus status);

    Page<Booking> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query(value = """
            SELECT *
            FROM bookings b
            WHERE lower(b.pnr_number) = lower(:pnrNumber)
              AND b.user_id = :userId
            """, nativeQuery = true)
    Optional<Booking> findByPnrNumberAndUserId(@Param("pnrNumber") String pnrNumber, @Param("userId") Long userId);

    @Query(value = """
            SELECT *
            FROM bookings b
            WHERE b.id = :bookingId
              AND b.user_id = :userId
            FOR UPDATE
            """, nativeQuery = true)
    Optional<Booking> findByIdAndUserIdForUpdate(@Param("bookingId") Long bookingId, @Param("userId") Long userId);
}
