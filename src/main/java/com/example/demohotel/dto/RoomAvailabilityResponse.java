package com.example.demohotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomAvailabilityResponse {
    private Long hotelId;
    private String roomType;
    private Integer totalRooms;
    private Integer bookedRooms;
    private Integer availableRooms;
    private Double pricePerNight;
    private boolean isAvailable;
}
