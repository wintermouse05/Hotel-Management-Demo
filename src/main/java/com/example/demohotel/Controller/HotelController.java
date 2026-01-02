package com.example.demohotel.Controller;
import com.example.demohotel.dto.CreateHotelRequest;
import com.example.demohotel.dto.ResponseDTO;
import com.example.demohotel.dto.UpdateHotelRequest;
import com.example.demohotel.entity.Hotel;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class HotelController {

    private static List<Hotel> hotels = new ArrayList<>();
//    Create hotel
    @PostMapping("/hotels")
    public Hotel createHotel(@RequestBody CreateHotelRequest request){
        Hotel hotel = new Hotel();
        hotel.setHotelId(request.getHotelId());
        hotel.setHotelName(request.getHotelName());
        hotel.setRate(request.getRate());
        hotels.add(hotel);

        return hotel;
    }
//    List hotels
    @GetMapping("/hotels")
    public List<Hotel> getHotels(@RequestParam(required = false) Integer rate){
        if (rate != null ) {
            List<Hotel> result = new LinkedList<>();
            for (Hotel hotel: hotels) {
                if (hotel.getRate().equals(rate)) result.add(hotel);
            }
            return result;
        }
        return hotels;
    }
    @GetMapping("/hotels/{hotel_id}")
    public Hotel getHotelById(@PathVariable String hotel_id){
//        return hotels.stream().filter(hotel -> hotel.getHotelId().equals(hotel_id)).findFirst().orElse(null)
        for (Hotel hotel: hotels) {
            if (hotel.getHotelId().equals(hotel_id)) return hotel;
        }
        return null;
    }
    @PutMapping("/hotels/{hotel_id}")
    public Hotel updateHotel(@PathVariable String hotel_id, @RequestBody UpdateHotelRequest request){
        Hotel hotel = findHotelById(hotel_id);
        if (hotel == null) {
            return null;

        }
        hotel.setHotelName(request.getHotelName());
        hotel.setStatus(request.getStatus());
        return hotel;

    }
    private Hotel findHotelById(String hotelId){
        return hotels.stream().filter(hotel -> hotel.getHotelId().equals(hotelId)).findFirst().orElse(null);
    }
    @DeleteMapping("/hotels/{hotel_id}")
    public ResponseDTO deleteHotel(@PathVariable String hotel_id){
        for (Hotel hotel: hotels) {
            if (hotel.getHotelId().equals(hotel_id)) {
                hotels.remove(hotel);
                return new ResponseDTO(true, "Deleted hotel");

            }
        }
        return new ResponseDTO(false, "Hotel not found");
    }
}
