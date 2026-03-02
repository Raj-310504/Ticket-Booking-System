package com.example.TicketBooking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Pattern(
            regexp = "^[A-Za-z]+\\d+$",
            message = "Preferred coach must be in format like D3"
    )
    private String preferredCoach;

    @NotEmpty(message = "At least one passenger is required")
    @Valid
    private List<CreateBookingPassengerRequest> passengers;
}
