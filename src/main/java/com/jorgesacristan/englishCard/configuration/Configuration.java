package com.jorgesacristan.englishCard.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Qualifier("configuration")
public class Configuration {

    public final static String API_V1_PREFIX = "/api/v1";
    public final static int LEVEL_MAX = 100;
    public final static int EXPERIENCE_MAX = 1000;
    public static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-Id";
    public static final String HEADER_X_RETRIES_COUNT = "X-retry";

    public static final int MAX_RETRIES_COUNT = 3;

    @Value("${urlaws}")
    private String urlAws;

    public String getUrlAws() {
        return urlAws;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    //RABBITMQ - CONFIG

    @Value("${spring.rabbitmq.host}")
    private String hostName;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${sprint.rabbitmq.virtual-host}")
    private String virtualHost;

    @Value("${spring.rabbitmq.port}")
    private int port;

    //ROUTING KEY - SAVE CARD
    @Value("${routing.key.saveCard}")
    private String routingKeySaveCard;

    @Value("${routing.retry.key.saveCard}")
    private String routingRetryKeySaveCard;

    @Value("${routing.error.key.saveCard}")
    private String routingErrorKeySaveCard;

    //QUEUE - CARDS SAVE
    @Value("${exchange.saveCard}")
    private String exchangeSaveCard;

    @Value("${queue.saveCard}")
    private String queueSaveCard;

    @Value("${queue.saveCard.retry}")
    private String queueSaveCardRetry;

    @Value("${queue.saveCard.error}")
    private String queueSaveCardError;

    //ROUTING KEY - SAVE DECK
    @Value("${routing.key.saveDeck}")
    private String routingKeySaveDeck;

    @Value("${routing.retry.key.saveDeck}")
    private String routingRetryKeySaveDeck;

    @Value("${routing.error.key.saveDeck}")
    private String routingErrorKeySaveDeck;


    //QUEUE - DECKS SAVE

    @Value("${exchange.saveDeck}")
    private String exchangeSaveDeck;

    @Value("${queue.saveDeck}")
    private String queueSaveDeck;

    @Value("${queue.saveDeck.retry}")
    private String queueSaveDeckRetry;

    @Value("${queue.saveDeck.error}")
    private String queueSaveDeckError;


    public String getHostName() {
        return hostName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public int getPort() {
        return port;
    }

    public String getRoutingKeySaveCard() {
        return routingKeySaveCard;
    }

    public String getRoutingRetryKeySaveCard() {
        return routingRetryKeySaveCard;
    }

    public String getRoutingErrorKeySaveCard() {
        return routingErrorKeySaveCard;
    }

    public String getExchangeSaveCard() {
        return exchangeSaveCard;
    }

    public String getQueueSaveCard() {
        return queueSaveCard;
    }

    public String getRoutingKeySaveDeck() {
        return routingKeySaveDeck;
    }

    public String getRoutingRetryKeySaveDeck() {
        return routingRetryKeySaveDeck;
    }

    public String getRoutingErrorKeySaveDeck() {
        return routingErrorKeySaveDeck;
    }

    public String getExchangeSaveDeck() {
        return exchangeSaveDeck;
    }

    public String getQueueSaveDeck() {
        return queueSaveDeck;
    }

    public String getQueueSaveCardRetry() {
        return queueSaveCardRetry;
    }

    public String getQueueSaveCardError() {
        return queueSaveCardError;
    }

    public String getQueueSaveDeckRetry() {
        return queueSaveDeckRetry;
    }

    public String getQueueSaveDeckError() {
        return queueSaveDeckError;
    }
}
