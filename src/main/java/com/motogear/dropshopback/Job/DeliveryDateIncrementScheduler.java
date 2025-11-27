// java
package com.motogear.dropshopback.Job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DeliveryDateIncrementScheduler {

    private final JdbcTemplate jdbcTemplate;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public DeliveryDateIncrementScheduler(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void addOneDayToDeliveryDates() {

        // UPDATE: suma 1 día sólo cuando la columna no es NULL
        String updateSql = ""
                + "UPDATE shop.product SET "
                + "  delivery_min_date = CASE WHEN delivery_min_date IS NOT NULL THEN DATE_ADD(delivery_min_date, INTERVAL 1 DAY) ELSE NULL END, "
                + "  delivery_max_date = CASE WHEN delivery_max_date IS NOT NULL THEN DATE_ADD(delivery_max_date, INTERVAL 1 DAY) ELSE NULL END "
                + "WHERE delivery_min_date IS NOT NULL OR delivery_max_date IS NOT NULL";

        try {
            int rows = jdbcTemplate.update(updateSql);
            log.info("addOneDayToDeliveryDates: filas afectadas = {}", rows);
        } catch (DataAccessException ex) {
            log.error("Error ejecutando update de fechas", ex);
        }
    }
}
