package com.golmok.golmok_daejang.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/hello")
    public String hello(){
        return "Hello It's golmok_daejang Server ^____^";
    }
}
