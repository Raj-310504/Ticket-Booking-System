package com.example.TicketBooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String trainNumber;

    @NotBlank
    private String trainName;

    @Min(value = 1)
    private int totalSeats;

    @ManyToOne
    @JoinColumn(name = "source_station_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Station sourceStation;

    @ManyToOne
    @JoinColumn(name = "destination_station_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Station destinationStation;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainSchedule> schedules;
}
