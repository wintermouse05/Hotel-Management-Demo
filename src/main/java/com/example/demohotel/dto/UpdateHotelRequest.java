package com.example.demohotel.dto;
import lombok.Data;

@Data
public class UpdateHotelRequest {
    private String hotelName;
    private Integer rate;

}
