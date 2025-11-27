package com.motogear.dropshopback.config.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;


    @GetMapping("/profit-margin")
    public ResponseEntity<BigDecimal> getProfitMargin() {
        return ResponseEntity.ok(configService.getProfitMargin());
    }

    @PutMapping("/profit-margin")
    public ResponseEntity<String> updateProfitMargin(@RequestParam BigDecimal percentage) {
        if (percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            return ResponseEntity.badRequest().body("El porcentaje debe estar entre 0 y 100");
        }
        configService.updateProfitMargin(percentage);
        return ResponseEntity.ok("Margen actualizado correctamente");
    }

}
