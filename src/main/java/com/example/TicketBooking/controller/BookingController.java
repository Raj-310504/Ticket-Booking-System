package com.example.TicketBooking.controller;

import com.example.TicketBooking.dto.BookingResponse;
import com.example.TicketBooking.dto.CancelBookingResponse;
import com.example.TicketBooking.dto.CreateBookingRequest;
import com.example.TicketBooking.dto.CreateBookingResponse;
import com.example.TicketBooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Page<BookingResponse>> getMyBookings(@AuthenticationPrincipal UserDetails userDetails,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(bookingService.getMyBookings(userDetails.getUsername(), page, size));
    }

    @GetMapping("/{pnr}")
    public ResponseEntity<BookingResponse> getBookingByPnr(@AuthenticationPrincipal UserDetails userDetails,
                                                           @PathVariable String pnr) {
        return ResponseEntity.ok(bookingService.getBookingByPnr(userDetails.getUsername(), pnr));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<CancelBookingResponse> cancelBooking(@AuthenticationPrincipal UserDetails userDetails,
                                                               @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.cancelBooking(userDetails.getUsername(), bookingId));
    }
}