package com.example.elevator.controller;

import com.example.elevator.model.*;
import com.example.elevator.repository.ElevatorRepository;
import com.example.elevator.service.ElevatorSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/elevators")
@RequiredArgsConstructor
public class ElevatorController {
    private final ElevatorSchedulerService scheduler;
    private final ElevatorRepository elevatorRepo;
    private final CacheManager cacheManager;

    @PostMapping("/request")
    public ResponseEntity<ElevatorRequest> request(@RequestBody Map<String,Integer> body) {
        int requestFloor = body.get("requestFloor");
        int destFloor = body.get("destinationFloor");
        ElevatorRequest req = scheduler.requestElevator(requestFloor, destFloor);
        if (cacheManager.getCache("status")!=null) cacheManager.getCache("status").clear();
        return ResponseEntity.ok(req);
    }

    @GetMapping("/status")
    @Cacheable("status")
    public ResponseEntity<List<Elevator>> status() {
        return ResponseEntity.ok(elevatorRepo.findAll());
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Elevator> assign(@PathVariable Long id, @RequestBody Map<String,Integer> body) {
        Elevator e = elevatorRepo.findById(id).orElseThrow();
        e.setTargetFloor(body.get("targetFloor"));
        e.setState(ElevatorState.MOVING);
        e.setDirection(e.getCurrentFloor() < e.getTargetFloor() ? Direction.UP : Direction.DOWN);
        elevatorRepo.save(e);
        if (cacheManager.getCache("status")!=null) cacheManager.getCache("status").clear();
        return ResponseEntity.ok(e);
    }

    @PostMapping("/simulate")
    public ResponseEntity<String> simulate() {
        scheduler.simulateMovement();
        return ResponseEntity.accepted().body("Simulation event published");
    }

    @GetMapping("/logs")
    public ResponseEntity<Page<ElevatorRequest>> logs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(scheduler.logs(PageRequest.of(page,size, Sort.by("requestTime").descending())));
    }

    @PutMapping("/{id}/repair")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Elevator> repair(@PathVariable Long id) {
        return ResponseEntity.ok(scheduler.repairElevator(id));
    }

    @GetMapping("/optimize")
    public ResponseEntity<List<Elevator>> optimize() {
        return ResponseEntity.ok(scheduler.optimizeRoutes());
    }
}
