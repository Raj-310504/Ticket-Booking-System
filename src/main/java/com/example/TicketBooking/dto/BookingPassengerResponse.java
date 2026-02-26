package com.example.TicketBooking.dto;

import com.example.TicketBooking.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingPassengerResponse {
    private String name;
    private int age;
    private Gender gender;
    private String seatNumber;
}
