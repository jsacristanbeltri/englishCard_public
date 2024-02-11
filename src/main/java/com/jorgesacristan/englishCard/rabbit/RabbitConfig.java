package com.jorgesacristan.englishCard.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

@Configuration
public class RabbitConfig implements RabbitListenerConfigurer {

    @Autowired
    public com.jorgesacristan.englishCard.configuration.Configuration configuration;

    @Autowired
    private RabbitProperties rabbitProperties;


    @Primary
    @Bean(name="mainConnectionFactory")
    public ConnectionFactory eventConnectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(this.configuration.getHostName());
        connectionFactory.setPort(this.configuration.getPort());
        connectionFactory.setUsername(this.configuration.getUsername());
        connectionFactory.setPassword(this.configuration.getPassword());
        return connectionFactory;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(this.messageHandlerMethodFactory());
    }

    @Bean
    MessageHandlerMethodFactory messageHandlerMethodFactory(){
        final DefaultMessageHandlerMethodFactory messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
        messageHandlerMethodFactory.setMessageConverter(this.consumerJackson2MessageConverter());
        return messageHandlerMethodFactory;
    }

    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter(){
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(@Qualifier("mainConnectionFactory") final ConnectionFactory connectionFactory){
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(this.producerJackson2MessageConverter());
        rabbitTemplate.setChannelTransacted(true);
        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(@Qualifier("mainConnectionFactory") final ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    // Exchanges

    @Bean
    TopicExchange exchangeSaveCard(final RabbitAdmin rabbitAdmin) {
        return ExchangeBuilder.topicExchange(this.configuration.getExchangeSaveCard())
                .durable(true)
                .admins(rabbitAdmin)
                .build();
    }

    @Bean
    public TopicExchange exchangeSaveDeck(final RabbitAdmin rabbitAdmin) {
        return ExchangeBuilder.topicExchange(this.configuration.getExchangeSaveDeck())
                .durable(true)
                .admins(rabbitAdmin)
                .build();
    }



    //--------   Q_saveCard ---------------

    @Bean
    public Queue queueSaveCard() {
        Queue queue = QueueBuilder
                .durable(this.configuration.getQueueSaveCard())
                .deadLetterExchange(this.configuration.getExchangeSaveCard())
                .deadLetterRoutingKey(this.configuration.getRoutingRetryKeySaveCard())
                .build();
        //queue.setAdminsThatShouldDeclare(rabbitAdmin);
        return queue;
    }

    @Bean
    Binding bindingSaveCard(@Qualifier("queueSaveCard")final Queue queue,final TopicExchange exchangeSaveCard) {
        Binding binding = BindingBuilder.bind(queue).to(exchangeSaveCard).with(this.configuration.getRoutingKeySaveCard());
        //binding.setAdminsThatShouldDeclare(rabbitAdmin);
        return binding;
    }

    //----------------- Q_SaveCard_Retry

    @Bean
    public Queue queueSaveCardRetry() {
        Queue queue = QueueBuilder
                .durable(this.configuration.getQueueSaveCardRetry())
                .deadLetterExchange(this.configuration.getExchangeSaveCard())
                .deadLetterRoutingKey(this.configuration.getRoutingKeySaveCard())
                .build();
        //queue.setAdminsThatShouldDeclare(rabbitAdmin);
        return queue;
    }

    @Bean
    Binding bindingSaveCardRetry(@Qualifier("queueSaveCardRetry")final Queue queue,final TopicExchange exchangeSaveCard) {
        Binding binding = BindingBuilder.bind(queue).to(exchangeSaveCard).with(this.configuration.getRoutingRetryKeySaveCard());
        //binding.setAdminsThatShouldDeclare(rabbitAdmin);
        return binding;
    }

    //------------- Q_SaveCard_error

    @Bean
    public Queue queueSaveCardError() {
        Queue queue = QueueBuilder.durable(this.configuration.getQueueSaveCardError()).build();
        //queue.setAdminsThatShouldDeclare(rabbitAdmin);
        return queue;
    }

    @Bean
    Binding bindingSaveCardError(@Qualifier("queueSaveCardError")final Queue queue,final TopicExchange exchangeSaveCard) {
        Binding binding = BindingBuilder.bind(queue).to(exchangeSaveCard).with(this.configuration.getRoutingErrorKeySaveCard());
        //binding.setAdminsThatShouldDeclare(rabbitAdmin);
        return binding;
    }


    //------------- Q_SaveDeck

    @Bean
    public Queue queueSaveDeck() {
        Queue queue = QueueBuilder.durable(this.configuration.getQueueSaveDeck()).build();
        //queue.setAdminsThatShouldDeclare(rabbitAdmin);
        return queue;
    }

    @Bean
    Binding bindingSaveDeck(@Qualifier("queueSaveDeck")final Queue queueSaveDeck,final TopicExchange exchangeSaveDeck) {
        Binding binding = BindingBuilder.bind(queueSaveDeck).to(exchangeSaveDeck).with(this.configuration.getRoutingKeySaveDeck());
        //binding.setAdminsThatShouldDeclare(rabbitAdmin);
        return binding;
    }

    //------------- Q_SaveDeck_Retry

    @Bean
    public Queue queueSaveDeckRetry() {
        Queue queue = QueueBuilder
                .durable(this.configuration.getQueueSaveDeckRetry())
                .deadLetterExchange(this.configuration.getExchangeSaveDeck())
                .deadLetterRoutingKey(this.configuration.getRoutingRetryKeySaveDeck())
                .build();
        //queue.setAdminsThatShouldDeclare(rabbitAdmin);
        return queue;
    }

    @Bean
    Binding bindingSaveDeckRetry(@Qualifier("queueSaveDeckRetry")final Queue queue,final TopicExchange exchangeSaveDeck) {
        Binding binding = BindingBuilder.bind(queue).to(exchangeSaveDeck).with(this.configuration.getRoutingRetryKeySaveDeck());
        //binding.setAdminsThatShouldDeclare(rabbitAdmin);
        return binding;
    }

    //------------- Q_SaveDeck_Error

    @Bean
    public Queue queueSaveDeckError() {
        Queue queue = QueueBuilder.durable(this.configuration.getQueueSaveDeckError()).build();
        //queue.setAdminsThatShouldDeclare(rabbitAdmin);
        return queue;
    }

    @Bean
    Binding bindingSaveDeckError(@Qualifier("queueSaveDeckError")final Queue queueSaveDeck,final TopicExchange exchangeSaveDeck) {
        Binding binding = BindingBuilder.bind(queueSaveDeck).to(exchangeSaveDeck).with(this.configuration.getRoutingErrorKeySaveDeck());
        //binding.setAdminsThatShouldDeclare(rabbitAdmin);
        return binding;
    }
}
