package com.motogear.dropshopback.shop.catalog.service;

import com.motogear.dropshopback.shop.catalog.domain.Review;
import com.motogear.dropshopback.shop.catalog.dto.ReviewRequest;
import com.motogear.dropshopback.shop.catalog.repository.ReviewRepository;
import com.motogear.dropshopback.shop.order.domain.Order;
import com.motogear.dropshopback.shop.order.domain.OrderStatus;
import com.motogear.dropshopback.shop.order.service.OrderService;
import com.motogear.dropshopback.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final OrderService orderService;

    @Transactional(readOnly = true)
    public List<Review> findByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Transactional
    public Review save(ReviewRequest review) {
        if(!canSaveReview(review.getProductId())){
            throw new IllegalStateException("User cannot review this product without purchasing it.");
        }
        if(findReviewsByUserIdAndProduct(userService.getCurrentUser().getId(), review.getProductId()) != null){
            throw new IllegalStateException("User has already reviewed this product.");
        }
        return reviewRepository.save(createReview(review, userService.getCurrentUser().getId()));
    }

    @Transactional
    public void deleteReviewsById(Long id) {
        reviewRepository.deleteById(id);
    }

    public Boolean canSaveReview(Long productId) {
        List<Order> orders = orderService.listMyOrders();
        for (Order order : orders) {
            if (order.getCartShadedIds().contains(productId) && order.getStatus() == OrderStatus.PAID) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public Review findReviewsByUserIdAndProduct(Long userId, Long productId) {
        return reviewRepository.findByUserIdAndProductId(userId, productId);
    }

    private Review createReview(ReviewRequest reviewRequest, Long userId) {
        Review review = new Review();
        review.setUserId(userId);
        review.setProductId(reviewRequest.getProductId());
        review.setRating(reviewRequest.getRating());
        review.setContent(reviewRequest.getContent());
        return review;
    }
}
