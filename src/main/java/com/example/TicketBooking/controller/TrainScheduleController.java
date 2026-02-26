package com.example.TicketBooking.controller;

import com.example.TicketBooking.dto.CreateTrainScheduleRequest;
import com.example.TicketBooking.dto.CreateTrainScheduleResponse;
import com.example.TicketBooking.dto.TrainScheduleResponse;
import com.example.TicketBooking.service.TrainScheduleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class TrainScheduleController {

    private final TrainScheduleService trainScheduleService;

    public TrainScheduleController(TrainScheduleService trainScheduleService) {
        this.trainScheduleService = trainScheduleService;
    }

    @PostMapping
    public ResponseEntity<CreateTrainScheduleResponse> createSchedule(@Valid @RequestBody CreateTrainScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainScheduleService.createSchedule(request));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrainScheduleResponse>> searchSchedules(
            @RequestParam Long sourceStationId,
            @RequestParam Long destinationStationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate
    ) {
        LocalDateTime startOfDay = journeyDate.atStartOfDay();
        LocalDateTime endOfDay = journeyDate.atTime(23, 59, 59);
        return ResponseEntity.ok(
                trainScheduleService.searchSchedules(sourceStationId, destinationStationId, startOfDay, endOfDay)
        );
    }
}
