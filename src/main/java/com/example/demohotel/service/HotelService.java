package com.example.demohotel.service;

import com.example.demohotel.dto.CreateHotelRequest;
import com.example.demohotel.dto.HotelSearchRequest;
import com.example.demohotel.dto.ResponseDTO;
import com.example.demohotel.dto.UpdateHotelRequest;
import com.example.demohotel.entity.Hotel;
import com.example.demohotel.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

    @Autowired
    HotelRepository hotelRepository;

    @Cacheable(value = "hotels-all", key = "'all'")
    public List<Hotel> getAllHotels(){
        return hotelRepository.findAll();
    }
    @Cacheable(value = "hotels", key = "#hotelId")
    public Hotel getHotelById(Long hotelId){
        return hotelRepository.findByHotelId(hotelId);
    }
    @CacheEvict(value = "hotels-all", allEntries = true)
    public Hotel createHotel(CreateHotelRequest request){
        Hotel hotel = new Hotel();
        hotel.setHotelName(request.getHotelName());
        hotel.setRate(request.getRate());
        return hotelRepository.save(hotel);
    }
    @Caching(evict = {
            @CacheEvict(value = "hotels", key = "#hotelId"),
            @CacheEvict(value = "hotels-all", allEntries = true)
    })
    public Hotel updateHotel(Long hotelId, UpdateHotelRequest request){
        Hotel hotel = hotelRepository.findByHotelId(hotelId);
        if (hotel == null) return null;
        hotel.setHotelName(request.getHotelName());
        hotel.setRate(request.getRate());
        return hotelRepository.save(hotel);

    }
    @Caching(evict = {
            @CacheEvict(value = "hotels", key = "#hotelId"),
            @CacheEvict(value = "hotels-all", allEntries = true)
    })
    public ResponseDTO<Void> deleteHotel(Long hotelId){
        Hotel hotel = hotelRepository.findByHotelId(hotelId);
        if (hotel == null) return ResponseDTO.<Void>builder().code(404).message("Hotel not found").build();
        hotel.setStatus(false);
        hotelRepository.save(hotel);
        return ResponseDTO.<Void>builder().code(200).message("Hotel deleted successfully").build();
    }
    public ResponseEntity<Page<Hotel>> searchHotel(String hotelName, Integer minRate, Integer maxRate, int page, int size, String sortBy, String sortDirection){
        HotelSearchRequest request = new HotelSearchRequest();
        request.setHotelName(hotelName);
        request.setMinRate(minRate);
        request.setMaxRate(maxRate);
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDirection(sortDirection);
        // Implement search logic here using the request object

        Specification<Hotel> spec = (root, query, cb) -> cb.conjunction();

        if (request.getHotelName() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("hotelName")),
                            "%" + request.getHotelName().toLowerCase() + "%"));
        }
        if (request.getMinRate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("rate"), request.getMinRate()));
        }
        if (request.getMaxRate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("rate"), request.getMaxRate()));
        }

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize()
        );

        Page<Hotel> result = hotelRepository.findAll(spec, pageable);
        return ResponseEntity.ok(result);


    }
}
