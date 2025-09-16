package com.example.elevator.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "elevator_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElevatorRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int requestFloor;
    private int destinationFloor;
    @Enumerated(EnumType.STRING)
    private Direction direction;
    private LocalDateTime requestTime;
    @ManyToOne
    private Elevator elevator;
}
