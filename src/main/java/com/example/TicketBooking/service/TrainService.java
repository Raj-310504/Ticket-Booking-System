package com.example.TicketBooking.service;

import com.example.TicketBooking.dto.CreateTrainRequest;
import com.example.TicketBooking.dto.CreateTrainResponse;
import com.example.TicketBooking.dto.TrainResponse;
import com.example.TicketBooking.entity.Station;
import com.example.TicketBooking.entity.Train;
import com.example.TicketBooking.repository.StationRepository;
import com.example.TicketBooking.repository.TrainRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainService {

    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;

    public TrainService(TrainRepository trainRepository, StationRepository stationRepository) {
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
    }

    public CreateTrainResponse createTrain(CreateTrainRequest request) {
        if (trainRepository.existsByTrainNumber(request.getTrainNumber())) {
            throw new IllegalStateException("Train number already exists");
        }

        if (request.getSourceStationId().equals(request.getDestinationStationId())) {
            throw new IllegalArgumentException("Source and destination stations must be different");
        }

        Optional<Station> sourceStationOpt = stationRepository.findById(request.getSourceStationId());
        if (sourceStationOpt.isEmpty()) {
            throw new java.util.NoSuchElementException("Source station not found");
        }

        Optional<Station> destinationStationOpt = stationRepository.findById(request.getDestinationStationId());
        if (destinationStationOpt.isEmpty()) {
            throw new java.util.NoSuchElementException("Destination station not found");
        }

        Train train = new Train();
        train.setTrainNumber(request.getTrainNumber());
        train.setTrainName(request.getTrainName());
        train.setTotalSeats(request.getTotalSeats());
        train.setSourceStation(sourceStationOpt.get());
        train.setDestinationStation(destinationStationOpt.get());

        Train savedTrain = trainRepository.save(train);

        return new CreateTrainResponse(
                "Train created successfully",
                savedTrain.getId(),
                savedTrain.getTrainNumber(),
                savedTrain.getTrainName(),
                savedTrain.getTotalSeats(),
                savedTrain.getSourceStation().getId(),
                savedTrain.getDestinationStation().getId()
        );
    }

    public List<TrainResponse> getAllTrains() {
        return trainRepository.findAll().stream()
                .map(train -> new TrainResponse(
                        train.getId(),
                        train.getTrainNumber(),
                        train.getTrainName(),
                        train.getTotalSeats(),
                        train.getSourceStation().getId(),
                        train.getSourceStation().getStationName(),
                        train.getDestinationStation().getId(),
                        train.getDestinationStation().getStationName()
                ))
                .toList();
    }
}
