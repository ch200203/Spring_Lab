package com.study.springkafkastream;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

public class Pipe {

    public static void main(String[] args) throws Exception {
        // Streams 실행 구성 값을 지정하는 맵을 생성
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Kafka 클러스터에 대한 초기 연결을 설정

        // 동일한 맵에서 다른 구성(예: 레코드 키-값 쌍에 대한 기본 직렬화 및 역직렬화 라이브러리)을 사용자 정의
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // Streams 애플리케이션의 논리를 정의
        final StreamsBuilder builder = new StreamsBuilder();

        // KStream<String, String> source = builder.stream("streams-plaintext-input");
        // source.to("streams-pipe-output");

        // 한줄러 처리 가능
        builder.stream("streams-plaintext-input").to("streams-pipe-output");

        // topology다음을 수행하여 이 빌더에서 어떤 종류가 생성되었는지 검사
        final Topology topology = builder.build();

        System.out.println(topology.describe());

        final KafkaStreams streams = new KafkaStreams(topology, properties);
        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });


        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

}
