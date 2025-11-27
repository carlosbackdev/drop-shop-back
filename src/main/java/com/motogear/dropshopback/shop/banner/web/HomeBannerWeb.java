package com.motogear.dropshopback.shop.banner.web;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home-banners")
@SecurityRequirement(name = "bearerAuth")
public class HomeBannerWeb {
}
