package com.motogear.dropshopback.config.global;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirectRootToSwagger() {
        return "redirect:/swagger-ui.html";
    }

    @GetMapping("/docs")
    public String redirectDocsToSwagger() {
        return "redirect:/swagger-ui.html";
    }
}

