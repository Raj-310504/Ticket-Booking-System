package com.example.TicketBooking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTrainScheduleResponse {
    private String message;
    private Long id;
    private Long trainId;
    private String trainNumber;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
    private int availableSeats;
}
