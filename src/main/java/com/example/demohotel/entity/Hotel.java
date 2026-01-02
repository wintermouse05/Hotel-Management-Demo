package com.example.demohotel.entity;
import lombok.Data;

@Data
public class Hotel {
    private String hotelId;
    private String hotelName;
    private Integer rate;

    private boolean status = true;


}
