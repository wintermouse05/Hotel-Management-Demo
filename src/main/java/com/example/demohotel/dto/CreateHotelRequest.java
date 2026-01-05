package com.example.demohotel.dto;
import lombok.Data;

@Data
public class CreateHotelRequest {
//    private Long hotelId;
    private String hotelName;
    private Integer rate;
}
