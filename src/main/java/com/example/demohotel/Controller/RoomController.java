package com.example.demohotel.Controller;

import com.example.demohotel.dto.CreateRoomRequest;
import com.example.demohotel.dto.ResponseDTO;
import com.example.demohotel.dto.RoomAvailabilityResponse;
import com.example.demohotel.entity.Room;
import com.example.demohotel.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/rooms")
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long roomId) {
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }

    @GetMapping("/hotels/{hotelId}/rooms")
    public List<Room> getRoomsByHotelId(@PathVariable Long hotelId) {
        return roomService.getRoomsByHotelId(hotelId);
    }

    @PostMapping("/rooms")
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequest request) {
        try {
            Room room = roomService.createRoom(request);
            return ResponseEntity.ok(room);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder().code(400).message(e.getMessage()).build());
        }
    }

    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<?> updateRoom(@PathVariable Long roomId, @RequestBody CreateRoomRequest request) {
        Room room = roomService.updateRoom(roomId, request);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }

    @DeleteMapping("/rooms/{roomId}")
    public ResponseDTO<Void> deleteRoom(@PathVariable Long roomId) {
        return roomService.deleteRoom(roomId);
    }

    /**
     * Check room availability for a specific hotel and room type
     */
    @GetMapping("/hotels/{hotelId}/rooms/availability")
    public RoomAvailabilityResponse checkRoomAvailability(
            @PathVariable Long hotelId,
            @RequestParam String roomType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate
    ) {
        return roomService.checkRoomAvailability(hotelId, roomType, checkInDate, checkOutDate);
    }
}
