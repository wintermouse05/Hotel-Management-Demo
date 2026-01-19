package com.example.demohotel.service;

import com.example.demohotel.dto.CreateRoomRequest;
import com.example.demohotel.dto.ResponseDTO;
import com.example.demohotel.dto.RoomAvailabilityResponse;
import com.example.demohotel.entity.Hotel;
import com.example.demohotel.entity.Room;
import com.example.demohotel.repository.BookingRepository;
import com.example.demohotel.repository.HotelRepository;
import com.example.demohotel.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Cacheable(value = "rooms-all", key = "'all'")
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Cacheable(value = "rooms", key = "#roomId")
    public Room getRoomById(Long roomId) {
        return roomRepository.findByRoomId(roomId);
    }

    @Cacheable(value = "hotel-rooms", key = "#hotelId")
    public List<Room> getRoomsByHotelId(Long hotelId) {
        return roomRepository.findByHotelHotelId(hotelId);
    }

    @Caching(evict = {
            @CacheEvict(value = "rooms-all", allEntries = true),
            @CacheEvict(value = "hotel-rooms", key = "#request.hotelId")
    })
    public Room createRoom(CreateRoomRequest request) {
        Hotel hotel = hotelRepository.findByHotelId(request.getHotelId());
        if (hotel == null) {
            throw new RuntimeException("Hotel not found");
        }

        Room room = new Room();
        room.setHotel(hotel);
        room.setRoomName(request.getRoomName());
        room.setRoomType(request.getRoomType());
        room.setTotalQuantity(request.getTotalQuantity());
        room.setPricePerNight(request.getPricePerNight());
        room.setDescription(request.getDescription());

        return roomRepository.save(room);
    }

    @Caching(evict = {
            @CacheEvict(value = "rooms", key = "#roomId"),
            @CacheEvict(value = "rooms-all", allEntries = true),
            @CacheEvict(value = "hotel-rooms", allEntries = true)
    })
    public Room updateRoom(Long roomId, CreateRoomRequest request) {
        Room room = roomRepository.findByRoomId(roomId);
        if (room == null) {
            return null;
        }

        room.setRoomName(request.getRoomName());
        room.setRoomType(request.getRoomType());
        room.setTotalQuantity(request.getTotalQuantity());
        room.setPricePerNight(request.getPricePerNight());
        room.setDescription(request.getDescription());

        return roomRepository.save(room);
    }

    @Caching(evict = {
            @CacheEvict(value = "rooms", key = "#roomId"),
            @CacheEvict(value = "rooms-all", allEntries = true),
            @CacheEvict(value = "hotel-rooms", allEntries = true)
    })
    public ResponseDTO deleteRoom(Long roomId) {
        Room room = roomRepository.findByRoomId(roomId);
        if (room == null) {
            return new ResponseDTO(false, "Room not found");
        }

        room.setStatus(false);
        roomRepository.save(room);
        return new ResponseDTO(true, "Room deleted successfully");
    }

    /**
     * Check room availability for a specific hotel, room type and date range
     */
    public RoomAvailabilityResponse checkRoomAvailability(Long hotelId, String roomType,
                                                          LocalDate checkInDate, LocalDate checkOutDate) {
        Room room = roomRepository.findByHotelHotelIdAndRoomType(hotelId, roomType).orElse(null);

        if (room == null) {
            return new RoomAvailabilityResponse(hotelId, roomType, 0, 0, 0, 0.0, false);
        }

        Integer totalRooms = room.getTotalQuantity();
        Integer bookedRooms = bookingRepository.getTotalBookedRooms(hotelId, roomType, checkInDate, checkOutDate);
        Integer availableRooms = totalRooms - bookedRooms;

        RoomAvailabilityResponse response = new RoomAvailabilityResponse();
        response.setHotelId(hotelId);
        response.setRoomType(roomType);
        response.setTotalRooms(totalRooms);
        response.setBookedRooms(bookedRooms);
        response.setAvailableRooms(availableRooms);
        response.setPricePerNight(room.getPricePerNight());
        response.setAvailable(availableRooms > 0);

        return response;
    }

    /**
     * Get available room count for a specific hotel, room type and date range
     */
    public Integer getAvailableRoomCount(Long hotelId, String roomType,
                                         LocalDate checkInDate, LocalDate checkOutDate) {
        Room room = roomRepository.findByHotelHotelIdAndRoomType(hotelId, roomType).orElse(null);

        if (room == null) {
            return 0;
        }

        Integer totalRooms = room.getTotalQuantity();
        Integer bookedRooms = bookingRepository.getTotalBookedRooms(hotelId, roomType, checkInDate, checkOutDate);

        return totalRooms - bookedRooms;
    }
}
