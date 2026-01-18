package com.example.demohotel.dto;

import lombok.Data;

@Data
public class CreateRoomRequest {
    private Long hotelId;
    private String roomName;
    private String roomType;
    private Integer totalQuantity;
    private Double pricePerNight;
    private String description;
}
