package com.appsdeveloperblog.orders.saga;


import com.appsdeveloperblog.core.dto.commands.*;
import com.appsdeveloperblog.core.dto.events.*;
import com.appsdeveloperblog.core.types.OrderStatus;
import com.appsdeveloperblog.orders.service.OrderHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"${orders.events.topic.name}", "${products.events.topic.name}", "${payments.events.topic.name}"})
public class OrderSaga {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String productsCommandsTopicName;

    private final OrderHistoryService orderHistoryService;

    private final String paymentsCommandsTopicName;

    private final String ordersCommandsTopicName;


    public OrderSaga(KafkaTemplate<String, Object> kafkaTemplate,
                     @Value("${products.commands.topic.name}") String productsCommandsTopicName,
                     OrderHistoryService orderHistoryService,
                     @Value("${payments.commands.topic.name}") String paymentsCommandsTopicName,
                     @Value("${orders.commands.topic.name}") String orderCommandsTopicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.productsCommandsTopicName = productsCommandsTopicName;
        this.orderHistoryService = orderHistoryService;
        this.paymentsCommandsTopicName = paymentsCommandsTopicName;
        this.ordersCommandsTopicName = orderCommandsTopicName;
    }

    @KafkaHandler
    public void handleEvent(@Payload OrderCreatedEvent orderCreatedEvent){
        ReserveProductCommand reserveProductCommand = new ReserveProductCommand(
                orderCreatedEvent.getProductId(),
                orderCreatedEvent.getOrderId(),
                orderCreatedEvent.getProductQuantity()
        );
        kafkaTemplate.send(productsCommandsTopicName, reserveProductCommand);
        orderHistoryService.add(orderCreatedEvent.getOrderId(), OrderStatus.CREATED);
    }

    @KafkaHandler
    public void handleEvent(@Payload ProductReservedEvent event){
        ProcessPaymentCommand command = new ProcessPaymentCommand(
                event.getProductId(),
                event.getOrderId(),
                event.getProductPrice(),
                event.getProductQuantity()
        );
        kafkaTemplate.send(paymentsCommandsTopicName, command );
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentProcessedEvent event){
        System.out.println("events1 = " +event);
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(
                event.getOrderId()
        );

        kafkaTemplate.send(ordersCommandsTopicName, approveOrderCommand);
    }
    @KafkaHandler
    public void handleEvent(@Payload OrderApprovedEvent event){
        System.out.println("events2 = " +event);
        orderHistoryService.add(event.getOrderId(), OrderStatus.APPROVED);
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentsFailedEvent event){
        CancelProductReservationCommand cancelProductReservationCommand = new CancelProductReservationCommand(
                event.getProductId(),
                event.getOrderId(),
                event.getProductQuantity()
        );
        kafkaTemplate.send(productsCommandsTopicName, cancelProductReservationCommand);
    }
    @KafkaHandler
    public void handleEvent(@Payload ProductReservationCancelledEvent event){
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(event.getOrderId());
        kafkaTemplate.send(ordersCommandsTopicName, rejectOrderCommand);

        orderHistoryService.add(event.getOrderId(), OrderStatus.REJECTED);
    }
}
