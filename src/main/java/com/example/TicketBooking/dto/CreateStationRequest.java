package com.example.TicketBooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateStationRequest {
    @NotBlank(message = "Station name is required")
    @Size(min = 3, max = 100, message = "Station name must be between 3 and 100 characters")
    private String stationName;

    @NotBlank(message = "Station code is required")
    @Size(min = 2, max = 10, message = "Station code must be between 2 and 10 characters")
    private String stationCode;

    @NotBlank(message = "City is required")
    private String city;
}
