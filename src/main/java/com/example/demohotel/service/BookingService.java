package com.example.demohotel.service;

import com.example.demohotel.dto.BookingResponse;
import com.example.demohotel.dto.CreateBookingRequest;
import com.example.demohotel.dto.UpdateBookingRequest;
import com.example.demohotel.entity.Booking;
import com.example.demohotel.entity.Hotel;
import com.example.demohotel.entity.Room;
import com.example.demohotel.entity.User;
import com.example.demohotel.repository.BookingRepository;
import com.example.demohotel.repository.HotelRepository;
import com.example.demohotel.repository.RoomRepository;
import com.example.demohotel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomService roomService;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findByBookingId(bookingId);
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserUserId(userId);
    }

    public List<Booking> getBookingsByHotelId(Long hotelId) {
        return bookingRepository.findByHotelHotelId(hotelId);
    }

    /**
     * Create a new booking with availability check
     * User can only book if rooms are available for the selected dates
     */
    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        // Validate user
        User user = userRepository.findByUserId(request.getUserId());
        if (user == null) {
            return new BookingResponse(false, "User not found");
        }

        // Validate hotel
        Hotel hotel = hotelRepository.findByHotelId(request.getHotelId());
        if (hotel == null) {
            return new BookingResponse(false, "Hotel not found");
        }

        // Validate room type exists in hotel
        Room room = roomRepository.findByHotelHotelIdAndRoomType(request.getHotelId(), request.getRoomType())
                .orElse(null);
        if (room == null) {
            return new BookingResponse(false, "Room type '" + request.getRoomType() + "' not found in this hotel");
        }

        // Validate dates
        if (request.getCheckInDate() == null || request.getCheckOutDate() == null) {
            return new BookingResponse(false, "Check-in and check-out dates are required");
        }

        if (request.getCheckInDate().isAfter(request.getCheckOutDate())) {
            return new BookingResponse(false, "Check-in date must be before check-out date");
        }

        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            return new BookingResponse(false, "Check-in date cannot be in the past");
        }

        // Validate quantity
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            return new BookingResponse(false, "Quantity must be greater than 0");
        }

        // Check room availability
        Integer availableRooms = roomService.getAvailableRoomCount(
                request.getHotelId(),
                request.getRoomType(),
                request.getCheckInDate(),
                request.getCheckOutDate()
        );

        if (availableRooms < request.getQuantity()) {
            return new BookingResponse(false,
                    "Not enough rooms available. Requested: " + request.getQuantity() +
                            ", Available: " + availableRooms);
        }

        // Calculate total price
        long numberOfNights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        Double totalPrice = room.getPricePerNight() * request.getQuantity() * numberOfNights;

        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setHotel(hotel);
        booking.setRoomType(request.getRoomType());
        booking.setQuantity(request.getQuantity());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setTotalPrice(totalPrice);
        booking.setBookingStatus(Booking.BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        return new BookingResponse(true, "Booking created successfully", savedBooking);
    }

    /**
     * Update an existing booking with availability check
     */
    @Transactional
    public BookingResponse updateBooking(Long bookingId, UpdateBookingRequest request) {
        Booking booking = bookingRepository.findByBookingId(bookingId);
        if (booking == null) {
            return new BookingResponse(false, "Booking not found");
        }

        if (booking.getBookingStatus() == Booking.BookingStatus.CANCELLED) {
            return new BookingResponse(false, "Cannot update a cancelled booking");
        }

        // Validate room type if changed
        String roomType = request.getRoomType() != null ? request.getRoomType() : booking.getRoomType();
        Room room = roomRepository.findByHotelHotelIdAndRoomType(booking.getHotel().getHotelId(), roomType)
                .orElse(null);
        if (room == null) {
            return new BookingResponse(false, "Room type '" + roomType + "' not found in this hotel");
        }

        // Get dates
        LocalDate checkInDate = request.getCheckInDate() != null ? request.getCheckInDate() : booking.getCheckInDate();
        LocalDate checkOutDate = request.getCheckOutDate() != null ? request.getCheckOutDate() : booking.getCheckOutDate();
        Integer quantity = request.getQuantity() != null ? request.getQuantity() : booking.getQuantity();

        // Validate dates
        if (checkInDate.isAfter(checkOutDate)) {
            return new BookingResponse(false, "Check-in date must be before check-out date");
        }

        // Check availability (excluding current booking)
        Integer currentBookedRooms = bookingRepository.getTotalBookedRooms(
                booking.getHotel().getHotelId(),
                roomType,
                checkInDate,
                checkOutDate
        );

        // Subtract current booking's quantity if room type and dates overlap
        if (roomType.equals(booking.getRoomType()) &&
                checkInDate.isBefore(booking.getCheckOutDate()) &&
                checkOutDate.isAfter(booking.getCheckInDate())) {
            currentBookedRooms -= booking.getQuantity();
        }

        Integer availableRooms = room.getTotalQuantity() - currentBookedRooms;

        if (availableRooms < quantity) {
            return new BookingResponse(false,
                    "Not enough rooms available. Requested: " + quantity +
                            ", Available: " + availableRooms);
        }

        // Calculate total price
        long numberOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        Double totalPrice = room.getPricePerNight() * quantity * numberOfNights;

        // Update booking
        booking.setRoomType(roomType);
        booking.setQuantity(quantity);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setTotalPrice(totalPrice);

        Booking savedBooking = bookingRepository.save(booking);

        return new BookingResponse(true, "Booking updated successfully", savedBooking);
    }

    /**
     * Cancel a booking
     */
    public BookingResponse cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findByBookingId(bookingId);
        if (booking == null) {
            return new BookingResponse(false, "Booking not found");
        }

        if (booking.getBookingStatus() == Booking.BookingStatus.CANCELLED) {
            return new BookingResponse(false, "Booking is already cancelled");
        }

        booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
        Booking savedBooking = bookingRepository.save(booking);

        return new BookingResponse(true, "Booking cancelled successfully", savedBooking);
    }
}
