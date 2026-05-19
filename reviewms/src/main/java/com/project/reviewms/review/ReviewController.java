package com.project.reviewms.review;

import com.project.reviewms.review.messaging.ReviewMessageProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMessageProducer reviewMessageProducer;

    public ReviewController(ReviewService reviewService,  ReviewMessageProducer reviewMessageProducer) {
        this.reviewService = reviewService;
        this.reviewMessageProducer = reviewMessageProducer;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews(@RequestParam Long companyId){
        return new ResponseEntity<>(reviewService.getAllReviews(companyId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addReview(@RequestParam Long companyId, @RequestBody Review review){
        boolean isReviewed = reviewService.addReview(companyId, review);

        if (isReviewed){
            reviewMessageProducer.sendMessage(review);
            return new ResponseEntity<>("Review added successfully", HttpStatus.OK);
        }

        return new ResponseEntity<>("Failed to add review", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReview(@PathVariable Long reviewId){
        Review review = reviewService.getReview(reviewId);

        if (review == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable Long reviewId, @RequestBody Review review){
        boolean isReviewedUpdated = reviewService.updateReview(reviewId, review);

        if (isReviewedUpdated){
        return new ResponseEntity<>("Review updated successfully", HttpStatus.OK);

        }
        return new ResponseEntity<>("Failed to update review", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId){
        boolean isReviewedDeleted = reviewService.deleteReview(reviewId);

        if (isReviewedDeleted){
            return new ResponseEntity<>("Review deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Failed to delete review", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/averageRating")
    public double getAverageReview(@RequestParam Long companyId) {
        List<Review> reviewList = reviewService.getAllReviews(companyId);

        return reviewList.stream().mapToDouble(Review::getRating).average().orElse(0.0);
    }
}
