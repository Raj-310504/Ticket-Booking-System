package com.example.TicketBooking.entity;

import com.example.TicketBooking.enums.ScheduleStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FutureOrPresent
    @NotNull
    private LocalDateTime departureDateTime;

    @NotNull
    private LocalDateTime arrivalDateTime;

    @Min(0)
    private int availableSeats;

    @DecimalMin(value = "0.0", inclusive = false)
    @Column(precision = 10, scale = 2)
    private BigDecimal farePerPassenger;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Train train;

    @OneToMany(mappedBy = "trainSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "trainSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coach> coaches;

    @OneToMany(mappedBy = "trainSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats;
}
