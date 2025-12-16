package com.motogear.dropshopback.shop.blog.service;

import com.motogear.dropshopback.shop.blog.domain.Post;
import com.motogear.dropshopback.shop.blog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Post createPost(Post post) {
        post.setDate(java.time.LocalDate.now());
        return postRepository.save(post);
    }
    public Post findPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }
    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }
    public List<Post> findPostsByTag(String tag) {
        return postRepository.findByTagsContaining(tag);
    }
    public Post updatePost(Post post) {
        return postRepository.save(post);
    }
    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }

}
