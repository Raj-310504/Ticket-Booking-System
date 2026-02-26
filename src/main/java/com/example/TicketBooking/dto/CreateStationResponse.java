package com.example.TicketBooking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateStationResponse {
    private String message;
    private Long id;
    private String stationName;
    private String stationCode;
    private String city;
}
