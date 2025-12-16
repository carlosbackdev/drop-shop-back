package com.motogear.dropshopback.shop.blog.web;

import com.motogear.dropshopback.shop.blog.domain.Post;
import com.motogear.dropshopback.shop.blog.service.PostService;
import com.motogear.dropshopback.shop.catalog.service.ImageProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
@Tag(name = "Blog", description = "Endpoints para gestión del BLOG")
public class PostController {
    private final PostService postService;
    private final ImageProductService imageProductService;


    @PostMapping("/admin/create")
    @Operation(
            summary = "Crear un nuevo post",
            description = "Permite a los administradores crear un nuevo post para el blog"
    )
    public ResponseEntity<Post> createPost(@Valid @RequestBody Post post) {
        return ResponseEntity.ok(postService.createPost(post));
    }

    @DeleteMapping("/admin/delete/{id}")
    @Operation(
            summary = "Eliminar un banner de la página principal",
            description = "Permite a los administradores eliminar un banner existente de la página principal"
    )
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
    postService.deletePostById(id);
    imageProductService.deleteImagesByPostId(id);
    return ResponseEntity.noContent().build();
    }

    @GetMapping("/get/all")
    @Operation(
            summary = "Obtener todos los posts",
            description = "Retorna una lista de todos los post para el BLOG"
    )
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.findAllPosts());
    }
    @GetMapping("/get/{id}")
    @Operation(
            summary = "Obtener un post por ID",
            description = "Retorna un post específico del BLOG según su ID")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.findPostById(id));
    }

    @PutMapping("/admin/update")
    @Operation(
            summary = "Actualizar un post",
            description = "Permite a los administradores actualizar un post del BLOG")
    public ResponseEntity<Post> updatePost(Post post) {
            return ResponseEntity.ok(postService.updatePost(post));
        }


}
