package com.example.demohotel.repository;

import com.example.demohotel.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findByBookingId(Long bookingId);

    List<Booking> findByUserUserId(Long userId);

    List<Booking> findByHotelHotelId(Long hotelId);

    /**
     * Find all bookings for a specific hotel, room type, and date range
     * that overlap with the requested dates (excluding cancelled bookings)
     */
    @Query("SELECT b FROM Booking b WHERE b.hotel.hotelId = :hotelId " +
            "AND b.roomType = :roomType " +
            "AND b.bookingStatus != 'CANCELLED' " +
            "AND b.checkInDate < :checkOutDate " +
            "AND b.checkOutDate > :checkInDate")
    List<Booking> findOverlappingBookings(
            @Param("hotelId") Long hotelId,
            @Param("roomType") String roomType,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    /**
     * Calculate total booked rooms for a specific hotel, room type and date range
     */
    @Query("SELECT COALESCE(SUM(b.quantity), 0) FROM Booking b WHERE b.hotel.hotelId = :hotelId " +
            "AND b.roomType = :roomType " +
            "AND b.bookingStatus != 'CANCELLED' " +
            "AND b.checkInDate < :checkOutDate " +
            "AND b.checkOutDate > :checkInDate")
    Integer getTotalBookedRooms(
            @Param("hotelId") Long hotelId,
            @Param("roomType") String roomType,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );
}
