package com.jorgesacristan.englishCard.controller;

import com.jorgesacristan.englishCard.request.CreateDeckRequest;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.response.ApiResponse;
import com.jorgesacristan.englishCard.services.DeckService;
import com.jorgesacristan.englishCard.services.LanguageService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.jorgesacristan.englishCard.configuration.*;

import javax.validation.Valid;

/**
 * Controller for managing operations related to decks.
 */
@RestController
@RequestMapping(Configuration.API_V1_PREFIX + "/decks")
public class DeckController{

    private static Logger log = LoggerFactory.getLogger(DeckController.class);

    @Autowired
    private DeckService deckService;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Retrieve all decks by user
     * @param userLoged
     * @return
     * @throws Exception
     */
    @GetMapping()
    public ResponseEntity<?> getAllDecksByUser (Authentication userLoged) throws Exception{
        log.info("user loged: " + userLoged);
        return deckService.getAllDecksByUser(userLoged);
    }

    /**
     * Retrieve all language of the decks of a specific user.
     * @param userLoged
     * @param languageRequest
     * @return
     * @throws Exception
     */
    @GetMapping("/language/{languageRequest}")
    public ResponseEntity<ApiResponse> getAllDecksByUsernameLenguage(Authentication userLoged ,
            @PathVariable(value="languageRequest") String languageRequest)throws Exception {
        return deckService.getAllDecksByUsernameLanguage(userLoged,languageRequest);
    }

    /**
     * Retrieve deck by id
     * @param userLoged
     * @param id
     * @return
     * @throws BaseException
     * @throws Exception
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDeckById (Authentication userLoged, final @PathVariable Long id) throws BaseException,Exception{
        log.info("IN DECKS findById id: " +  id);
        ResponseEntity<?> responseEntity = deckService.getDeckById(id, userLoged);
        log.info("OUT DECKS findById id: " +  id);
        return responseEntity;

    }

    /**
     * Retrieve the languages by user
     * @param userLoged
     * @return
     * @throws Exception
     */
    @GetMapping("/languages")
    public ResponseEntity<ApiResponse> getAllLanguageByUser (Authentication userLoged) throws Exception{
        return deckService.getAllLanguageByUser (userLoged);
    }

    /**
     * Add deck
     * @param deckRequest
     * @param userLoged
     * @return
     * @throws Exception
     */
    @PostMapping()
    public ResponseEntity<?> addDeck (
            @RequestBody @Valid CreateDeckRequest deckRequest,
            Authentication userLoged) throws Exception{
        return deckService.sendSaveDeck(deckRequest, userLoged);
    }


    /**
     * Update deck
     * @param userLoged
     * @param id
     * @param deckRequest
     * @return
     * @throws Exception
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(Authentication userLoged,
                       final @PathVariable Long id,
                       final @RequestBody @Valid CreateDeckRequest deckRequest) throws Exception{
        return deckService.updateDeck(id,deckRequest,userLoged);
    }

    /**
     * Delete deck
     * @param userLoged
     * @param id
     * @return
     * @throws Exception
     */
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> deleteDeck (Authentication userLoged,
                                         final @PathVariable Long id) throws Exception {
        return deckService.deleteDeck(id, userLoged);


    }

}
