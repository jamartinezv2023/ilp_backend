package com.inclusive.learning.commons.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonsServiceController {

    @GetMapping("/commons/status")
    public String getStatus() {
        return "âœ… Commons Service activo y funcionando correctamente";
    }
}




