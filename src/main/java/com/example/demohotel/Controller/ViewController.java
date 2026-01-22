package com.example.demohotel.Controller;

import com.example.demohotel.entity.Booking;
import com.example.demohotel.entity.Hotel;
import com.example.demohotel.entity.Room;
import com.example.demohotel.entity.User;
import com.example.demohotel.service.BookingService;
import com.example.demohotel.service.HotelService;
import com.example.demohotel.service.RoomService;
import com.example.demohotel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("")
public class ViewController {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    /**
     * Home page
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalHotels", hotelService.getAllHotels().size());
        model.addAttribute("totalRooms", roomService.getAllRooms().size());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalBookings", bookingService.getAllBookings().size());
        return "index";
    }

    /**
     * Hotels list page
     */
    @GetMapping("/hotels")
    public String hotelsList(Model model) {
        List<Hotel> hotels = hotelService.getAllHotels();
        model.addAttribute("hotels", hotels);
        return "hotels/list";
    }

    /**
     * Hotel detail page
     */
    @GetMapping("/hotels/{hotelId}")
    public String hotelDetail(@PathVariable Long hotelId, Model model) {
        Hotel hotel = hotelService.getHotelById(hotelId);
        List<Room> rooms = roomService.getRoomsByHotelId(hotelId);
        model.addAttribute("hotel", hotel);
        model.addAttribute("rooms", rooms);
        return "hotels/detail";
    }

    /**
     * Rooms list page
     */
    @GetMapping("/rooms")
    public String roomsList(Model model) {
        List<Room> rooms = roomService.getAllRooms();
        model.addAttribute("rooms", rooms);
        return "rooms/list";
    }

    /**
     * Users list page
     */
    @GetMapping("/users")
    public String usersList(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/list";
    }

    /**
     * User detail page with bookings
     */
    @GetMapping("/users/{userId}")
    public String userDetail(@PathVariable Long userId, Model model) {
        User user = userService.getUserById(userId);
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        model.addAttribute("user", user);
        model.addAttribute("bookings", bookings);
        return "users/detail";
    }

    /**
     * Bookings list page
     */
    @GetMapping("/bookings")
    public String bookingsList(Model model) {
        List<Booking> bookings = bookingService.getAllBookings();
        model.addAttribute("bookings", bookings);
        return "bookings/list";
    }

    /**
     * Booking detail page
     */
    @GetMapping("/bookings/{bookingId}")
    public String bookingDetail(@PathVariable Long bookingId, Model model) {
        Booking booking = bookingService.getBookingById(bookingId);
        model.addAttribute("booking", booking);
        return "bookings/detail";
    }
}
