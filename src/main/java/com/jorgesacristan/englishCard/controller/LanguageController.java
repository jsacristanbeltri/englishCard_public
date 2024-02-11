package com.jorgesacristan.englishCard.controller;

import com.jorgesacristan.englishCard.configuration.Configuration;
import com.jorgesacristan.englishCard.dtos.DeckOutDto;
import com.jorgesacristan.englishCard.enums.UserRole;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.models.Deck;
import com.jorgesacristan.englishCard.models.Language;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.request.CreateDeckRequest;
import com.jorgesacristan.englishCard.request.CreateLanguageRequest;
import com.jorgesacristan.englishCard.request.UpdateLanguageRequest;
import com.jorgesacristan.englishCard.response.ApiResponse;
import com.jorgesacristan.englishCard.services.DeckService;
import com.jorgesacristan.englishCard.services.LanguageService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for managing operations related to languages.
 */
@RestController
@RequestMapping(Configuration.API_V1_PREFIX + "/languages")
public class LanguageController {
    private static Logger log = LoggerFactory.getLogger(LanguageController.class);

    @Autowired
    private LanguageService languageService;

    @Autowired
    private DeckService deckService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Retrieve all language by user
     * @param userLoged
     * @return
     * @throws BaseException
     * @throws Exception
     */
    @GetMapping()
    public ResponseEntity<ApiResponse> getAllLanguages (Authentication userLoged) {
        return languageService.findAllLanguages();
    }

    /**
     * Add language
     * @param createLanguageRequest
     * @param userLoged
     * @return
     * @throws BaseException
     * @throws Exception
     */
   /* @PostMapping()
    public ResponseEntity<?> saveLanguage ( @RequestBody @Valid CreateLanguageRequest createLanguageRequest,
                                            @AuthenticationPrincipal User userLoged) throws BaseException,Exception{
        log.info("IN Language, saveLanguage, createLanguageRequest: " + createLanguageRequest);
        Language languageResponse;
        Language languageRequest = new Language();
        try {
            Optional<Language> language = languageService.findByLanguage(createLanguageRequest.getLanguage());
            if(language.isPresent())
                throw new BaseException(String.format("The language %s already exist", createLanguageRequest.getLanguage()),HttpStatus.BAD_REQUEST.toString());
            else{
                languageRequest= modelMapper.map(createLanguageRequest,Language.class);
                if (!userLoged.getRoles().contains(UserRole.ADMIN)){
                    languageRequest.setUserid(userLoged.getId());
                }
                languageResponse=languageService.save(languageRequest);
            }
        }catch (BaseException e){
            throw e;
        }catch (Exception e){
            throw e;
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(languageResponse);
    }*/

    /**
     * Update language
     * @param updateLanguageRequest
     * @param userLoged
     * @return
     * @throws BaseException
     * @throws Exception
     */
    /*@PutMapping()
    public ResponseEntity<?> updateLanguage (@RequestBody UpdateLanguageRequest updateLanguageRequest,
                                             @AuthenticationPrincipal User userLoged) throws BaseException,Exception{
        log.info("IN Language, updateLanguage, updateLanguageRequest: " + updateLanguageRequest);
        Language languageResponse;

        try {
            Optional<Language> language = languageService.findById(updateLanguageRequest.getId());
            if(!language.isPresent())
                throw new BaseException(String.format("Language with id %s not found", updateLanguageRequest.getId()), HttpStatus.NOT_FOUND.toString());
            else{
                if (!userLoged.getRoles().contains(UserRole.ADMIN)){
                    if(language.get().getUserid()==null){
                        throw new BaseException("User not authorized", HttpStatus.UNAUTHORIZED.toString());
                    } else if(language.get().getUserid()!=userLoged.getId()){
                         throw new BaseException("User not authorized", HttpStatus.UNAUTHORIZED.toString());
                    }

                }

                languageResponse = languageService.updateLanguage(language.get(),updateLanguageRequest);
            }


        }catch (BaseException e){
            throw e;
        }catch (Exception e){
            throw e;
        }
        return ResponseEntity.status(HttpStatus.OK).body(languageResponse);
    }*/


}
