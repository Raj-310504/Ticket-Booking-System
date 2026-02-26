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
public class TrainScheduleResponse {
    private Long id;
    private Long trainId;
    private String trainNumber;
    private String trainName;
    private Long sourceStationId;
    private String sourceStationName;
    private Long destinationStationId;
    private String destinationStationName;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
    private int availableSeats;
}
