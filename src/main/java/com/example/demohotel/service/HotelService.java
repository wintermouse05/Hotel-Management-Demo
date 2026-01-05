package com.example.demohotel.service;

import com.example.demohotel.dto.CreateHotelRequest;
import com.example.demohotel.dto.ResponseDTO;
import com.example.demohotel.dto.UpdateHotelRequest;
import com.example.demohotel.entity.Hotel;
import com.example.demohotel.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

    @Autowired
    HotelRepository hotelRepository;

    public List<Hotel> getAllHotels(){
        return hotelRepository.findAll();
    }
    public Hotel getHotelById(Long hotelId){
        return hotelRepository.findByHotelId(hotelId);
    }
    public Hotel createHotel(CreateHotelRequest request){
        Hotel hotel = new Hotel();
        hotel.setHotelName(request.getHotelName());
        hotel.setRate(request.getRate());
        return hotelRepository.save(hotel);
    }
    public Hotel updateHotel(Long hotelId, UpdateHotelRequest request){
        Hotel hotel = hotelRepository.findByHotelId(hotelId);
        if (hotel == null) return null;
        hotel.setHotelName(request.getHotelName());
        hotel.setRate(request.getRate());
        return hotelRepository.save(hotel);

    }
    public ResponseDTO deleteHotel(Long hotelId){
        Hotel hotel = hotelRepository.findByHotelId(hotelId);
        if (hotel == null) return new ResponseDTO(false, "Hotel not found");
        hotel.setStatus(false);
        hotelRepository.save(hotel);
        return new ResponseDTO(true, "Hotel deleted successfully");
    }
}
