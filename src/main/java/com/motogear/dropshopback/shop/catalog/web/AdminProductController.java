package com.motogear.dropshopback.shop.catalog.web;

import com.motogear.dropshopback.shop.catalog.domain.Product;
import com.motogear.dropshopback.shop.catalog.dto.ImportRequest;
import com.motogear.dropshopback.shop.catalog.service.AsyncUpdate;
import com.motogear.dropshopback.shop.catalog.service.ImageProductService;
import com.motogear.dropshopback.shop.catalog.service.ProductServiceAdmin;
import com.motogear.dropshopback.common.dto.JobStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/products/admin")
@RequiredArgsConstructor
@Tag(name = "Productos Admin", description = "Endpoints administrativos para la gestión de productos - Solo accesible desde localhost")
public class AdminProductController {

    private final ProductServiceAdmin productService;
    private final AsyncUpdate asyncUpdate;
    private final ImageProductService imageProductService;

    @Operation(summary = "Obtener todos los productos (ADMIN)", description = "Retorna la lista completa de productos con todos sus detalles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo localhost")
    })
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllAdminProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @Operation(summary = "Obtener producto por ID (ADMIN)", description = "Retorna un producto específico por su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo localhost")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Product>> getProductAdminById(
            @Parameter(description = "ID del producto a buscar", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @Operation(summary = "Crear producto (ADMIN)", description = "Crea un nuevo producto manualmente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo localhost")
    })
    @PostMapping("/save")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.saveProduct(product));
    }
    @Operation(summary = "Importar producto desde AliExpress (ADMIN)",
               description = "Importa un producto desde una URL de AliExpress, procesa la información con IA y lo guarda en la base de datos{\n" +
                       "  \"url\": \"https://es.aliexpress.com/item/1005007226764379.html\",\n" +
                       "  \"categoryd\": \"4\"\n" +
                       "}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto importado y creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "URL inválida o faltante"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo localhost"),
        @ApiResponse(responseCode = "500", description = "Error durante la importación o procesamiento")
    })
    @PostMapping("/import")
    public ResponseEntity<Product> importProduct (@RequestBody ImportRequest request) {
        try {

            Product product = productService.importProductFromUrl(request.getUrl());
            // otro servicio para cambiar tiutlo al producto y sacar palabras clave
            product = productService.getChangesAiService(product);
            //luego guardar el producto
            product.setCategory(request.getCategoryId());

            if (product.getImages() == null || product.getImages().isEmpty()){
                return ResponseEntity.status(HttpStatus.valueOf("sin imagenes")).build();
            }else{
                product.getImages().get(0).setIsPrimary(true);
            }
            product=productService.saveProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar producto (ADMIN)", description = "Actualiza la información de un producto existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo localhost")
    })
    @PutMapping("/update")
    public ResponseEntity<Product> updateProduct(
            @RequestBody Product request) {
        return ResponseEntity.ok(productService.updateProduct(request));
    }

    @Operation(summary = "Actualizar productos mediante scraping (ADMIN)",
               description = "Inicia un proceso asíncrono para actualizar los productos desde AliExpress mediante scraping")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Proceso de actualización iniciado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo localhost")
    })
    @PostMapping("/scripting-update")
    public ResponseEntity<JobStatusDto> scriptingUpdateProduct (){
        String jobId = UUID.randomUUID().toString();
        asyncUpdate.executeUpdate();
        return ResponseEntity.accepted().body(new JobStatusDto(jobId, "STARTED"));
    }

    @Operation(summary = "Eliminar producto (ADMIN)", description = "Elimina un producto por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo localhost")
    })
    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID del producto a eliminar", required = true)
            @PathVariable Long id) {
        imageProductService.deleteImagesApiByProductId(id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
