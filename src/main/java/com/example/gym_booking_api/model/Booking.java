package com.example.gym_booking_api.model;

import com.example.gym_booking_api.dto.BookingRequest;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "number_of_people", nullable = false)
    private Integer numberOfPeople;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private String activity;

    protected Booking() {
    }

    public Booking(BookingRequest request) {
        updateFrom(request);
    }

    public void updateFrom(BookingRequest request) {
        this.name = request.name();
        this.email = request.email();
        this.dateTime = request.dateTime();
        this.numberOfPeople = request.numberOfPeople();
        this.status = request.status();
        this.activity = request.activity();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public String getActivity() {
        return activity;
    }
}