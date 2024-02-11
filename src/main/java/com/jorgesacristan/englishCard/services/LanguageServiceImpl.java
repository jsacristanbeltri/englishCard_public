package com.jorgesacristan.englishCard.services;

import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.models.Deck;
import com.jorgesacristan.englishCard.models.Language;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.repositories.LanguageRepository;
import com.jorgesacristan.englishCard.request.CreateLanguageRequest;
import com.jorgesacristan.englishCard.request.UpdateLanguageRequest;
import com.jorgesacristan.englishCard.response.ApiResponse;
import com.jorgesacristan.englishCard.response.ResponseBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LanguageServiceImpl implements LanguageService{

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ResponseBuilder responseBuilder;

    @Override
    public List<Language> findAll(){
        return languageRepository.findAll();
    }

    @Override
    public ResponseEntity<ApiResponse> findAllLanguages(){
        log.info("IN getAllLanguages");
        List<Language> languages = languageRepository.findAll();
        log.info("OUT getAllLanguages");
        return responseBuilder.buildResponse(HttpStatus.OK.value(), "All languages got successfully", languages);
    }

    @Override
    @SneakyThrows
    public Optional<Language> findByLanguage(String language){
        return languageRepository.findByLanguage(language);
    }

    @Override
    @SneakyThrows
    public List<String> findByUser(User user){
        //List<Deck> decksUser = deckService.findDecksByUsername(user);
        //List<String> languages = decksUser.stream().map(deck -> deck.getLanguage().getLanguage()).distinct().collect(Collectors.toList());
        List<String> d = new ArrayList<>();
        return d;
    }

    @Override
    public Language save(Language languageRequest) throws Exception{
        Language languageSaved;
        try{
            Language language = modelMapper.map(languageRequest,Language.class);
            languageSaved=languageRepository.save(language);
        }catch (Exception e){
            throw e;
        }
        return languageSaved;
    }

    @Override
    public Optional<Language> findById (Long idRequest) throws BaseException,Exception {
        Optional<Language> response;
        try {
            response = this.languageRepository.findById(idRequest);
        }catch (Exception e){
            throw e;
        }
        return response;
    }

    @Override
    public List<Language> findByUserId (Long idUser) throws Exception {
        return this.languageRepository.findByUserId(idUser);
    }

    @Override
    public Language updateLanguage(Language language, UpdateLanguageRequest updateLanguageRequest) throws Exception {
        Language languageResponse;

        language.setLanguage(updateLanguageRequest.getLanguage());
        language.setImage(updateLanguageRequest.getImage());
        try{
            languageResponse = this.languageRepository.save(language);
        }catch (Exception e){
            throw e;
        }

        return languageResponse;
    }


}
