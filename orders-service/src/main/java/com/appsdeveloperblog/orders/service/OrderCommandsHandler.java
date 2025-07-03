package com.appsdeveloperblog.orders.service;

import com.appsdeveloperblog.core.dto.commands.ApproveOrderCommand;
import com.appsdeveloperblog.core.dto.commands.RejectOrderCommand;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${orders.commands.topic.name}")
public class OrderCommandsHandler {

    private OrderService orderService;

    public OrderCommandsHandler(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaHandler
    public void handleCommand(@Payload ApproveOrderCommand command){
        orderService.approveOrder(command.getOrderId());
    }
    @KafkaHandler
    public void handleCommand(@Payload RejectOrderCommand rejectOrderCommand){
        orderService.rejectOrder(rejectOrderCommand.getOrderId());
    }
}
