package com.appsdeveloperblog.products.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {
    @Value("${products.events.topic.name}")
    private String productEventsTopicName;

    private final static Integer TOPIC_REPLICATIONS_FACTOR = 3;
    private final static Integer TOPIC_PARTITIONS=3;

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    NewTopic createProductEventsTopic(){
        return TopicBuilder.name(productEventsTopicName)
                .replicas(TOPIC_REPLICATIONS_FACTOR)
                .partitions(TOPIC_PARTITIONS)
                .build();
    }
}
