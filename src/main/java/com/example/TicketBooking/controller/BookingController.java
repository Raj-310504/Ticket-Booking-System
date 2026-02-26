package com.example.TicketBooking.controller;

import com.example.TicketBooking.dto.BookingResponse;
import com.example.TicketBooking.dto.CancelBookingResponse;
import com.example.TicketBooking.dto.CreateBookingRequest;
import com.example.TicketBooking.dto.CreateBookingResponse;
import com.example.TicketBooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<CreateBookingResponse> createBooking(@AuthenticationPrincipal UserDetails userDetails,
                                                               @Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(userDetails.getUsername(), request));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookingService.getMyBookings(userDetails.getUsername()));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<CancelBookingResponse> cancelBooking(@AuthenticationPrincipal UserDetails userDetails,
                                                               @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.cancelBooking(userDetails.getUsername(), bookingId));
    }
}
