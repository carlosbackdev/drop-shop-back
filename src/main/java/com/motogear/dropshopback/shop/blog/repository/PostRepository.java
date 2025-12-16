package com.motogear.dropshopback.shop.blog.repository;

import com.motogear.dropshopback.shop.blog.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTagsContaining(String tags);
}
