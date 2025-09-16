package com.example.elevator;

import com.example.elevator.model.Elevator;
import com.example.elevator.repository.ElevatorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ElevatorRepositoryTest {
    @Autowired ElevatorRepository repo;

    @Test
    void contextLoadsAndSave() {
        Elevator e = new Elevator();
        e.setCurrentFloor(0);
        e.setTargetFloor(0);
        repo.save(e);
        assertThat(repo.findAll()).isNotEmpty();
    }
}
