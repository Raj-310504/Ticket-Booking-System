package com.example.TicketBooking.service;

import com.example.TicketBooking.dto.CreateStationRequest;
import com.example.TicketBooking.dto.CreateStationResponse;
import com.example.TicketBooking.entity.Station;
import com.example.TicketBooking.repository.StationRepository;
import org.springframework.stereotype.Service;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public CreateStationResponse createStation(CreateStationRequest request) {
        if (stationRepository.existsByStationCode(request.getStationCode())) {
            throw new IllegalStateException("Station code already exists");
        }

        Station station = new Station();
        station.setStationName(request.getStationName());
        station.setStationCode(request.getStationCode());
        station.setCity(request.getCity());

        Station savedStation = stationRepository.save(station);

        return new CreateStationResponse(
                "Station created successfully",
                savedStation.getId(),
                savedStation.getStationName(),
                savedStation.getStationCode(),
                savedStation.getCity()
        );
    }
}
