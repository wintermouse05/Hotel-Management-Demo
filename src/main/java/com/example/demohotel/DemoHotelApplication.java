package com.example.demohotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DemoHotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoHotelApplication.class, args);
    }

}
