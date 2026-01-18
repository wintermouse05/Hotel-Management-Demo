package com.example.demohotel.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "room")
@Data
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long roomId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hotel_id", referencedColumnName = "id")
    private Hotel hotel;

    @Column(name = "name")
    private String roomName;

    @Column(name = "room_type")
    private String roomType;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "price_per_night")
    private Double pricePerNight;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private boolean status = true;
}
