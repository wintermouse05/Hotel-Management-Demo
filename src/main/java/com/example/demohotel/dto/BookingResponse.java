package com.example.demohotel.dto;

import com.example.demohotel.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private boolean success;
    private String message;
    private Booking booking;

    public BookingResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
