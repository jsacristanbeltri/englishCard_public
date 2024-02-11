package com.jorgesacristan.englishCard.services;

import com.jorgesacristan.englishCard.dtos.CreateCardDto;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.models.Card;
import com.jorgesacristan.englishCard.models.Deck;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.request.CreateCardRequest;
import com.jorgesacristan.englishCard.response.ApiResponse;
import com.jorgesacristan.englishCard.response.StandardResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface CardService{
    void save(CreateCardRequest cardRequest) throws BaseException;
    ResponseEntity<?> updateCard(Long id, CreateCardDto createCardDto, Authentication userLoged) throws BaseException;
    public List<Card> findAll();
    ResponseEntity<?> getCardsByIdDeck (Long idDeck, Authentication userLoged) throws BaseException;
    ResponseEntity<ApiResponse> getCardById(Long id, Authentication userLoged) throws BaseException;
    ResponseEntity<?> deleteCardById (Long id, Authentication userLoged) throws BaseException;
    ResponseEntity<?> getCardsByIdDeckToStudy(Long idDeck, Authentication userLoged) throws BaseException;
    ResponseEntity<?> saveCardResonseYes(Long id) throws BaseException;
    ResponseEntity<?> saveCardResonseNo(Long id) throws BaseException;
    StandardResponse sendSaveCard (CreateCardRequest newCard) throws Exception;

    Card findCardById (Long id) throws BaseException;

    ResponseEntity<?> addCard(CreateCardRequest cardRequest, Authentication user) throws BaseException;

    List<Card> findCardPendingStudy (Deck deck) throws BaseException;
}
