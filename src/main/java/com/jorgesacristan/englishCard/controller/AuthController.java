package com.jorgesacristan.englishCard.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jorgesacristan.englishCard.configuration.Configuration;
import com.jorgesacristan.englishCard.dtos.LoginRequestDto;
import com.jorgesacristan.englishCard.dtos.SignUpRequest;
import com.jorgesacristan.englishCard.dtos.SigninRequest;
import com.jorgesacristan.englishCard.dtos.UserDtoConverter;
import com.jorgesacristan.englishCard.enums.UserRole;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.response.ApiResponse;
import com.jorgesacristan.englishCard.response.JwtAuthenticationResponse;
import com.jorgesacristan.englishCard.services.AuthenticationService;
import com.jorgesacristan.englishCard.services.JwtService;
import com.jorgesacristan.englishCard.services.UserService;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for managing operations related to authetication user.
 */
@RestController
@RequestMapping(Configuration.API_V1_PREFIX + "/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;
    private final UserDtoConverter userDtoConverter;
    private JwtService jwtService;

    @Autowired
    UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    /*@CrossOrigin(origins = "*")
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> login (@RequestBody LoginRequestDto loginRequest) throws JsonProcessingException {
        log.info("IN AUTH login username: " + loginRequest.getUsername());
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getUsername(),
                                loginRequest.getPassword()
                        ));

        //Añadimos el usuario logeado en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertUserEntityAndTokenToJwtUserResponse(user,jwtToken));

    }*/

    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.signup(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse> signin(@RequestBody SigninRequest request) {
        return authenticationService.signin(request);
    }

//    @PostMapping("/signingoogle")
//    public ResponseEntity<JwtAuthenticationResponse> signinGoogle(@RequestBody SigninRequest request) {
//        return ResponseEntity.ok(authenticationService.signin(request));
//    }

   /* @GetMapping("/googleresponse")
    public ResponseEntity<ApiResponse> googleResponse(@AuthenticationPrincipal OAuth2User oAuth2User) throws Exception{
        return authenticationService.signGoogle(oAuth2User);
    }*/

    @GetMapping("googleresponse")
    public void login(@AuthenticationPrincipal OAuth2User oAuth2User, HttpServletResponse response) throws IOException, BaseException {
        Map<String, String> userData = authenticationService.signGoogle(oAuth2User);
        response.sendRedirect("http://localhost:8100/login-screen?token=" + userData.get("token") + "&email=" + userData.get("email") + "&username=" + userData.get("username"));
    }

    @GetMapping("validateToken/{tokenToValidate}")
    public ResponseEntity<ApiResponse> validateToken (@PathVariable String tokenToValidate,@RequestParam("username") String email) {
        return authenticationService.validateToken(tokenToValidate,email);
    }

 /*   *//**
     * SB 2. get user permisions
     * @param user
     * @return
     *//*
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/me")
    public GetUserDto me(@AuthenticationPrincipal User user){
        return userDtoConverter.convertUserEntityToGetUserDto(user);
    }*/

  /*  private JwtUserResponse convertUserEntityAndTokenToJwtUserResponse(User user, String jwtToken) {
        return JwtUserResponse
                .jwtUserResponseBuilder()
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .roles(user.getRoles().stream().map(UserRole::name).collect(Collectors.toSet()))
                .token(jwtToken)
                .build();
    }*/


}


