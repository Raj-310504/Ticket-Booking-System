package com.example.TicketBooking.dto;

import com.example.TicketBooking.enums.BookingStatus;
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
public class CreateBookingResponse {
    private String message;
    private Long bookingId;
    private String pnrNumber;
    private Long trainScheduleId;
    private LocalDate journeyDate;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private int passengerCount;
    private int remainingSeats;
    private List<BookingPassengerResponse> passengers;
}
