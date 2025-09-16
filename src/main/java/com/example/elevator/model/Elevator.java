package com.example.elevator.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "elevators")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Elevator {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int currentFloor;
    private int targetFloor;
    @Enumerated(EnumType.STRING)
    private ElevatorState state = ElevatorState.IDLE;
    @Enumerated(EnumType.STRING)
    private Direction direction = Direction.NONE;
    private int capacity = 10;
    private LocalDateTime lastMaintenance;
    @OneToMany(mappedBy = "elevator", cascade = CascadeType.ALL)
    private List<ElevatorRequest> requests = new ArrayList<>();
}
