package com.example.demohotel.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateBookingRequest {
    private Long userId;
    private Long hotelId;
    private String roomType;
    private Integer quantity;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
