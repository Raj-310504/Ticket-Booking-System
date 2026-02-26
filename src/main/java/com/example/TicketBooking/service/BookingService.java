package com.example.TicketBooking.service;

import com.example.TicketBooking.dto.BookingPassengerResponse;
import com.example.TicketBooking.dto.BookingResponse;
import com.example.TicketBooking.dto.CancelBookingResponse;
import com.example.TicketBooking.dto.CreateBookingPassengerRequest;
import com.example.TicketBooking.dto.CreateBookingRequest;
import com.example.TicketBooking.dto.CreateBookingResponse;
import com.example.TicketBooking.entity.Booking;
import com.example.TicketBooking.entity.Passenger;
import com.example.TicketBooking.entity.TrainSchedule;
import com.example.TicketBooking.entity.User;
import com.example.TicketBooking.enums.BookingStatus;
import com.example.TicketBooking.repository.BookingRepository;
import com.example.TicketBooking.repository.PassengerRepository;
import com.example.TicketBooking.repository.TrainScheduleRepository;
import com.example.TicketBooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrainScheduleRepository trainScheduleRepository;
    @Autowired
    private PassengerRepository passengerRepository;

    @Transactional
    public CreateBookingResponse createBooking(String userEmail, CreateBookingRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new SecurityException("Authenticated user not found"));

        TrainSchedule schedule = trainScheduleRepository.findById(request.getTrainScheduleId())
                .orElseThrow(() -> new NoSuchElementException("Train schedule not found"));

        LocalDate departureDate = schedule.getDepartureDateTime().toLocalDate();
        if (!request.getJourneyDate().equals(departureDate)) {
            throw new IllegalArgumentException("Journey date must match schedule departure date");
        }

        int requestedSeats = request.getPassengers().size();
        if (schedule.getAvailableSeats() < requestedSeats) {
            throw new IllegalStateException("Not enough seats available");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTrainSchedule(schedule);
        booking.setBookingDate(LocalDate.now());
        booking.setJourneyDate(request.getJourneyDate());
        booking.setStatus(BookingStatus.BOOKED);
        booking.setPnrNumber(generateUniquePnr());
        booking.setTotalAmount(request.getFarePerPassenger().multiply(BigDecimal.valueOf(requestedSeats)));

        List<String> assignedSeats = allocateSeats(schedule.getId(), schedule.getTrain().getTotalSeats(), requestedSeats);
        List<Passenger> passengers = new ArrayList<>();
        for (int i = 0; i < request.getPassengers().size(); i++) {
            CreateBookingPassengerRequest passengerRequest = request.getPassengers().get(i);

            Passenger passenger = new Passenger();
            passenger.setName(passengerRequest.getName());
            passenger.setAge(passengerRequest.getAge());
            passenger.setGender(passengerRequest.getGender());
            passenger.setSeatNumber(assignedSeats.get(i));
            passenger.setBooking(booking);
            passengers.add(passenger);
        }
        booking.setPassengers(passengers);

        schedule.setAvailableSeats(schedule.getAvailableSeats() - requestedSeats);

        Booking saved = bookingRepository.save(booking);
        trainScheduleRepository.save(schedule);

        return new CreateBookingResponse(
                "Booking created successfully",
                saved.getId(),
                saved.getPnrNumber(),
                saved.getTrainSchedule().getId(),
                saved.getJourneyDate(),
                saved.getStatus(),
                saved.getTotalAmount(),
                saved.getPassengers().size(),
                saved.getTrainSchedule().getAvailableSeats(),
                mapPassengers(saved.getPassengers())
        );
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new SecurityException("Authenticated user not found"));

        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return bookings.stream().map(this::mapBooking).toList();
    }

    @Transactional
    public CancelBookingResponse cancelBooking(String userEmail, Long bookingId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new SecurityException("Authenticated user not found"));

        Booking booking = bookingRepository.findByIdAndUserId(bookingId, user.getId())
                .orElseThrow(() -> new NoSuchElementException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        int releasedSeats = booking.getPassengers().size();
        booking.setStatus(BookingStatus.CANCELLED);

        TrainSchedule schedule = booking.getTrainSchedule();
        schedule.setAvailableSeats(schedule.getAvailableSeats() + releasedSeats);

        Booking updated = bookingRepository.save(booking);
        trainScheduleRepository.save(schedule);

        return new CancelBookingResponse(
                "Booking cancelled successfully",
                updated.getId(),
                updated.getPnrNumber(),
                updated.getStatus(),
                releasedSeats,
                schedule.getAvailableSeats()
        );
    }

    private BookingResponse mapBooking(Booking booking) {
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
                mapPassengers(booking.getPassengers())
        );
    }

    private List<BookingPassengerResponse> mapPassengers(List<Passenger> passengers) {
        return passengers.stream()
                .map(passenger -> new BookingPassengerResponse(
                        passenger.getName(),
                        passenger.getAge(),
                        passenger.getGender(),
                        passenger.getSeatNumber()
                ))
                .toList();
    }

    private List<String> allocateSeats(Long scheduleId, int totalSeats, int requestedSeats) {
        List<String> alreadyTakenSeats = passengerRepository.findSeatNumbersByScheduleIdAndBookingStatus(scheduleId, BookingStatus.BOOKED);
        Set<String> taken = new HashSet<>(alreadyTakenSeats);
        List<String> assigned = new ArrayList<>();

        for (int i = 1; i <= totalSeats && assigned.size() < requestedSeats; i++) {
            String seat = "S" + i;
            if (!taken.contains(seat)) {
                assigned.add(seat);
            }
        }

        if (assigned.size() < requestedSeats) {
            throw new IllegalStateException("Unable to allocate seats");
        }

        return assigned;
    }

    private String generateUniquePnr() {
        String pnr;
        do {
            pnr = "PNR" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        } while (bookingRepository.existsByPnrNumber(pnr));
        return pnr;
    }
}
