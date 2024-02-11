package com.jorgesacristan.englishCard.services;

import com.jorgesacristan.englishCard.dtos.SignUpRequest;
import com.jorgesacristan.englishCard.dtos.SigninRequest;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.response.ApiResponse;
import com.jorgesacristan.englishCard.response.JwtAuthenticationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public interface AuthenticationService {
    JwtAuthenticationResponse signup(SignUpRequest request);

    ResponseEntity<ApiResponse> signin(SigninRequest request);
    Map<String, String> signGoogle(OAuth2User request) throws BaseException;

    ResponseEntity<ApiResponse> validateToken (String tokenToValidate, String userEmail);
    String getUsernameFromAuthentication (Authentication authentication) throws BaseException;
}
