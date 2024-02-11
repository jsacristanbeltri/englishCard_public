package com.jorgesacristan.englishCard.services;

import com.jorgesacristan.englishCard.dtos.DeckRequestDto;
import com.jorgesacristan.englishCard.request.CreateDeckRequest;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.models.Deck;
import com.jorgesacristan.englishCard.models.Language;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.response.ApiResponse;
import com.jorgesacristan.englishCard.response.StandardResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface DeckService{
    Optional<Deck> findDeck(Long id) throws BaseException;
    public List<Deck> findAllDecks();
    void saveDeck(DeckRequestDto deckRequest) throws BaseException;
    ResponseEntity<ApiResponse> deleteDeck(Long id, Authentication userLoged) throws BaseException;
    ResponseEntity<ApiResponse> updateDeck(Long id, CreateDeckRequest deckRequest, Authentication userLoged) throws BaseException;
    List<Deck> findDecksByUsernameAndLenguage(User user, Language language);
    public List<Deck> findByLanguage (Language lenguage);
    List<Deck> findDecksByUsername(User username);
    //public void decrementTotalCardsOfDeck (Long id) throws BaseException,Exception;
    //public void incrementTotalCardsOfDeck (Long id) throws BaseException,Exception;
    ResponseEntity<?> getDeckById (Long id, Authentication userLoged) throws BaseException;
    ResponseEntity<?> sendSaveDeck (CreateDeckRequest newDeck, Authentication userLoged) throws Exception;
    List<String> findAllLanguage() throws Exception;
    List<String> findLanguagesByUser(User userLoged) throws Exception;

    ResponseEntity<ApiResponse> getAllLanguageByUser(Authentication userLoged) throws BaseException;

    ResponseEntity<ApiResponse> getAllDecksByUsernameLanguage (Authentication user, String languageRequest) throws BaseException;
    ResponseEntity<?> getAllDecksByUser(Authentication userLoged) throws BaseException;
    Deck findById (Long id) throws BaseException;



}
