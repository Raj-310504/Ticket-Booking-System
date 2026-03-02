package com.example.TicketBooking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    private long totalTrains;
    private long totalStations;
    private long totalSchedules;
    private long activeBookings;
}
