package com.example.demohotel.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import org.apache.logging.log4j.message.AsynchronouslyFormattable;

@Entity
@Table(name="hotel")

@Data
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id")
    private Long hotelId;

    @Column(name ="name")
    private String hotelName;

    @Column(name ="status")
    private boolean status = true;

    @Transient
    private Integer rate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="address_id", referencedColumnName = "id")
    private Address address;

}
