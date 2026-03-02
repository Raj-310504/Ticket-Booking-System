package com.example.TicketBooking.repository;

import com.example.TicketBooking.entity.Booking;
import com.example.TicketBooking.enums.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByPnrNumber(String pnrNumber);
    long countByStatus(BookingStatus status);

    Page<Booking> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE lower(b.pnrNumber) = lower(:pnrNumber)
            AND b.user.id = :userId
            """)
    Optional<Booking> findByPnrNumberAndUserId(@Param("pnrNumber") String pnrNumber, @Param("userId") Long userId);

    Optional<Booking> findByIdAndUserId(Long bookingId, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.id = :bookingId
            AND b.user.id = :userId
            """)
    Optional<Booking> findByIdAndUserIdForUpdate(@Param("bookingId") Long bookingId, @Param("userId") Long userId);
}
