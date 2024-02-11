package com.jorgesacristan.englishCard.services;

import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.models.Language;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.request.CreateLanguageRequest;
import com.jorgesacristan.englishCard.request.UpdateLanguageRequest;
import com.jorgesacristan.englishCard.response.ApiResponse;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface LanguageService {
    List<Language> findAll();
    ResponseEntity<ApiResponse> findAllLanguages();

    @SneakyThrows
    Optional<Language> findByLanguage(String language);
    List<String> findByUser(User user);
    Language save(Language languageRequest) throws Exception;
    Optional<Language> findById (Long idRequest) throws BaseException,Exception;
    List<Language> findByUserId (Long idUser) throws Exception;

    Language updateLanguage(Language language, UpdateLanguageRequest updateLanguageRequest) throws Exception;
}
