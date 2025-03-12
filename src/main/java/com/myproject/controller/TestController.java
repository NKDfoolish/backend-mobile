package com.myproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Tag(name = "Test Controller", description = "Test Controller")
public class TestController {

    @Operation(summary = "Test API", description = "Test API")
    @GetMapping("/hello")
    public String test(@RequestParam String name) {
        return "Hello " + name;
    }
}
