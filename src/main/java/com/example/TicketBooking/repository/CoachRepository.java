package com.example.TicketBooking.repository;

import com.example.TicketBooking.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachRepository extends JpaRepository<Coach, Long> {
}
