package com.example.demohotel.Controller;

import com.example.demohotel.dto.BookingResponse;
import com.example.demohotel.dto.CreateBookingRequest;
import com.example.demohotel.dto.UpdateBookingRequest;
import com.example.demohotel.entity.Booking;
import com.example.demohotel.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * Get all bookings
     */
    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    /**
     * Get booking by ID
     */
    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(booking);
    }

    /**
     * Get all bookings for a specific user
     */
    @GetMapping("/users/{userId}/bookings")
    public List<Booking> getBookingsByUserId(@PathVariable Long userId) {
        return bookingService.getBookingsByUserId(userId);
    }

    /**
     * Get all bookings for a specific hotel
     */
    @GetMapping("/hotels/{hotelId}/bookings")
    public List<Booking> getBookingsByHotelId(@PathVariable Long hotelId) {
        return bookingService.getBookingsByHotelId(hotelId);
    }

    /**
     * Create a new booking
     * User can book rooms if they are available for the selected dates
     */
    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody CreateBookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Update an existing booking
     */
    @PutMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long bookingId,
            @RequestBody UpdateBookingRequest request
    ) {
        BookingResponse response = bookingService.updateBooking(bookingId, request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Cancel a booking
     */
    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long bookingId) {
        BookingResponse response = bookingService.cancelBooking(bookingId);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}
