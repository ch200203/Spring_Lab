package com.study.springkafka.config.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private static final String TOPIC = "test_topic";
    private final KafkaTemplate<String, String> kafkaTemplate;

    // KafkaTemplate을 통해 해당되는 TOPIC에 메시지를 전달
    public void sendMessage(String message) {
        System.out.println(String.format("Produce message : %s", message));
        kafkaTemplate.send(TOPIC, message);
    }

}
