package com.example.elevator.kafka;

import com.example.elevator.model.Elevator;
import com.example.elevator.model.ElevatorState;
import com.example.elevator.model.Direction;
import com.example.elevator.repository.ElevatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaSimulateListener {
    private final ElevatorRepository elevatorRepo;

    @KafkaListener(topics = "elevator-simulate", groupId = "elevator-group")
    public void listen(String msg) {
        List<Elevator> elevators = elevatorRepo.findByStateNot(ElevatorState.MAINTENANCE);
        for (Elevator e : elevators) {
            if (e.getState() == ElevatorState.MOVING) {
                if (e.getCurrentFloor() < e.getTargetFloor()) e.setCurrentFloor(e.getCurrentFloor()+1);
                else if (e.getCurrentFloor() > e.getTargetFloor()) e.setCurrentFloor(e.getCurrentFloor()-1);
                if (e.getCurrentFloor() == e.getTargetFloor()) {
                    e.setState(ElevatorState.IDLE);
                    e.setDirection(Direction.NONE);
                    e.setTargetFloor(e.getCurrentFloor());
                }
                elevatorRepo.save(e);
            }
        }
    }
}
