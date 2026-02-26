package com.example.TicketBooking.service;

import com.example.TicketBooking.dto.CreateTrainScheduleRequest;
import com.example.TicketBooking.dto.CreateTrainScheduleResponse;
import com.example.TicketBooking.dto.TrainScheduleResponse;
import com.example.TicketBooking.entity.Train;
import com.example.TicketBooking.entity.TrainSchedule;
import com.example.TicketBooking.repository.TrainRepository;
import com.example.TicketBooking.repository.TrainScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TrainScheduleService {

    private final TrainScheduleRepository trainScheduleRepository;
    private final TrainRepository trainRepository;

    public TrainScheduleService(TrainScheduleRepository trainScheduleRepository, TrainRepository trainRepository) {
        this.trainScheduleRepository = trainScheduleRepository;
        this.trainRepository = trainRepository;
    }

    public CreateTrainScheduleResponse createSchedule(CreateTrainScheduleRequest request) {
        Optional<Train> trainOpt = trainRepository.findById(request.getTrainId());
        if (trainOpt.isEmpty()) {
            throw new java.util.NoSuchElementException("Train not found");
        }

        if (!request.getArrivalDateTime().isAfter(request.getDepartureDateTime())) {
            throw new IllegalArgumentException("Arrival time must be after departure time");
        }

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
}
