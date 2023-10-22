package com.study.springkafka.config.consumer;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    @KafkaListener(
        topics = "test_topic",
        groupId = "test"
    )
    public void listen(String message) throws IOException {
        // KafkaListener를 통해 topic과 groupId에 해당되는 메시지를 소비한다.
        System.out.println(String.format("Consumed message : %s", message));
    }


}
