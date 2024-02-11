package com.jorgesacristan.englishCard.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jorgesacristan.englishCard.configuration.Configuration;
import com.jorgesacristan.englishCard.controller.CardController;
import com.jorgesacristan.englishCard.dtos.DeckRequestDto;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.models.Card;
import com.jorgesacristan.englishCard.models.Deck;
import com.jorgesacristan.englishCard.request.CreateCardRequest;
import com.jorgesacristan.englishCard.request.CreateDeckRequest;
import com.jorgesacristan.englishCard.services.CardService;
import com.jorgesacristan.englishCard.services.DeckService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MessageListener {

    private static Logger log = LoggerFactory.getLogger(MessageListener.class);

    @Autowired
    private Configuration configuration;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeckService deckService;

    @Autowired
    private CardService cardService;

    @Autowired
    private MessageSender messageSender;

    @RabbitListener(queues = "${queue.saveCard}")
    public void saveCardFromRabbit(Message message){
        log.info("IN queue.saveCard, message in: " + message);
        try{
            CreateCardRequest cardRequest = objectMapper.readValue(message.getBody(),CreateCardRequest.class);
            log.info("IN queue.saveCard, card: " + cardRequest);
            cardService.save(cardRequest);
            log.info("OUT queue.saveCard, message in: " + message);
        }catch (Exception e){
            log.error(e.getMessage());
            this.messageSender.sendSaveCardToQueueRetry(message);
        }

    }

    @RabbitListener(queues = "${queue.saveDeck}")
    public void saveDeckFromRabbit(Message message){
        log.info(String.format("IN queue.saveDeck, message in: %s ", message));
        try{
            DeckRequestDto deckRequestDto = objectMapper.readValue(message.getBody(), DeckRequestDto.class);
            deckService.saveDeck(deckRequestDto);

        }catch (Exception e){
            log.error(String.format("Error saving deck from message %s error: ", message, e.getMessage()));
            this.messageSender.sendSaveDeckToQueueRetry(message);
        }

    }

    @RabbitListener(queues = "${queue.saveCard.retry}")
    public void processFailedMessagesRequeue_Card(Message failedMessage) {
        Integer retriesCnt = (Integer) failedMessage.getMessageProperties()
                .getHeaders().get(this.configuration.HEADER_X_RETRIES_COUNT);
        if (retriesCnt == null) retriesCnt = 1;
        if (retriesCnt > this.configuration.MAX_RETRIES_COUNT) {
            log.info("Sending message to the parking lot queue");
            this.messageSender.sendSaveCardToQueueError(failedMessage);
            return;
        }

        log.info("Retrying message for the {} time", retriesCnt);
        failedMessage.getMessageProperties()
                .getHeaders().put(this.configuration.HEADER_X_RETRIES_COUNT, ++retriesCnt);
        this.messageSender.sendSaveCardToQueue(failedMessage);
    }

    @RabbitListener(queues = "${queue.saveDeck.retry}")
    public void processFailedMessagesRequeu_Deck(Message failedMessage) {
        Integer retriesCnt = (Integer) failedMessage.getMessageProperties()
                .getHeaders().get(this.configuration.HEADER_X_RETRIES_COUNT);
        if (retriesCnt == null) retriesCnt = 1;
        if (retriesCnt > this.configuration.MAX_RETRIES_COUNT) {
            log.info("Sending message to the parking lot queue");
            //this.messageSender.sendReprocessedMessage(this.configuration.getExchangeSaveDeck(),failedMessage,this.configuration.getRoutingErrorKeySaveDeck(),"");
            this.messageSender.sendSaveDeckToQueueError(failedMessage);
            return;
        }

        log.info("Retrying message for the {} time", retriesCnt);
        failedMessage.getMessageProperties()
                .getHeaders().put(this.configuration.HEADER_X_RETRIES_COUNT, ++retriesCnt);
        this.messageSender.sendSaveDeckToQueue(failedMessage);
    }

}
