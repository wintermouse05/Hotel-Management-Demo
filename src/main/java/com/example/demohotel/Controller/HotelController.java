package com.example.demohotel.Controller;
import com.example.demohotel.dto.CreateHotelRequest;
import com.example.demohotel.dto.ResponseDTO;
import com.example.demohotel.dto.UpdateHotelRequest;
import com.example.demohotel.entity.Hotel;
import com.example.demohotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class HotelController {

    private static List<Hotel> hotels = new ArrayList<>();

    @Autowired
    HotelService hotelService;
    //    Create hotel
    @PostMapping("/hotels")
    public Hotel createHotel(@RequestBody CreateHotelRequest request){
        return hotelService.createHotel(request);
    }
    //    List hotels
    @GetMapping("/hotels")
    public List<Hotel> getHotels(@RequestParam(required = false) Integer rate){
        return hotelService.getAllHotels();
    }
    @GetMapping("/hotels/{hotel_id}")
    public Hotel getHotelById(@PathVariable Long hotel_id){
//        return hotels.stream().filter(hotel -> hotel.getHotelId().equals(hotel_id)).findFirst().orElse(null)
        return hotelService.getHotelById(hotel_id);
    }
    @PutMapping("/hotels/{hotel_id}")
    public Hotel updateHotel(@PathVariable Long hotel_id, @RequestBody UpdateHotelRequest request){
        return hotelService.updateHotel(hotel_id, request);

    }
    private Hotel findHotelById(String hotelId){
        return hotels.stream().filter(hotel -> hotel.getHotelId().equals(hotelId)).findFirst().orElse(null);
    }
    @DeleteMapping("/hotels/{hotel_id}")
    public ResponseDTO<Void> deleteHotel(@PathVariable Long hotel_id){
        return hotelService.deleteHotel(hotel_id);
    }

    @GetMapping("/hotels/search")
    public ResponseEntity<Page<Hotel>> searchHotel(
            @RequestParam(required = false) String hotelName,
            @RequestParam(required = false) Integer minRate,
            @RequestParam(required = false) Integer maxRate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ){
        return hotelService.searchHotel(hotelName, minRate, maxRate, page, size, sortBy, sortDirection);
    }
}
