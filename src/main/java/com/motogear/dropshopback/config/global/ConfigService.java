package com.motogear.dropshopback.config.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ConfigService {
    @Autowired
    private GlobalConfigRepository configRepository;
    private static final String PROFIT_MARGIN_KEY = "PROFIT_MARGIN_PERCENTAGE";


    public BigDecimal getProfitMargin() {
        return configRepository.findByKey(PROFIT_MARGIN_KEY)
                .map(config -> new BigDecimal(config.getValue()))
                .orElse(BigDecimal.valueOf(20)); // 20% por defecto
    }

    public void updateProfitMargin(BigDecimal percentage) {
        GlobalConfig config = configRepository.findByKey(PROFIT_MARGIN_KEY)
                .orElse(new GlobalConfig());
        config.setKey(PROFIT_MARGIN_KEY);
        config.setValue(percentage.toString());
        config.setDescription("Porcentaje de margen de beneficio sobre el precio base");
        configRepository.save(config);
    }

    public BigDecimal calculateSellPrice(BigDecimal basePrice) {
        BigDecimal margin = getProfitMargin();
        BigDecimal increase = basePrice.multiply(margin).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return basePrice.add(increase);
    }
}
