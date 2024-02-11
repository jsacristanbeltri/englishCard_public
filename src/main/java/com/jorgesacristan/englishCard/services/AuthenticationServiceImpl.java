package com.jorgesacristan.englishCard.services;

import com.jorgesacristan.englishCard.configuration.Configuration;
import com.jorgesacristan.englishCard.dtos.SignUpRequest;
import com.jorgesacristan.englishCard.dtos.SigninRequest;
import com.jorgesacristan.englishCard.dtos.UserDto;
import com.jorgesacristan.englishCard.enums.UserRole;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.maps.UserMapper;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.repositories.UserRepository;
import com.jorgesacristan.englishCard.response.ApiResponse;
import com.jorgesacristan.englishCard.response.JwtAuthenticationResponse;
import com.jorgesacristan.englishCard.response.ResponseBuilder;
import com.jorgesacristan.englishCard.utils.CorrelationIdUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final CorrelationIdUtils correlationIdUtils;
    private final ResponseBuilder responseBuilder;
    private final UserDetailService userDetailService;
    private final UserMapper userMapper;


    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(UserRole.ROLE_USER)).build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    @Override
    public ResponseEntity<ApiResponse> signin(SigninRequest request) {
        log.info(String.format("IN signin username: %s", request.getEmail()));
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByUsername(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        var jwt = jwtService.generateToken(user);

        Map<String,Object> signInDataResponse = new HashMap<>();
        signInDataResponse.put("token",jwt);

        HttpHeaders headers = new HttpHeaders();
        headers.add(Configuration.CORRELATION_ID_HEADER_NAME, correlationIdUtils.generateAndStoreId(null));
        correlationIdUtils.removeCorrelationId();
        UserDto userDto = userMapper.userToUserDto(user);
        log.info(String.format("OUT signin username: %s, result ok", request.getEmail()));
        return responseBuilder.buildResponse(
                headers,
                HttpStatus.OK.value(),
                String.format("User %s loggin successfully", request.getEmail()),
                userMapper.userToUserDto(user),
                signInDataResponse
        );


    }

    @Override
    public Map<String, String> signGoogle(OAuth2User request) throws BaseException {
        try{
            log.info("IN signGoogle, request: " + request);

            Map<String,String> userData = new HashMap<>();
            User user = userService.getAndSaveUserFromOauth2(request);
            var jwt = jwtService.generateToken(user);
            //return JwtAuthenticationResponse.builder().token(jwt).build();
            userData.put("token", jwt);
            userData.put("email", user.getEmail());
            userData.put("username",  user.getUsername());
            log.info(String.format("OUT signGoogle, user: %s", user));

//            HttpHeaders headers = new HttpHeaders();
//            headers.add(Configuration.CORRELATION_ID_HEADER_NAME, correlationIdUtils.generateAndStoreId(null));
//            correlationIdUtils.removeCorrelationId();

//            Map<String, Object> otherParams = new HashMap<>();
//            otherParams.put("token", jwt);
            return userData;
//            return responseBuilder.buildResponse(
//                    headers,
//                    HttpStatus.OK.value(),
//                    String.format("User %s logged succesfully", user.getUsername()),
//                    UserMapper.MAPPER.userToUserDto(user),
//                    otherParams
//            );

        }catch (Exception e){
            throw new BaseException( HttpStatus.INTERNAL_SERVER_ERROR.toString(), String.format("Error google login detected, error: %s", e.getMessage()));
        }


    }

    @Override
    public ResponseEntity<ApiResponse> validateToken (String tokenToValidate, String userName) {
        log.info(String.format("IN validateToken %s , user email: %s", tokenToValidate,userName));
        UserDetails userDetails = userDetailService.userDetailsService().loadUserByUsername(userName);
        Boolean isValidate = jwtService.isTokenValid(tokenToValidate,userDetails);

        log.info(String.format("OUT validateToken %s , user email: %s, result: ", tokenToValidate,userName, isValidate));

        return responseBuilder.buildResponse(
                Boolean.TRUE.equals(isValidate)?HttpStatus.OK.value():HttpStatus.UNAUTHORIZED.value(),
                Boolean.TRUE.equals(isValidate)?"Token is validated":"token is invalidated",
                Boolean.TRUE.equals(isValidate)?true:false
        );
    }

    @Override
    public String getUsernameFromAuthentication (Authentication authentication) throws BaseException{
        UsernamePasswordAuthenticationToken auth;
        OAuth2AuthenticationToken oauth2;
        String username ="";
        DefaultOidcUser defaultOidcUser;
        try{
            if(authentication instanceof UsernamePasswordAuthenticationToken) {
                auth = (UsernamePasswordAuthenticationToken) authentication;
                username = auth.getName();
            }
            else {
                oauth2 = (OAuth2AuthenticationToken) authentication;
                Object principal = authentication.getPrincipal();
                if (principal instanceof DefaultOidcUser){
                    defaultOidcUser = (DefaultOidcUser) principal;
                    username = defaultOidcUser.getAttribute("given_name");
                }

                //username = auth2.getAttributes().get("given_name")
            }

            return username;
        }catch (Exception e){
            throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), String.format("Error on login detected: ", e.getMessage()));
        }

    }
}
