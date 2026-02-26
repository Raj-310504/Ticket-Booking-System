package com.example.TicketBooking.service;

import com.example.TicketBooking.dto.LoginRequest;
import com.example.TicketBooking.dto.LoginResponse;
import com.example.TicketBooking.dto.RegisterRequest;
import com.example.TicketBooking.dto.RegisterResponse;
import com.example.TicketBooking.entity.User;
import com.example.TicketBooking.enums.Role;
import com.example.TicketBooking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                "User registered successfully",
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            throw new SecurityException("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new SecurityException("Invalid email or password");
        }

        return new LoginResponse(
                "Login successful",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
