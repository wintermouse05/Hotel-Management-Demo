package com.example.demohotel.repository;

import com.example.demohotel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Room findByRoomId(Long roomId);

    List<Room> findByHotelHotelId(Long hotelId);

    Optional<Room> findByHotelHotelIdAndRoomType(Long hotelId, String roomType);

    List<Room> findByRoomType(String roomType);
}
