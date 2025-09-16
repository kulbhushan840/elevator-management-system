package com.example.elevator.repository;

import com.example.elevator.model.Elevator;
import com.example.elevator.model.ElevatorState;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ElevatorRepository extends JpaRepository<Elevator, Long> {
    List<Elevator> findByStateNot(ElevatorState state);
}
