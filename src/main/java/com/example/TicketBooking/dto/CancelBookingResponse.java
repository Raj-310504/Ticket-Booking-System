package com.example.TicketBooking.dto;

import com.example.TicketBooking.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelBookingResponse {
    private String message;
    private Long bookingId;
    private String pnrNumber;
    private BookingStatus status;
    private int releasedSeats;
    private int availableSeatsAfterCancellation;
}
