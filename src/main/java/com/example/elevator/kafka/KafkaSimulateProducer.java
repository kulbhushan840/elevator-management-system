package com.example.elevator.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaSimulateProducer {
    private final KafkaTemplate<String,String> kafkaTemplate;
    public void publish(String msg) {
        kafkaTemplate.send("elevator-simulate", msg);
    }
}
