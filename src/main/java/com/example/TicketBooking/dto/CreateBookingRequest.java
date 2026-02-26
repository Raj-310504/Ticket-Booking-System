package com.example.TicketBooking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    @NotNull(message = "Train schedule id is required")
    private Long trainScheduleId;

    @NotNull(message = "Journey date is required")
    @FutureOrPresent(message = "Journey date must be today or a future date")
    private LocalDate journeyDate;

    @NotNull(message = "Fare per passenger is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Fare per passenger must be greater than zero")
    private BigDecimal farePerPassenger;

    @NotEmpty(message = "At least one passenger is required")
    @Valid
    private List<CreateBookingPassengerRequest> passengers;
}
