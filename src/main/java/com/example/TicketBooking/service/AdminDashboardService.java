package com.example.TicketBooking.service;

import com.example.TicketBooking.dto.AdminDashboardResponse;
import com.example.TicketBooking.enums.BookingStatus;
import com.example.TicketBooking.repository.BookingRepository;
import com.example.TicketBooking.repository.StationRepository;
import com.example.TicketBooking.repository.TrainRepository;
import com.example.TicketBooking.repository.TrainScheduleRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardService {

    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final TrainScheduleRepository trainScheduleRepository;
    private final BookingRepository bookingRepository;

    public AdminDashboardService(TrainRepository trainRepository,
                                 StationRepository stationRepository,
                                 TrainScheduleRepository trainScheduleRepository,
                                 BookingRepository bookingRepository) {
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
        this.trainScheduleRepository = trainScheduleRepository;
        this.bookingRepository = bookingRepository;
    }

    public AdminDashboardResponse getDashboardCounts() {
        return new AdminDashboardResponse(
                trainRepository.count(),
                stationRepository.count(),
                trainScheduleRepository.count(),
                bookingRepository.countByStatus(BookingStatus.BOOKED)
        );
    }
}
