package com.example.TicketBooking.controller;

import com.example.TicketBooking.dto.CreateTrainRequest;
import com.example.TicketBooking.dto.CreateTrainResponse;
import com.example.TicketBooking.service.TrainService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trains")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @PostMapping
    public ResponseEntity<CreateTrainResponse> createTrain(@Valid @RequestBody CreateTrainRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainService.createTrain(request));
    }
}
