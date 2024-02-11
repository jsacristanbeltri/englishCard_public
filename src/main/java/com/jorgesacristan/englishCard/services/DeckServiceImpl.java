package com.jorgesacristan.englishCard.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jorgesacristan.englishCard.configuration.Configuration;
import com.jorgesacristan.englishCard.dtos.DeckOutDto;
import com.jorgesacristan.englishCard.dtos.DeckRequestDto;
import com.jorgesacristan.englishCard.enums.UserRole;
import com.jorgesacristan.englishCard.maps.DeckMapper;
import com.jorgesacristan.englishCard.models.Card;
import com.jorgesacristan.englishCard.repositories.UserRepository;
import com.jorgesacristan.englishCard.request.CreateDeckRequest;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.models.Deck;
import com.jorgesacristan.englishCard.models.Language;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.rabbit.MessageSender;
import com.jorgesacristan.englishCard.repositories.DeckRepository;
import com.jorgesacristan.englishCard.response.ApiResponse;
import com.jorgesacristan.englishCard.response.ResponseBuilder;
import com.jorgesacristan.englishCard.response.StandardResponse;
import com.jorgesacristan.englishCard.utils.CorrelationIdUtils;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeckServiceImpl implements DeckService{

    private static Logger log = LoggerFactory.getLogger(DeckServiceImpl.class);

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CorrelationIdUtils correlationIdUtils;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResponseBuilder responseBuilder;

    @Autowired
    private DeckMapper deckMapper;

    @Autowired
    private AuthenticationService authenticationService;


    @Override
    public Optional<Deck> findDeck(Long id) throws BaseException{
        Optional<Deck> deck = null;
        try{
            deck =  deckRepository.findById(id);
        }catch (Exception e){
            throw new BaseException(e.getMessage(), HttpStatus.NOT_FOUND.toString());
        }
        return deck;
    }

    @Override
    public List<Deck> findAllDecks() {
        return deckRepository.findAll();
    }


    @Override
    public Deck findById (Long id) throws BaseException {
        return deckRepository.findById(id).orElseThrow(()->new BaseException(String.format("Deck with id %s not found", id), HttpStatus.NOT_FOUND.toString()));
    }

    @Override
    public ResponseEntity<ApiResponse> updateDeck(Long id, CreateDeckRequest deckRequest, Authentication userLoged) throws BaseException {

        log.info(String.format("IN DECK update id: %s, deckRequest: %s", String.valueOf(id), deckRequest));
        Deck deckResponse =  deckRepository.save(this.buildDeck(findById(id),deckRequest));
        log.info("OUT DECK update id: "+ id);
        return responseBuilder.buildResponse(
                HttpStatus.OK.value(),
                String.format("Deck %s updated successfully", deckResponse),
                deckResponse
               );
    }

    private DeckOutDto converToDeckOutDto (Deck deck) {
        DeckOutDto deckOutDto = modelMapper.map(deck,DeckOutDto.class);
        deckOutDto.setUsername(deck.getUser().getUsername());
        return deckOutDto;
    }

    private Deck buildDeck(Deck deck, CreateDeckRequest deckRequest) throws BaseException{

        Optional<Language> newLanguage = languageService.findByLanguage(deckRequest.getLanguage());

        if(!newLanguage.isPresent())
            throw new BaseException(String.format("Language %s not found", deckRequest.getLanguage()), HttpStatus.NOT_FOUND.toString());

        deck.setName(deckRequest.getName());
        deck.setDescription(deckRequest.getDescription());
        deck.setLanguage(newLanguage.get());

        return deck;
    }


    /*public Deck findByDecknameUsername(String deckname, String username, String lenguage){
        List<Deck> decks = new ArrayList<>();
        try{
            decks = this.findAllDecks();
            for(Deck deck : decks){
                if(deck.getName().equals(deckname) &&
                        deck.getUser().getUsername().equals(username) &&
                        deck.getLanguage().equals(lenguage))
                    return deck;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }*/

    @Override
    public List<Deck> findDecksByUsernameAndLenguage(User user, Language language){
        Objects.requireNonNull(user,"User can't be null");
        Objects.requireNonNull(language, "Language can't be null");
        return deckRepository.findDeckByLanguageAndUser(language,user);
    }

    @Override
    public ResponseEntity<ApiResponse> getAllDecksByUsernameLanguage (Authentication user, String languageRequest) throws BaseException{
        List<Deck> result = new ArrayList<>();
        List<Language> languages = new ArrayList<>();
        List<DeckOutDto> decksResponse = new ArrayList<>();

        log.info("IN getAllDecksByLenguage, language: "+ languageRequest);

        try{
            if(user==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            languages = languageService.findAll();
            Optional<Language> language = languages.stream().filter(l -> l.getLanguage().equals(languageRequest)).findFirst();

            if(!language.isPresent())
                throw new BaseException("Language not supported", HttpStatus.BAD_REQUEST.toString());

            User userDb = userService.getAndSaveUser(authenticationService.getUsernameFromAuthentication(user));
            result = this.findDecksByUsernameAndLenguage(userDb, language.get());
            //result.stream().forEach(deck -> decksResponse.add(deckMapper.deckToDeckOutDto(deck)));

        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }catch (Exception e){
            throw new BaseException(e.getMessage());
        }
        log.info("OUT getAllDecksByLenguage, language: "+ languageRequest + ", result: " + decksResponse);
        return responseBuilder.buildResponse(
                HttpStatus.OK.value(),
                String.format("Decks by language %s got successfully", languageRequest),
                result);
    }

    @Override
    public ResponseEntity<ApiResponse> getAllLanguageByUser(Authentication userLoged) throws BaseException {
        log.info("IN DECKS getAllLanguageByUser language of user: " + userLoged.getName());
        List<String> languages = new ArrayList<>();
        try {
            if (userLoged == null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());
            User user = userService.getAndSaveUser(authenticationService.getUsernameFromAuthentication(userLoged));
            languages = this.findLanguagesByUser(user);

        }catch (BaseException e){
            throw new BaseException(e.getMessage(),e.getCode());
        }catch (Exception e){
            throw new BaseException(e.getMessage());
        }
        log.info(String.format("OUT DECKS getAllLanguageByUser  language of user: %s , result: %s",userLoged.getName(), languages));
        return responseBuilder.buildResponse(HttpStatus.OK.value(), "Languages got successfully", languages);
    }


    @Override
    public List<Deck> findByLanguage(Language language) {
        return deckRepository.findDeckByLanguage(language);
    }

    @SneakyThrows
    @Override
    public List<Deck> findDecksByUsername(User user) {
        return deckRepository.findByUser(user);
    }


    @Override
    public ResponseEntity<?> getAllDecksByUser(Authentication userLoged) throws BaseException{
        log.info("IN getAllDecksByUser,  decks of user: " + userLoged.getName());

        List<Deck> decksUser = new ArrayList<>();
        List<DeckOutDto> decksResponse = new ArrayList<>();
        String username = "";
        UsernamePasswordAuthenticationToken auth;
        OAuth2AuthenticationToken auth2;
        DefaultOidcUser userDetailService;

        try{
            if(userLoged==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            User user = userService.getAndSaveUser(authenticationService.getUsernameFromAuthentication(userLoged));
            decksUser = this.findDecksByUsername(user);

        }catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        decksUser.stream().forEach(deck -> decksResponse.add(deckMapper.deckToDeckOutDto(deck)));

        HttpHeaders headers = new HttpHeaders();
        headers.add(Configuration.CORRELATION_ID_HEADER_NAME, correlationIdUtils.generateAndStoreId(null));
        correlationIdUtils.removeCorrelationId();

        log.info("OUT getAllDecksByUser, decks of user: " + userLoged.getName() + "decks: " + decksResponse);
        return responseBuilder.buildResponse(
                headers,
                HttpStatus.OK.value(),
                String.format("Decks of user %s got correctly", userLoged.getName()),
                decksResponse
        );
    }

    @Override
    public void saveDeck(DeckRequestDto deckRequest) throws BaseException {
        try{
            Optional<User> userDb = userRepository.findByUsername(deckRequest.getUsername());
            if(!userDb.isPresent())
                throw new BaseException(HttpStatus.NOT_FOUND.toString(), String.format("User %s not found", deckRequest.getUsername()));

            Optional<Language> language = languageService.findByLanguage(deckRequest.getLanguage());
            if(!language.isPresent())
                throw new BaseException(HttpStatus.NOT_FOUND.toString(), String.format("Language %s not found", deckRequest.getLanguage()));

            Deck newDeck = new Deck();
            newDeck.setUser(userDb.get());
            newDeck.setName(deckRequest.getName());
            newDeck.setCards(new ArrayList<>());
            newDeck.setDescription(deckRequest.getDescription()==null?"":deckRequest.getDescription());
            newDeck.setLanguage(language.get());
            deckRepository.save(newDeck);
            log.info(String.format("Deck %s saved successfully", newDeck));
        }catch (Exception e){
            throw new BaseException("Error detected saving deck , error: " + e.getMessage() + ", uuid: " + correlationIdUtils.getActualCorrelationId(), HttpStatus.SERVICE_UNAVAILABLE.toString());
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getDeckById (Long id, Authentication userLoged) throws BaseException{
        Deck deckResponse = this.findById(id);
        return responseBuilder.buildResponse(HttpStatus.OK.value(), String.format("Deck with id %s got successfully", deckResponse.getId()), deckResponse );
    }

    public ResponseEntity<ApiResponse> deleteDeck(Long id, Authentication userLoged) throws BaseException{
        log.info("IN DECK delete id: " + id);
        Deck deckToUpdate = this.findById(id);
        deckRepository.deleteById(id);
        log.info("OUT DECK delete id: " + id);
        return responseBuilder.buildResponse(
                HttpStatus.OK.value(),
                String.format("Deck with id %s deleted successfully", id));
    }

    @Override
    public List<String> findAllLanguage() throws Exception {
        List<String> languages = new ArrayList<>();
        try{
            List<Deck> decks = this.findAllDecks();
            if(!CollectionUtils.isEmpty(decks)){
                decks.stream().distinct().forEach(deck -> languages.add(deck.getLanguage().getLanguage()));
            }else
                throw new BaseException("Decks not found",HttpStatus.NOT_FOUND.toString());
            return languages;
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public List<String> findLanguagesByUser(User userLoged) throws Exception {
        List<String> languages = new ArrayList<>();
        try{
            List<Deck> decks = this.findAllDecks();
            if(!CollectionUtils.isEmpty(decks)){
                for(Deck deck: decks) {
                    if(deck.getUser().getUsername().equals(userLoged.getUsername()))
                        languages.add(deck.getLanguage().getLanguage());
                }
            }else{
                log.info("No decks found so the languages available are 0");
                return languages;
            }

            return languages.stream().distinct().collect(Collectors.toList());
        }catch (Exception e){
            throw e;
        }
    }

    /*@Override
    public void incrementTotalCardsOfDeck (Long id) throws BaseException,Exception{
        int totalCards=0;
        try{
            Optional<Deck> deck  = this.findDeck(id);
            if(deck.isPresent()){
                totalCards = deck.get().getNumberTotalOfCards();
                totalCards++;
                deck.get().setNumberTotalOfCards(totalCards);
                this.saveDeck(deck.get());
            }else
                throw new BaseException(String.format("Deck with id %s not found", id), HttpStatus.NOT_FOUND.toString());

        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public void decrementTotalCardsOfDeck (Long id) throws BaseException,Exception{
        int totalCards=0;
        try{
            Optional<Deck> deck  = this.findDeck(id);
            if(deck.isPresent()){
                totalCards = deck.get().getNumberTotalOfCards();
                if(totalCards>0)
                    totalCards--;
                this.saveDeck(deck.get());
            }else
                throw new BaseException(String.format("Deck with id %s not found", id), HttpStatus.NOT_FOUND.toString());

        }catch (Exception e){
            throw e;
        }
    }*/

    @Override
    public ResponseEntity<?> sendSaveDeck (CreateDeckRequest deckCreationRequest, Authentication userLoged) throws Exception{
        log.info("IN DECKS, addDeck, deckRequest: " + deckCreationRequest + ", user: " + userLoged);

        try {
            if (userLoged == null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            DeckRequestDto deckRequestDto = deckMapper.deckToDeckRequestDto(deckCreationRequest);
            deckRequestDto.setUsername(authenticationService.getUsernameFromAuthentication(userLoged));


            Message message = MessageBuilder
                    .withBody(objectMapper.writeValueAsBytes(deckRequestDto))
                    .build();
            String uuid = messageSender.sendSaveDeckToQueue(message);
            log.info(String.format("deck %s sended to queue save deck", deckRequestDto));

            HttpHeaders headers = new HttpHeaders();
            headers.add(Configuration.CORRELATION_ID_HEADER_NAME, uuid);

            return responseBuilder.buildResponse(
                    HttpStatus.OK.value(),
                    String.format("Deck %s saved correctly", deckCreationRequest)
            );
        }
        catch (Exception e){
            throw new BaseException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
    }

    private DeckOutDto converDeckToDeckOutDto (Deck deck) {
        DeckOutDto deckOutDto = modelMapper.map(deck,DeckOutDto.class);
        deckOutDto.setUsername(deck.getUser().getUsername().toString());
        deckOutDto.setNumberOfCards(deck.getCards().size());
        return deckOutDto;
    }
}
