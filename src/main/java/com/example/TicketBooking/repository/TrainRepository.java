package com.example.TicketBooking.repository;

import com.example.TicketBooking.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainRepository extends JpaRepository<Train, Long> {
    boolean existsByTrainNumber(String trainNumber);
}
