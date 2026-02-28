package com.example.TicketBooking.service;

import com.example.TicketBooking.dto.*;
import com.example.TicketBooking.entity.*;
import com.example.TicketBooking.enums.BerthType;
import com.example.TicketBooking.enums.SeatStatus;
import com.example.TicketBooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TrainScheduleService {

    private static final int SEATS_PER_COACH = 100;
    private static final String DEFAULT_COACH_PREFIX = "D";
    private static final List<BerthType> BERTH_SEQUENCE = Arrays.asList(
            BerthType.LOWER,
            BerthType.MIDDLE,
            BerthType.UPPER,
            BerthType.LOWER,
            BerthType.MIDDLE,
            BerthType.UPPER,
            BerthType.SIDE_LOWER,
            BerthType.SIDE_UPPER
    );

    private final TrainScheduleRepository trainScheduleRepository;
    private final TrainRepository trainRepository;
    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;

    public TrainScheduleService(TrainScheduleRepository trainScheduleRepository,
                                TrainRepository trainRepository,
                                CoachRepository coachRepository,
                                SeatRepository seatRepository) {
        this.trainScheduleRepository = trainScheduleRepository;
        this.trainRepository = trainRepository;
        this.coachRepository = coachRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public CreateTrainScheduleResponse createSchedule(CreateTrainScheduleRequest request) {
        Optional<Train> trainOpt = trainRepository.findById(request.getTrainId());
        if (trainOpt.isEmpty()) {
            throw new NoSuchElementException("Train not found");
        }

        // Arrival > Departure validation
        if (!request.getArrivalDateTime().isAfter(request.getDepartureDateTime())) {
            throw new IllegalArgumentException("Arrival time must be after departure time");
        }

        // Available Seats validation
        Train train = trainOpt.get();
        if (request.getAvailableSeats() > train.getTotalSeats()) {
            throw new IllegalArgumentException("Available seats cannot exceed total train seats");
        }

        TrainSchedule schedule = new TrainSchedule();
        schedule.setTrain(train);
        schedule.setDepartureDateTime(request.getDepartureDateTime());
        schedule.setArrivalDateTime(request.getArrivalDateTime());
        schedule.setAvailableSeats(request.getAvailableSeats());

        TrainSchedule saved = trainScheduleRepository.save(schedule);
        // Seats initialize
        initializeSeats(saved, request.getAvailableSeats());

        return new CreateTrainScheduleResponse(
                "Train schedule created successfully",
                saved.getId(),
                train.getId(),
                train.getTrainNumber(),
                saved.getDepartureDateTime(),
                saved.getArrivalDateTime(),
                saved.getAvailableSeats()
        );
    }

    public List<TrainScheduleResponse> searchSchedules(Long sourceStationId, Long destinationStationId, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        if (sourceStationId.equals(destinationStationId)) {
            throw new IllegalArgumentException("Source and destination stations must be different");
        }

        List<TrainSchedule> schedules = trainScheduleRepository.searchSchedules(
                sourceStationId,
                destinationStationId,
                startOfDay,
                endOfDay
        );

        return schedules.stream()
                .map(schedule -> new TrainScheduleResponse(
                        schedule.getId(),
                        schedule.getTrain().getId(),
                        schedule.getTrain().getTrainNumber(),
                        schedule.getTrain().getTrainName(),
                        schedule.getTrain().getSourceStation().getId(),
                        schedule.getTrain().getSourceStation().getStationName(),
                        schedule.getTrain().getDestinationStation().getId(),
                        schedule.getTrain().getDestinationStation().getStationName(),
                        schedule.getDepartureDateTime(),
                        schedule.getArrivalDateTime(),
                        schedule.getAvailableSeats()
                ))
                .toList();
    }

    // coach and seat auto generate
    private void initializeSeats(TrainSchedule schedule, int totalAllocatableSeats) {
        if (totalAllocatableSeats <= 0) {
            return;
        }

        // calculate total coaches
        int totalCoaches = (int) Math.ceil((double) totalAllocatableSeats / SEATS_PER_COACH);
        List<Coach> coaches = new ArrayList<>(totalCoaches);
        List<Seat> seats = new ArrayList<>(totalAllocatableSeats);

        int remainingSeats = totalAllocatableSeats;
        // create coaches
        for (int coachIndex = 1; coachIndex <= totalCoaches; coachIndex++) {
            String coachNumber = DEFAULT_COACH_PREFIX + coachIndex;
            Coach coach = new Coach();
            coach.setCoachNumber(coachNumber);
            coach.setTrainSchedule(schedule);
            coaches.add(coach);
        }

        List<Coach> savedCoaches = coachRepository.saveAll(coaches);

        for (Coach coach : savedCoaches) {
            int seatsInCoach = Math.min(SEATS_PER_COACH, remainingSeats);
            for (int seatIndex = 1; seatIndex <= seatsInCoach; seatIndex++) {
                Seat seat = new Seat();
                seat.setTrainSchedule(schedule);
                seat.setCoach(coach);
                seat.setCoachNumber(coach.getCoachNumber());
                seat.setSeatNumber("S" + seatIndex);
                seat.setStatus(SeatStatus.AVAILABLE);
                // Cyclic berth assign
                seat.setBerthType(BERTH_SEQUENCE.get((seatIndex - 1) % BERTH_SEQUENCE.size()));
                seats.add(seat);
            }
            remainingSeats -= seatsInCoach;
        }
        seatRepository.saveAll(seats);
    }
}
