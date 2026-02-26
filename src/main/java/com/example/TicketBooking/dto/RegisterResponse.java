package com.example.TicketBooking.dto;

import com.example.TicketBooking.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private Long id;
    private String name;
    private String email;
    private Role role;
}
