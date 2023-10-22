package com.study.springkafka;

import com.study.springkafka.config.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaProducer kafkaProducer;

    @PostMapping("/send")
    public void sendMessage() {
        String message = "test message";
        kafkaProducer.sendMessage(message);
    }


}
