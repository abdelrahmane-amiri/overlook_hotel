package com.example.Overlook_Hotel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestSql {
    
    @GetMapping("/")
    public String test() {
        return "Connexion  OK !";
    }
}
