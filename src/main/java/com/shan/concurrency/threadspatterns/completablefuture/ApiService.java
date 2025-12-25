package com.shan.concurrency.threadspatterns.completablefuture;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * ApiService simulates external API calls for user data.
 * Each method returns CompletableFuture for async processing.
 */
@Slf4j
public class ApiService {

    /**
     * Fetch user profile asynchronously
     */
    public CompletableFuture<UserProfile> fetchUserProfile(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("[{}] Fetching user profile for userId: {}",
                    Thread.currentThread().getName(), userId);

            simulateApiDelay(800);

            UserProfile profile = new UserProfile(userId, "User-" + userId, userId + "@example.com");

            log.info("[{}] User profile fetched: {}", Thread.currentThread().getName(), profile);
            return profile;
        });
    }

    /**
     * Fetch order history asynchronously
     */
    public CompletableFuture<OrderHistory> fetchOrderHistory(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("[{}] Fetching order history for userId: {}",
                    Thread.currentThread().getName(), userId);

            simulateApiDelay(1000);

            List<String> orders = Arrays.asList("Order-1", "Order-2", "Order-3");
            OrderHistory history = new OrderHistory(userId, orders);

            log.info("[{}] Order history fetched: {} orders", Thread.currentThread().getName(), orders.size());
            return history;
        });
    }

    /**
     * Fetch recommendations based on order history
     */
    public CompletableFuture<Recommendations> fetchRecommendations(OrderHistory orderHistory) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("[{}] Generating recommendations based on {} orders",
                    Thread.currentThread().getName(), orderHistory.getOrders().size());

            simulateApiDelay(600);

            List<String> products = Arrays.asList("Product-A", "Product-B", "Product-C");
            Recommendations recommendations = new Recommendations(orderHistory.getUserId(), products);

            log.info("[{}] Recommendations generated: {}", Thread.currentThread().getName(), products);
            return recommendations;
        });
    }

    /**
     * Simulate a failed API call
     */
    public CompletableFuture<String> fetchWithPossibleError(String userId, boolean shouldFail) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("[{}] Fetching data (shouldFail: {})", Thread.currentThread().getName(), shouldFail);

            simulateApiDelay(300);

            if (shouldFail) {
                log.error("[{}] API call failed!", Thread.currentThread().getName());
                throw new RuntimeException("API call failed for userId: " + userId);
            }

            return "Success-" + userId;
        });
    }

    private void simulateApiDelay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted during API call", e);
        }
    }
}
