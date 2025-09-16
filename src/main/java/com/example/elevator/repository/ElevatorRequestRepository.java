package com.example.elevator.repository;

import com.example.elevator.model.ElevatorRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ElevatorRequestRepository extends JpaRepository<ElevatorRequest, Long> {
    Page<ElevatorRequest> findAll(Pageable pageable);
}
