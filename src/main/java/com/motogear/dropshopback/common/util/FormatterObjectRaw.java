package com.motogear.dropshopback.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FormatterObjectRaw {
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonNode parsePricesResponse(String rawJson) {
        try {
            return mapper.readTree(rawJson);
        } catch (Exception e) {
            throw new RuntimeException("Error parseando respuesta precios", e);
        }
    }

}
