package com.adii.seatreservationengine.controller;

import com.adii.seatreservationengine.dto.LoginRequest;
import com.adii.seatreservationengine.dto.LoginResponse;
import com.adii.seatreservationengine.dto.RegisterRequest;
import com.adii.seatreservationengine.dto.UserResponse;
import com.adii.seatreservationengine.entity.User;
import com.adii.seatreservationengine.service.JwtService;
import com.adii.seatreservationengine.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService,JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public UserResponse register(
            @RequestBody RegisterRequest request
    ) {
        User user = userService.register(request);

        return new UserResponse(
                user.getId(),
                user.getUsername()
        );
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        User user = userService.login(request);

        String token = jwtService.generateToken(user);

        return new LoginResponse(token);
    }
}