package com.appsdeveloperblog.orders.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.internals.Topic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

    @Value("${orders.events.topic.name}")
    private String ordersEventsTopicName;

    @Value("${products.commands.topic.name}")
    private String productsCommandsTopicName;

    @Value("${payments.commands.topic.name}")
    private String paymentsCommandsTopicName;

    @Value("${orders.commands.topic.name}")
    private String ordersCommandsTopicName;

    private final static Integer TOPIC_REPLICATION_FACTOR=3;
    private final static Integer TOPIC_PARTITIONS=3;

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    NewTopic createOrdersEventsTopic(){
        return TopicBuilder.name(ordersEventsTopicName)
                .replicas(TOPIC_REPLICATION_FACTOR)
                .partitions(TOPIC_PARTITIONS)
                .build();
    }

    @Bean
    NewTopic productsCommandsTopic(){
        return TopicBuilder.name(productsCommandsTopicName)
                .partitions(TOPIC_PARTITIONS)
                .replicas(TOPIC_REPLICATION_FACTOR)
                .build();
    }

    @Bean
    NewTopic createPaymentsCommandsTopic(){
        return TopicBuilder.name(paymentsCommandsTopicName)
                .partitions(TOPIC_PARTITIONS)
                .replicas(TOPIC_REPLICATION_FACTOR)
                .build();
    }

    @Bean
    NewTopic createOrdersCommandsTopic(){
        return TopicBuilder.name(ordersCommandsTopicName)
                .replicas(TOPIC_REPLICATION_FACTOR)
                .partitions(TOPIC_PARTITIONS)
                .build();
    }

}
