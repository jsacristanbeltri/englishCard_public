package com.jorgesacristan.englishCard.rabbit;

import com.jorgesacristan.englishCard.configuration.Configuration;
import com.jorgesacristan.englishCard.models.Card;
import com.jorgesacristan.englishCard.response.StandardResponse;
import com.jorgesacristan.englishCard.utils.CorrelationIdUtils;
import org.springframework.amqp.core.Correlation;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class MessageSender {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public Configuration configuration;

    @Autowired
    private CorrelationIdUtils correlationIdUtils;

    @Autowired
    public MessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendSaveCardToQueue(final Message message) {
        String correlationId = correlationIdUtils.getActualCorrelationIdOrNew();
        message.getMessageProperties().setHeader(Configuration.CORRELATION_ID_HEADER_NAME, correlationId);
        message.getMessageProperties().setCorrelationId(correlationId);
        this.rabbitTemplate.convertAndSend(configuration.getExchangeSaveCard(), this.configuration.getRoutingKeySaveCard() , message);
    }

    public String sendSaveDeckToQueue(final Message message) {
        String correlationId = correlationIdUtils.getActualCorrelationIdOrNew();
        message.getMessageProperties().setHeader(Configuration.CORRELATION_ID_HEADER_NAME, correlationId);
        message.getMessageProperties().setCorrelationId(correlationId);
        this.rabbitTemplate.convertAndSend(configuration.getExchangeSaveDeck(), this.configuration.getRoutingKeySaveDeck() , message);
        return correlationId;
    }

    public void sendReprocessedMessage(String exchange,Message message, String routingKey,
                                       String expiration){
        if(expiration!=null)
            message.getMessageProperties().setExpiration(expiration);


        message.getMessageProperties().setHeader(this.configuration.CORRELATION_ID_HEADER_NAME, correlationIdUtils.getActualCorrelationIdOrNew());
        message.getMessageProperties().setContentType(MediaType.APPLICATION_JSON_VALUE);

        this.rabbitTemplate.convertAndSend(exchange,routingKey,message);

    }

    public void sendSaveDeckToQueueRetry(final Message object) {
        this.rabbitTemplate.convertAndSend(configuration.getExchangeSaveDeck(), this.configuration.getRoutingRetryKeySaveDeck() , object);
    }

    public void sendSaveDeckToQueueError(final Message object) {
        this.rabbitTemplate.convertAndSend(configuration.getExchangeSaveDeck(), this.configuration.getRoutingErrorKeySaveDeck() , object);
    }

    public void sendSaveCardToQueueRetry(final Message object) {
        this.rabbitTemplate.convertAndSend(configuration.getExchangeSaveCard(), this.configuration.getRoutingRetryKeySaveCard() , object);
    }

    public void sendSaveCardToQueueError(final Message object) {
        this.rabbitTemplate.convertAndSend(configuration.getExchangeSaveCard(), this.configuration.getRoutingErrorKeySaveCard() , object);
    }

}
