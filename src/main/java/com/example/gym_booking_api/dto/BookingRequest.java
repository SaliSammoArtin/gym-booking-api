package com.example.gym_booking_api.dto;

import com.example.gym_booking_api.model.BookingStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingRequest(

        @NotBlank
        String name,

        @NotBlank
        @Email
        String email,

        @NotNull
        LocalDateTime dateTime,

        @NotNull
        @Min(1)
        Integer numberOfPeople,

        @NotNull
        BookingStatus status,

        @NotBlank
        String activity

) {
}