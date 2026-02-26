package com.example.TicketBooking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class CreateTrainScheduleRequest {
    @NotNull(message = "Train id is required")
    private Long trainId;

    @NotNull(message = "Departure time is required")
    private LocalDateTime departureDateTime;

    @NotNull(message = "Arrival time is required")
    private LocalDateTime arrivalDateTime;

    @Min(value = 0, message = "Available seats cannot be negative")
    private int availableSeats;
}
