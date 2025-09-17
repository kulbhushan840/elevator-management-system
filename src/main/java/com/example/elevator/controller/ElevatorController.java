package com.example.elevator.controller;

import com.example.elevator.model.*;
import com.example.elevator.repository.ElevatorRepository;
import com.example.elevator.service.ElevatorSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
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
    private final CacheManager cacheManager; // Redis cache manager

    @PostMapping("/request")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<ElevatorRequest> request(@RequestBody Map<String,Integer> body) {
        int requestFloor = body.get("requestFloor");
        int destFloor = body.get("destinationFloor");
        ElevatorRequest req = scheduler.requestElevator(requestFloor, destFloor);

        // Clear cached elevator status whenever a new request comes in
        Cache cache = cacheManager.getCache("status");
        if (cache != null) cache.clear();

        return ResponseEntity.ok(req);
    }

    @GetMapping("/status")
    @Cacheable("status") // Cached in Redis automatically
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

        // Clear cache so next GET /status reflects changes
        Cache cache = cacheManager.getCache("status");
        if (cache != null) cache.clear();

        return ResponseEntity.ok(e);
    }

    @PostMapping("/simulate")
    public ResponseEntity<String> simulate() {
        scheduler.simulateMovement(); // This should publish Kafka events asynchronously
        return ResponseEntity.accepted().body("Simulation event published");
    }

    @GetMapping("/logs")
    public ResponseEntity<Page<ElevatorRequest>> logs(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                scheduler.logs(PageRequest.of(page, size, Sort.by("requestTime").descending()))
        );
    }

    @PutMapping("/{id}/repair")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Elevator> repair(@PathVariable Long id) {
        Elevator repaired = scheduler.repairElevator(id);
        // Clear cache to reflect repaired elevator
        Cache cache = cacheManager.getCache("status");
        if (cache != null) cache.clear();
        return ResponseEntity.ok(repaired);
    }

    @GetMapping("/optimize")
    public ResponseEntity<List<Elevator>> optimize() {
        List<Elevator> optimized = scheduler.optimizeRoutes();
        // Clear cache after optimization
        Cache cache = cacheManager.getCache("status");
        if (cache != null) cache.clear();
        return ResponseEntity.ok(optimized);
    }
}
