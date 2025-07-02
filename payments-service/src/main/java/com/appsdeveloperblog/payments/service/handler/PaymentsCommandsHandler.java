package com.appsdeveloperblog.payments.service.handler;

import com.appsdeveloperblog.core.dto.Payment;
import com.appsdeveloperblog.core.dto.commands.ProcessPaymentCommand;
import com.appsdeveloperblog.core.dto.events.PaymentProcessedEvent;
import com.appsdeveloperblog.core.dto.events.PaymentsFailedEvent;
import com.appsdeveloperblog.core.exceptions.CreditCardProcessorUnavailableException;
import com.appsdeveloperblog.payments.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics={"${payments.commands.topic.name}"})
public class PaymentsCommandsHandler {
    PaymentService paymentService;
    Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    KafkaTemplate<String, Object> kafkaTemplate;

    public final String paymentEventsTopicName;

    public PaymentsCommandsHandler(PaymentService paymentService,
                                   KafkaTemplate<String, Object> kafkaTemplate,
                                   @Value("${payments.events.topic.name}") String paymentEventsTopicName) {
        this.paymentService = paymentService;
        this.kafkaTemplate = kafkaTemplate;
        this.paymentEventsTopicName = paymentEventsTopicName;
    }

    @KafkaHandler
    public void handleCommand(@Payload ProcessPaymentCommand command){
        try {
            System.out.println("payment = " + command);
            Payment payment = new Payment(command.getOrderId(),
                    command.getProductId(),
                    command.getProductPrice(),
                    command.getProductQuantity()
                    );

            Payment processedPayment = paymentService.process(payment);

            PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(
                    processedPayment.getId(),
                    processedPayment.getOrderId()
            );
            kafkaTemplate.send(paymentEventsTopicName, paymentProcessedEvent);

        } catch (CreditCardProcessorUnavailableException e) {
            LOGGER.error(e.getLocalizedMessage(),e);
            PaymentsFailedEvent paymentsFailedEvent = new PaymentsFailedEvent(
                    command.getOrderId(),
                    command.getProductId(),
                    command.getProductQuantity()
            );
            kafkaTemplate.send(paymentEventsTopicName, paymentsFailedEvent);
        }

    }
}
