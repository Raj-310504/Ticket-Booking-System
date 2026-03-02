package com.example.TicketBooking.entity;

import com.example.TicketBooking.enums.BerthType;
import com.example.TicketBooking.enums.SeatStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seat", uniqueConstraints = @UniqueConstraint(columnNames = {"schedule_id", "coach_number", "seat_number"}))
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @NotBlank
    @Column(name = "coach_number", nullable = false)
    private String coachNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "berth_type", nullable = false)
    private BerthType berthType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TrainSchedule trainSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Coach coach;
}
