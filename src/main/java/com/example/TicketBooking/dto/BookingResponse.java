package com.example.TicketBooking.dto;

import com.example.TicketBooking.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private String pnrNumber;
    private Long trainScheduleId;
    private String trainNumber;
    private String trainName;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
    private LocalDate bookingDate;
    private LocalDate journeyDate;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private List<BookingPassengerResponse> passengers;
}
