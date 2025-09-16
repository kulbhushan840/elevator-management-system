package com.example.elevator.service;

import com.example.elevator.model.*;
import com.example.elevator.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElevatorSchedulerService {
    private final ElevatorRepository elevatorRepo;
    private final ElevatorRequestRepository requestRepo;
    private final com.example.elevator.kafka.KafkaSimulateProducer simulateProducer;

    @Transactional
    public ElevatorRequest requestElevator(int requestFloor, int destFloor) {
        Direction dir = destFloor > requestFloor ? Direction.UP : Direction.DOWN;
        ElevatorRequest req = new ElevatorRequest();
        req.setRequestFloor(requestFloor);
        req.setDestinationFloor(destFloor);
        req.setDirection(dir);
        req.setRequestTime(LocalDateTime.now());

        Elevator chosen = findBestElevator(requestFloor, dir);
        if (chosen == null) {
            chosen = elevatorRepo.findAll().stream().filter(e -> e.getState() == ElevatorState.IDLE).findFirst().orElse(null);
        }
        if (chosen != null) {
            req.setElevator(chosen);
            chosen.getRequests().add(req);
            chosen.setTargetFloor(destFloor);
            chosen.setState(ElevatorState.MOVING);
            chosen.setDirection(dir);
            elevatorRepo.save(chosen);
        }
        return requestRepo.save(req);
    }

    private Elevator findBestElevator(int requestFloor, Direction dir) {
        List<Elevator> candidates = elevatorRepo.findAll().stream()
            .filter(e -> e.getState() != ElevatorState.MAINTENANCE)
            .collect(Collectors.toList());

        PriorityQueue<Elevator> pq = new PriorityQueue<>(Comparator.comparingInt(e -> Math.abs(e.getCurrentFloor() - requestFloor)));
        for (Elevator e : candidates) {
            if (e.getState() == ElevatorState.IDLE || e.getDirection() == dir || e.getDirection() == Direction.NONE) {
                pq.add(e);
            }
        }
        return pq.isEmpty() ? null : pq.poll();
    }

    public List<Elevator> optimizeRoutes() {
        List<Elevator> list = elevatorRepo.findAll();
        return list.stream().sorted(Comparator.comparingInt(e -> -e.getRequests().size())).collect(Collectors.toList());
    }

    public void simulateMovement() {
        simulateProducer.publish("simulate");
    }

    public Page<ElevatorRequest> logs(Pageable pageable) {
        return requestRepo.findAll(pageable);
    }

    @Transactional
    public Elevator repairElevator(Long id) {
        Elevator e = elevatorRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        e.setState(ElevatorState.IDLE);
        e.setLastMaintenance(LocalDateTime.now());
        return elevatorRepo.save(e);
    }
}
