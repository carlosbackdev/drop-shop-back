package com.motogear.dropshopback.shop.catalog.web;

import com.motogear.dropshopback.shop.catalog.service.ProductService;
import com.motogear.dropshopback.shop.catalog.dto.ProductClientResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Endpoints públicos para consulta de productos")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Obtener productos paginados",
               description = "Retorna una página de productos para clientes con información pública")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Página de productos obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/page")
    public ResponseEntity<Page<ProductClientResponse>> getAllClientProducts(
            @Parameter(description = "Parámetros de paginación (page, size, sort)")
            Pageable pageable) {
        return ResponseEntity.ok(productService.findAllClient(pageable));
    }

    @Operation(summary = "Obtener producto por ID",
               description = "Retorna un producto específico con información pública para clientes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductClientResponse> getProductClientById(
            @Parameter(description = "ID del producto a buscar", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(productService.findProductClientById(id));
    }

    @Operation(summary = "Obtener productos por categoría",
               description = "Retorna una lista de productos filtrados por categoría")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay productos para esta categoría"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductClientResponse>> getProductsByCategory(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable Integer categoryId,
            @Parameter(description = "Parámetros de paginación (page, size, sort)")
            Pageable pageable) {
        List<ProductClientResponse> products = productService.findProductByCategoryId(categoryId, pageable);
        if(products == null || products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Buscar productos por palabras clave",
            description = "Retorna productos que coincidan con las palabras clave proporcionadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos encontrados"),
            @ApiResponse(responseCode = "204", description = "No se encontraron productos")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ProductClientResponse>> searchProductsByKeywords(
            @Parameter(description = "Palabras clave para buscar", required = true)
            @RequestParam String keywords,
            @Parameter(description = "Parámetros de paginación (page, size)")
            Pageable pageable) {
        Page<ProductClientResponse> products = productService.searchProducts(keywords, pageable);
        if(products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(products);
    }
}