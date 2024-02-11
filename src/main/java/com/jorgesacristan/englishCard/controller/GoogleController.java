package com.jorgesacristan.englishCard.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/")
public class GoogleController {
    @GetMapping("loginGoogle")
    public String home() {
        return "http://localhost:8080/oauth2/authorization/google";
    }




}
