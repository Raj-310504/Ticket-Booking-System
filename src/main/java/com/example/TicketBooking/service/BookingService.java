package com.example.TicketBooking.service;

import com.example.TicketBooking.dto.BookingPassengerResponse;
import com.example.TicketBooking.dto.BookingResponse;
import com.example.TicketBooking.dto.CancelBookingResponse;
import com.example.TicketBooking.dto.CreateBookingPassengerRequest;
import com.example.TicketBooking.dto.CreateBookingRequest;
import com.example.TicketBooking.dto.CreateBookingResponse;
import com.example.TicketBooking.entity.Booking;
import com.example.TicketBooking.entity.Passenger;
import com.example.TicketBooking.entity.Seat;
import com.example.TicketBooking.entity.TrainSchedule;
import com.example.TicketBooking.entity.User;
import com.example.TicketBooking.enums.BookingStatus;
import com.example.TicketBooking.enums.SeatStatus;
import com.example.TicketBooking.repository.BookingRepository;
import com.example.TicketBooking.repository.PassengerRepository;
import com.example.TicketBooking.repository.SeatRepository;
import com.example.TicketBooking.repository.TrainScheduleRepository;
import com.example.TicketBooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
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
    private SeatRepository seatRepository;
    @Autowired
    private PassengerRepository passengerRepository;

    @Transactional
    public CreateBookingResponse createBooking(String userEmail, CreateBookingRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new SecurityException("Authenticated user not found"));

        TrainSchedule schedule = trainScheduleRepository.findByIdForUpdate(request.getTrainScheduleId())
                .orElseThrow(() -> new NoSuchElementException("Train schedule not found"));

        // journey date validation
        LocalDate departureDate = schedule.getDepartureDateTime().toLocalDate();
        if (!request.getJourneyDate().equals(departureDate)) {
            throw new IllegalArgumentException("Journey date must match schedule departure date");
        }

        int requestedSeats = request.getPassengers().size();

        List<Seat> allocatedSeats = allocateSeats(schedule.getId(), requestedSeats, request.getPreferredCoach());
        int confirmedSeatsCount = allocatedSeats.size();

        for (Seat seat : allocatedSeats) {
            seat.setStatus(SeatStatus.BOOKED);
        }
        if (!allocatedSeats.isEmpty()) {
            seatRepository.saveAll(allocatedSeats);
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTrainSchedule(schedule);
        booking.setBookingDate(LocalDate.now());
        booking.setJourneyDate(request.getJourneyDate());
        booking.setStatus(BookingStatus.BOOKED);
        booking.setPnrNumber(generateUniquePnr());
        booking.setTotalAmount(request.getFarePerPassenger().multiply(BigDecimal.valueOf(requestedSeats)));

        List<Passenger> passengers = new ArrayList<>();
        // RAC logic
        long existingRacCount = passengerRepository.countRacPassengersByScheduleAndBookingStatus(schedule.getId(), BookingStatus.BOOKED);
        int racSequence = (int) existingRacCount;

        // Passenger Creation Loop
        for (int i = 0; i < request.getPassengers().size(); i++) {
            CreateBookingPassengerRequest passengerRequest = request.getPassengers().get(i);

            Passenger passenger = new Passenger();
            passenger.setName(passengerRequest.getName());
            passenger.setAge(passengerRequest.getAge());
            passenger.setGender(passengerRequest.getGender());
            if (i < confirmedSeatsCount) {
                Seat seat = allocatedSeats.get(i);
                passenger.setSeat(seat);
                passenger.setSeatNumber(formatSeatLabel(seat));
            } else {
                racSequence++;
                passenger.setSeat(null);
                passenger.setSeatNumber("RAC-" + racSequence);
            }
            passenger.setBooking(booking);
            passengers.add(passenger);
        }
        booking.setPassengers(passengers);

        schedule.setAvailableSeats(Math.max(0, schedule.getAvailableSeats() - confirmedSeatsCount));

        // save booking & schedule
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

        Booking booking = bookingRepository.findByIdAndUserIdForUpdate(bookingId, user.getId())
                .orElseThrow(() -> new NoSuchElementException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        TrainSchedule schedule = trainScheduleRepository.findByIdForUpdate(booking.getTrainSchedule().getId())
                .orElseThrow(() -> new NoSuchElementException("Train schedule not found"));

        // Release Seats
        List<Seat> seatsToRelease = seatRepository.findBookedSeatsByBookingIdForUpdate(bookingId, BookingStatus.BOOKED);
        for (Seat seat : seatsToRelease) {
            seat.setStatus(SeatStatus.AVAILABLE);
        }
        if (!seatsToRelease.isEmpty()) {
            seatRepository.saveAll(seatsToRelease);
        }

        // RAC Promotion logic
        List<Seat> seatsForPromotion = seatRepository.findAvailableSeatsByScheduleForUpdate(
                schedule.getId(),
                SeatStatus.AVAILABLE.name(),
                seatsToRelease.size()
        );
        // find RAC passengers
        List<Passenger> racPassengersToPromote = passengerRepository.findRacPassengersForUpdate(
                schedule.getId(),
                BookingStatus.BOOKED.name(),
                seatsForPromotion.size()
        );

        // RAC -> Confirmed
        int promotedCount = Math.min(seatsForPromotion.size(), racPassengersToPromote.size());
        for (int i = 0; i < promotedCount; i++) {
            Seat availableSeat = seatsForPromotion.get(i);
            Passenger racPassenger = racPassengersToPromote.get(i);
            availableSeat.setStatus(SeatStatus.BOOKED);
            racPassenger.setSeat(availableSeat);
            racPassenger.setSeatNumber(formatSeatLabel(availableSeat));
        }
        if (promotedCount > 0) {
            seatRepository.saveAll(seatsForPromotion.subList(0, promotedCount));
            passengerRepository.saveAll(racPassengersToPromote.subList(0, promotedCount));
        }

        // update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        // update available seats count
        schedule.setAvailableSeats(schedule.getAvailableSeats() + seatsToRelease.size() - promotedCount);

        Booking updated = bookingRepository.save(booking);
        trainScheduleRepository.save(schedule);

        return new CancelBookingResponse(
                "Booking cancelled successfully",
                updated.getId(),
                updated.getPnrNumber(),
                updated.getStatus(),
                seatsToRelease.size(),
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
                        passenger.getSeat() != null ? formatSeatLabel(passenger.getSeat()) : passenger.getSeatNumber()
                ))
                .toList();
    }

    private List<Seat> allocateSeats(Long scheduleId, int requestedSeats, String preferredCoach) {
        String normalizedCoach = preferredCoach == null ? null : preferredCoach.trim().toUpperCase(Locale.ROOT);
        List<Seat> allocated = new ArrayList<>(requestedSeats);

        if (normalizedCoach != null && !normalizedCoach.isBlank()) {
            allocated.addAll(seatRepository.findAvailableSeatsByScheduleAndCoachForUpdate(
                    scheduleId,
                    SeatStatus.AVAILABLE.name(),
                    normalizedCoach,
                    requestedSeats
            ));
        }

        int remaining = requestedSeats - allocated.size();
        if (remaining > 0) {
            List<Seat> fallbackSeats;
            if (allocated.isEmpty()) {
                fallbackSeats = seatRepository.findAvailableSeatsByScheduleForUpdate(
                        scheduleId,
                        SeatStatus.AVAILABLE.name(),
                        remaining
                );
            } else {
                List<Long> allocatedSeatIds = allocated.stream().map(Seat::getId).toList();
                fallbackSeats = seatRepository.findAvailableSeatsByScheduleExcludingIdsForUpdate(
                        scheduleId,
                        SeatStatus.AVAILABLE.name(),
                        allocatedSeatIds,
                        remaining
                );
            }
            allocated.addAll(fallbackSeats);
        }

        return allocated;
    }

    private String formatSeatLabel(Seat seat) {
        return seat.getCoachNumber() + "-" + seat.getSeatNumber();
    }

    private String generateUniquePnr() {
        String pnr;
        do {
            pnr = "PNR" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        } while (bookingRepository.existsByPnrNumber(pnr));
        return pnr;
    }
}
