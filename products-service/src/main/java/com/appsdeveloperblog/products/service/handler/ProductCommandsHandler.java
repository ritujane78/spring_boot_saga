package com.appsdeveloperblog.products.service.handler;

import com.appsdeveloperblog.core.dto.Product;
import com.appsdeveloperblog.core.dto.commands.CancelProductReservationCommand;
import com.appsdeveloperblog.core.dto.commands.ReserveProductCommand;
import com.appsdeveloperblog.core.dto.events.ProductReservationCancelledEvent;
import com.appsdeveloperblog.core.dto.events.ProductReservationFailedEvent;
import com.appsdeveloperblog.core.dto.events.ProductReservedEvent;
import com.appsdeveloperblog.products.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
@KafkaListener(topics = "${products.commands.topic.name}")
public class ProductCommandsHandler {

    ProductService productService;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String productEventsTopic;

    public ProductCommandsHandler(ProductService productService,
                                  KafkaTemplate<String, Object> kafkaTemplate,
                                  @Value("${products.events.topic.name}") String productEventsTopic) {

        this.kafkaTemplate = kafkaTemplate;
        this.productService = productService;
        this.productEventsTopic = productEventsTopic;
    }

    @KafkaHandler
    public void handleCommands(@Payload ReserveProductCommand command){
        try {
            Product desiredProduct = new Product(command.getProductId(), command.getProductQuantity());
            Product reservedProduct = productService.reserve(desiredProduct, command.getOrderId());
            ProductReservedEvent reservedEvent = new ProductReservedEvent(
                    command.getOrderId(),
                    command.getProductId(),
                    reservedProduct.getPrice(),
                    command.getProductQuantity()
            );
            kafkaTemplate.send(productEventsTopic, reservedEvent);
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            ProductReservationFailedEvent reservationFailedEvent = new ProductReservationFailedEvent(
                    command.getProductId(),
                    command.getOrderId(),
                    command.getProductQuantity()
            );
            kafkaTemplate.send(productEventsTopic,reservationFailedEvent);
        }

    }

    @KafkaHandler
    public void handleCommand(@Payload CancelProductReservationCommand command){
        Product productToCancel = new Product(command.getProductId(), command.getProductQuantity());
        productService.cancelReservation(productToCancel, command.getOrderId());

        ProductReservationCancelledEvent productReservationCancelledEvent = new ProductReservationCancelledEvent(
                command.getProductId(),
                command.getOrderId()
        );

        kafkaTemplate.send(productEventsTopic, productReservationCancelledEvent);
    }
}
