package com.example.TicketBooking.repository;

import com.example.TicketBooking.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {
    boolean existsByStationCode(String stationCode);
}
