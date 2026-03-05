package com.example.TicketBooking.mapper;

import com.example.TicketBooking.dto.BookingPassengerResponse;
import com.example.TicketBooking.dto.BookingResponse;
import com.example.TicketBooking.entity.Booking;
import com.example.TicketBooking.entity.Passenger;
import com.example.TicketBooking.entity.Seat;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingMapper {

    public BookingResponse toBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getPnrNumber(),
                booking.getTrainSchedule().getId(),
                booking.getTrainSchedule().getTrain().getTrainNumber(),
                booking.getTrainSchedule().getTrain().getTrainName(),
                booking.getTrainSchedule().getDepartureDateTime(),
                booking.getTrainSchedule().getArrivalDateTime(),
                booking.getBookingDate(),
                booking.getJourneyDate(),
                booking.getStatus(),
                booking.getTotalAmount(),
                toPassengerResponses(booking.getPassengers())
        );
    }

    public List<BookingPassengerResponse> toPassengerResponses(List<Passenger> passengers) {
        return passengers.stream()
                .map(passenger -> new BookingPassengerResponse(
                        passenger.getName(),
                        passenger.getAge(),
                        passenger.getGender(),
                        passenger.getSeat() != null ? formatSeatLabel(passenger.getSeat()) : passenger.getSeatNumber()
                ))
                .toList();
    }

    private String formatSeatLabel(Seat seat) {
        return seat.getCoachNumber() + "-" + seat.getSeatNumber();
    }
}
