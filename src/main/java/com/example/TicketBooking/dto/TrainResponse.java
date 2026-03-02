package com.example.TicketBooking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainResponse {
    private Long id;
    private String trainNumber;
    private String trainName;
    private int totalSeats;
    private Long sourceStationId;
    private String sourceStationName;
    private Long destinationStationId;
    private String destinationStationName;
}
