package com.example.demohotel.dto;


import lombok.Data;

@Data
public class HotelSearchRequest {
    private String hotelName;
    private Integer minRate;
    private Integer maxRate;
    private int page = 0;
    private int size = 10;
    private String sortBy = "hotelId";
    private String sortDirection = "asc";


}
