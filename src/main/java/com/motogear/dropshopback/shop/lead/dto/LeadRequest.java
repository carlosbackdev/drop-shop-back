package com.motogear.dropshopback.shop.lead.dto;

import com.motogear.dropshopback.shop.lead.domain.LeadSource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Solicitud para registrar el interés de un visitante en un producto todavía no disponible")
public class LeadRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre es demasiado largo")
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    @Size(max = 200, message = "El email es demasiado largo")
    private String email;

    @NotBlank(message = "El modelo de la moto es obligatorio")
    @Size(max = 150, message = "El modelo es demasiado largo")
    @Schema(example = "Kawasaki ER-6n")
    private String motorcycleModel;

    @Size(max = 10, message = "El año no es válido")
    @Schema(example = "2009")
    private String motorcycleYear;

    @Size(max = 2000, message = "El mensaje es demasiado largo")
    private String message;

    @NotNull(message = "El origen del interés es obligatorio")
    private LeadSource source;

    @Size(max = 200)
    @Schema(example = "ordenador-bordo-kawasaki")
    private String productSlug;
}
