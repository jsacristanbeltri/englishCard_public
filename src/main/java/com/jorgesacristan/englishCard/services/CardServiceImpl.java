package com.jorgesacristan.englishCard.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jorgesacristan.englishCard.configuration.Configuration;
import com.jorgesacristan.englishCard.dtos.CreateCardDto;
import com.jorgesacristan.englishCard.enums.UserRole;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.exceptions.BaseExceptionEnum;
import com.jorgesacristan.englishCard.models.Card;
import com.jorgesacristan.englishCard.models.Deck;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.rabbit.MessageSender;
import com.jorgesacristan.englishCard.repositories.CardRepository;
import com.jorgesacristan.englishCard.repositories.DeckRepository;
import com.jorgesacristan.englishCard.request.CreateCardRequest;
import com.jorgesacristan.englishCard.response.ApiResponse;
import com.jorgesacristan.englishCard.response.ResponseBuilder;
import com.jorgesacristan.englishCard.response.StandardResponse;
import com.jorgesacristan.englishCard.utils.CorrelationIdUtils;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService{

    private static Logger log = LoggerFactory.getLogger(CardServiceImpl.class);

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private DeckServiceImpl deckService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private ResponseBuilder responseBuilder;

    @Autowired
    private CorrelationIdUtils correlationIdUtils;


    @Override
    public List<Card> findAll() {
        return cardRepository.findAll();
    }

    @Override
    public void save(CreateCardRequest cardRequest) throws BaseException {
        try{
            Optional<Deck> deck = deckService.findDeck(cardRequest.getIdDeck());
            if(deck.isPresent()){
                Card card = new Card(cardRequest.getName1(),cardRequest.getName2(),true,null,0,deck.get());
                //deck.get().getCards().add(card);
                //deckRepository.save(deck.get());
                cardRepository.save(card);
            }
        }catch (Exception e){
            throw new BaseException("Error saving card", HttpStatus.CREATED.toString());
        }
    }

    @Override
    public ResponseEntity<?> updateCard(Long id, CreateCardDto createCardDto, Authentication userLoged) throws BaseException{
        log.info("IN CARD updateCard id: " + id +", User: " + userLoged.getName());
        Card card;
        Card cardSaved = null;
        try{
            if(userLoged==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            card = this.findCardById(id);

            card.setId(id);
            card.setEnable(createCardDto.getEnable());
            card.setName1(createCardDto.getName1());
            card.setName2(createCardDto.getName2());
            //TODO se llama dos veces al mismo repositorio
            cardSaved = cardRepository.save(card);

        }catch (BaseException e){
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(Configuration.CORRELATION_ID_HEADER_NAME, correlationIdUtils.generateAndStoreId(null));
        correlationIdUtils.removeCorrelationId();

        log.info("OUT CARD updateCard, card updated: " + cardSaved);
        return responseBuilder.buildResponse(
                headers,
                HttpStatus.OK.value(),
                String.format("Card with id %s updated satisfactory", card.getId()),
                cardSaved);
    }

    @Override
    public ResponseEntity<?> getCardsByIdDeck (Long idDeck, Authentication userLoged) throws BaseException {
        log.info("IN CARD getCardsByIdDeck idDeck: " + idDeck +", User: " + userLoged.getName());
        Optional<Deck> deck;

        try {
            deck = deckRepository.findById(idDeck);
            if(deck.isPresent()){
                if (!userLoged.getAuthorities().contains(UserRole.ROLE_ADMIN)) {
                    if (!deck.get().getUser().getUsername().equals(userLoged.getName()))
                        throw new BaseException("You don't have permissions to access to the deck with id: " + idDeck, HttpStatus.UNAUTHORIZED.toString());
                    else{
                        if (deck.get().getCards().isEmpty())
                            throw new BaseException(String.format("The deck with id %s have not cards",idDeck), HttpStatus.NOT_FOUND.toString());
                    }
                }
            }else
                throw new BaseException(String.format("Deck with id %s not found ", idDeck), HttpStatus.NOT_FOUND.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.add(Configuration.CORRELATION_ID_HEADER_NAME, correlationIdUtils.generateAndStoreId(null));
            correlationIdUtils.removeCorrelationId();

            log.info("OUT CARD getCardsByIdDeck, cards response: " + deck.get().getCards() +", User: " + userLoged.getName());

            return responseBuilder.buildResponse(
                    headers,
                    HttpStatus.OK.value(),
                    String.format("Cards from deck with id %s got satisfactory", idDeck),
                    deck.get().getCards());

        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }catch (Exception e){
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public Card findCardById (Long id) throws BaseException {
        return cardRepository.findById(id).orElseThrow(() -> new BaseException(String.format("Card with id %s not found on database", id), HttpStatus.NOT_FOUND.toString()));
    }

    @Override
    public ResponseEntity<?> addCard(CreateCardRequest cardRequest, Authentication user) throws BaseException {
        log.info("IN CARD, addCard cardName: " + cardRequest + ", User: " + user);

        try{
            Optional<Deck> deck = deckService.findDeck(cardRequest.getIdDeck());
            if(!deck.isPresent())
                throw new BaseException(String.format("Deck with id %s not found ", cardRequest.getIdDeck()), HttpStatus.NOT_FOUND.toString());


            Card card = new Card(cardRequest.getName1(),cardRequest.getName2(),true,null,0,deck.get());
            //deck.get().getCards().add(card);
            //deckRepository.save(deck.get());
            //Card cardSaved = cardRepository.save(card);

            //TODO Meter en cabecera el UUID
            Message message = MessageBuilder
                    .withBody(objectMapper.writeValueAsBytes(cardRequest))
                    .build();
            messageSender.sendSaveCardToQueue(message);

            //this.sendSaveCard(cardRequest);
            log.info("OUT CARD, addCard, card saved: " + card + ", User: " + user);

            HttpHeaders headers = new HttpHeaders();
            headers.add(Configuration.CORRELATION_ID_HEADER_NAME, correlationIdUtils.generateAndStoreId(null));
            correlationIdUtils.removeCorrelationId();

            return responseBuilder.buildResponse(
                    headers,
                    HttpStatus.OK.value(),
                    String.format("Card saved satisfactory on deck with id %s", deck.get().getId()),
                    card);
        }
        catch (BaseException e){
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }

    }

    @Override
    public ResponseEntity<ApiResponse> getCardById(Long id, Authentication userLoged) throws BaseException {
        log.info("IN CARD findById id: " + id +", User: " + userLoged.getName());
        Card card;
        card = this.findCardById(id);

        log.info("OUT CARD, getCardById, card: " + card);
        return responseBuilder.buildResponse(
                HttpStatus.OK.value(),
                String.format("Card with id %s got succesfully", id),
                card
        );
    }


    @Override
    public ResponseEntity<?> deleteCardById (Long id, Authentication userLoged) throws BaseException {
        log.info("IN CARD deleteCard id: " + id + ", User: " + userLoged.getName());

        try{
            if(userLoged==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            Card card = this.findCardById(id);
            cardRepository.deleteById(card.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.add(Configuration.CORRELATION_ID_HEADER_NAME, correlationIdUtils.generateAndStoreId(null));
            correlationIdUtils.removeCorrelationId();

            log.info("OUT CARD, card deleted id: " + id +", User: " + userLoged.getName());

            return responseBuilder.buildResponse(
                    headers,
                    HttpStatus.OK.value(),
                    String.format("Card with id %s deleted succesfully", id)
            );

        }catch (BaseException e){
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }

    }

    @Override
    public ResponseEntity<?> getCardsByIdDeckToStudy(Long idDeck, Authentication userLoged) throws BaseException{
        log.info("IN CARD getCardsByIdDeckToStudy idDeck: " + idDeck);

        List <Card> cards = new ArrayList<>();
        try {
            Optional<Deck> deck = deckService.findDeck(idDeck);
            if (!deck.isPresent())
                throw new BaseException(String.format("Deck with id %s not found ", idDeck), HttpStatus.NOT_FOUND.toString());

            cards = this.findCardPendingStudy(deck.get());


            HttpHeaders headers = new HttpHeaders();
            headers.add(Configuration.CORRELATION_ID_HEADER_NAME, correlationIdUtils.generateAndStoreId(null));
            correlationIdUtils.removeCorrelationId();

            log.info("OUT CARD getCardsByIdDeckToStudy, Cards response: " + idDeck);

            return responseBuilder.buildResponse(
                    headers,
                    HttpStatus.OK.value(),
                    String.format("Obtained %s cards pending to study from deck %s", cards.size(), idDeck),
                    cards
            );

        }
        catch (BaseException e){
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public List<Card> findCardPendingStudy (Deck deck) throws BaseException {
        List<Card> cardsToStudy = new ArrayList<>();
        try{
            Instant today =  Instant.now();
            Long lastTrySecond;
            List<Card> cards = deck.getCards();
            for (Card card: cards){
                Long timeFromLastStudy = 0L;

                //segundos desde 1970
                Long todaySecond = today.getEpochSecond();

                if(card.getLastTry() == null)
                    lastTrySecond = todaySecond;
                else
                    lastTrySecond = card.getLastTry().getEpochSecond();

                //dias transcurridos desde la ultima vez q se estudio la tarjeta.
                timeFromLastStudy = TimeUnit.SECONDS.toDays(todaySecond-lastTrySecond);
                // TimeUnit.MILLISECONDS.toDays(today.getEpochSecond() - card.getLastTry().getEpochSecond());
                if(timeFromLastStudy >= card.getPeriodDaysReminder() )
                    cardsToStudy.add(card);

            }

        }catch (Exception e){
            throw new BaseException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }

        return cardsToStudy;
    }

    @Override
    public ResponseEntity<?> saveCardResonseYes(Long id) throws BaseException{
        try{
            Optional<Card> card = cardRepository.findById(id);

            if(card.isPresent()){
                //Integer actualPeriodDays = card.get().getPeriodDaysReminder();
                card.get().setLastTry(Instant.now());

                if(card.get().getPeriodDaysReminder()!=6) {
                    if(card.get().getPeriodDaysReminder()==0)
                        card.get().setPeriodDaysReminder(1);
                    else if (card.get().getPeriodDaysReminder()==1)
                        card.get().setPeriodDaysReminder(3);
                    else if (card.get().getPeriodDaysReminder()==3)
                        card.get().setPeriodDaysReminder(7);
                    else if (card.get().getPeriodDaysReminder()==7)
                        card.get().setPeriodDaysReminder(14);
                    else if (card.get().getPeriodDaysReminder()==14)
                        card.get().setPeriodDaysReminder(30);
                    else if (card.get().getPeriodDaysReminder()==30)
                        card.get().setPeriodDaysReminder(90);
                    else if (card.get().getPeriodDaysReminder()==90)
                        card.get().setEnable(false);
                }

                this.cardRepository.save(card.get());
            }else
                throw new BaseException(String.format("Card with id %s not found", id), HttpStatus.NOT_FOUND.toString());

        }
        catch (Exception e){
            throw new BaseException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return ResponseEntity.ok().body(null);
    }

    @Override
    public ResponseEntity<?> saveCardResonseNo(Long id) throws BaseException{
        try{
            Optional<Card> card = cardRepository.findById(id);
            if(card.isPresent()){
                /*
                card.get().setLastTry(Instant.now());

                if(card.get().getPeriodDaysReminder()==90)
                    card.get().setPeriodDaysReminder(30);
                else if (card.get().getPeriodDaysReminder()==30)
                    card.get().setPeriodDaysReminder(14);
                else if (card.get().getPeriodDaysReminder()==14)
                    card.get().setPeriodDaysReminder(7);
                else if (card.get().getPeriodDaysReminder()==7)
                    card.get().setPeriodDaysReminder(3);
                else if (card.get().getPeriodDaysReminder()==3)
                    card.get().setPeriodDaysReminder(1);
                else if (card.get().getPeriodDaysReminder()==1)
                    card.get().setPeriodDaysReminder(0);
                    */
                card.get().setPeriodDaysReminder(0);

                this.cardRepository.save(card.get());
            }else
                throw new BaseException("Cards not found", HttpStatus.NOT_FOUND.toString());

        }catch (Exception e){
            throw new BaseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return ResponseEntity.ok().body(null);
    }

    @Override
    public StandardResponse sendSaveCard (CreateCardRequest cardRequest) throws Exception{
        Message message = null;
        try{
            message = MessageBuilder
                    .withBody(objectMapper.writeValueAsBytes(cardRequest))
                    .build();
            messageSender.sendSaveCardToQueue(message);
        }catch (Exception e){
            return new StandardResponse("ko",e.getMessage(),Instant.now(),message.getMessageProperties().getHeader(Configuration.CORRELATION_ID_HEADER_NAME));
        }
        return new StandardResponse("ok","ok",Instant.now(),message.getMessageProperties().getHeader(Configuration.CORRELATION_ID_HEADER_NAME));

    }

}
