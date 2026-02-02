package com.example.E.commerce.E_commerce.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class HomePageController
{
    @GetMapping("/about")
    public String aboutPage()
    {
        return "Welcome to the E-commerce Website!!!";
    }
}
