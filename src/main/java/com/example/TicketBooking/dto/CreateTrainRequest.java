package com.example.TicketBooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTrainRequest {
    @NotBlank(message = "Train number is required")
    private String trainNumber;

    @NotBlank(message = "Train name is required")
    private String trainName;

    @Min(value = 1, message = "Total seats must be at least 1")
    private int totalSeats;

    @NotNull(message = "Source station id is required")
    private Long sourceStationId;

    @NotNull(message = "Destination station id is required")
    private Long destinationStationId;
}
