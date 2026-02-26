package com.example.TicketBooking.controller;

import com.example.TicketBooking.dto.CreateStationRequest;
import com.example.TicketBooking.dto.CreateStationResponse;
import com.example.TicketBooking.service.StationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<CreateStationResponse> createStation(@Valid @RequestBody CreateStationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stationService.createStation(request));
    }
}
