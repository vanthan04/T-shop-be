package com.productservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/api/v1/products/demo")
    public String sayHello(){
        return "Route product say hello";
    }
}
