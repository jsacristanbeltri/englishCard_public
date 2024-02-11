package com.jorgesacristan.englishCard.controller;

import com.jorgesacristan.englishCard.configuration.Configuration;
import com.jorgesacristan.englishCard.dtos.CreateCardDto;
import com.jorgesacristan.englishCard.exceptions.*;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.request.CreateCardRequest;
import com.jorgesacristan.englishCard.response.ApiResponse;
import com.jorgesacristan.englishCard.response.ResponseBuilder;
import com.jorgesacristan.englishCard.response.StandardResponse;
import com.jorgesacristan.englishCard.services.CardService;
import com.jorgesacristan.englishCard.services.DeckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller for managing operations related to cards.
 */

@RestController
@RequestMapping(Configuration.API_V1_PREFIX + "/cards")
public class CardController{

    private static Logger log = LoggerFactory.getLogger(CardController.class);

    @Autowired
    CardService cardService;

    @Autowired
    DeckService deckService;

    @Autowired
    private ResponseBuilder responseBuilder;

    /**
     * Retrieves a card by its ID.
     * @param id
     * @param user
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCardById (@PathVariable Long id, Authentication user) throws Exception{
        return cardService.getCardById(id, user);
    }

    /**
     * Retrieves a card by its ID DECK.
     * @param idDeck
     * @param user
     * @return
     * @throws Exception
     */
    @GetMapping("/deck/{idDeck}")
    public ResponseEntity<?> getCardsByIdDeck(@PathVariable Long idDeck, Authentication user) throws Exception{
        return cardService.getCardsByIdDeck ( idDeck, user);

    }

    /**
     * Retrieves the cards pending to study.
     * @param idDeck
     * @param user
     * @return
     * @throws Exception
     */
    @GetMapping("/pending/deck/{idDeck}")
    public ResponseEntity<?> getCardsByIdDeckToStudy(
            @PathVariable Long idDeck,
            Authentication user) throws Exception {
        return cardService.getCardsByIdDeckToStudy(idDeck, user);


    }

    /**
     * Add card to deck.
     * @param cardRequest
     * @param user
     * @return
     * @throws Exception
     */
    @PostMapping("/deck")
    public ResponseEntity<?> addCard (
            @RequestBody @Valid CreateCardRequest cardRequest,
            Authentication user) throws Exception {
        return cardService.addCard(cardRequest,user);
    }

    /**
     * Update card
     * @param userLoged
     * @param id
     * @param cardRequest
     * @return
     * @throws Exception
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCard(Authentication userLoged,
                                       final @PathVariable Long id,
                                       final @RequestBody @Valid CreateCardDto cardRequest) throws Exception{
        return cardService.updateCard(id, cardRequest, userLoged);
    }

    /**
     * Delete card
     * @param id
     * @param userLoged
     * @return
     * @throws Exception
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard (@PathVariable Long id, Authentication userLoged) throws Exception{
        return this.cardService.deleteCardById(id, userLoged);
    }

    /**
     * Update reminder day of card by id
     * @param id
     * @return
     * @throws BaseException
     */
    @GetMapping("/responseCardYes/{id}")
    public ResponseEntity saveCardResponseYes (@PathVariable Long id) throws BaseException{
        log.info("IN CARD saveCardResponseYes id: " + id);
        ResponseEntity responseEntity = this.cardService.saveCardResonseYes(id);
        log.info("OUT CARD saveCardResponseYes id: " + id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Update reminder day of card by id
     * @param id
     * @return
     * @throws BaseException
     */
    @GetMapping("/responseCardNo/{id}")
    public ResponseEntity saveCardResponseNo (@PathVariable Long id) throws BaseException{
        log.info("IN CARD saveCardResponseNo id: " +  id);
        ResponseEntity responseEntity = this.cardService.saveCardResonseNo(id);
        log.info("OUT CARD saveCardResponseNo id: ", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
