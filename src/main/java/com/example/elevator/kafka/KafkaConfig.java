package com.example.elevator.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic simulateTopic() {
        return new NewTopic("elevator-simulate", 1, (short)1);
    }
}
