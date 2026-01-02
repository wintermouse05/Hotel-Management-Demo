package com.example.demohotel.dto;
import lombok.Data;

@Data
public class UpdateHotelRequest {

    private String hotelName;
    private boolean status;

    public boolean getStatus() {
        return status;
    }

}
