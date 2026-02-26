package com.example.TicketBooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String stationName;

    @NotBlank
    @Size(min = 2, max = 10)
    @Column(unique = true)
    private String stationCode;

    @NotBlank
    private String city;
}