package com.shan.concurrency.threadspatterns.completablefuture;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * CompletableFuture Demo - Async API Call Chain with Error Handling
 *
 * Use Case: Compose asynchronous operations with error handling
 * Real-world Example: Fetch user profile, then orders, then recommendations (chained API calls)
 *
 * How it works:
 * 1. supplyAsync() - Start async computation returning a value
 * 2. thenApply() - Transform result when complete
 * 3. thenCompose() - Chain another CompletableFuture (flat map)
 * 4. thenCombine() - Combine two independent futures
 * 5. exceptionally() - Handle errors with fallback
 * 6. handle() - Handle both success and error cases
 * 7. allOf() / anyOf() - Wait for multiple futures
 */
@Slf4j
@Component
public class CompletableFutureDemo {

    private final ApiService apiService = new ApiService();

    public void demonstrate() {
        log.info("=== CompletableFuture Demo: Async API Call Chain ===");

        demonstrateBasicChaining();
        demonstrateCombining();
        demonstrateErrorHandling();
    }

    /**
     * Example 1: Chain async operations (profile -> orders -> recommendations)
     */
    private void demonstrateBasicChaining() {
        log.info("\n--- Example 1: Chaining Async Operations ---");
        String userId = "USER-123";

        CompletableFuture<Recommendations> futureRecommendations = apiService.fetchUserProfile(userId)
                .thenApply(profile -> {
                    log.info("[{}] Profile received, proceeding to fetch orders",
                            Thread.currentThread().getName());
                    return profile.getUserId();
                })
                .thenCompose(id -> {
                    log.info("[{}] Fetching order history for userId: {}",
                            Thread.currentThread().getName(), id);
                    return apiService.fetchOrderHistory(id);
                })
                .thenCompose(orderHistory -> {
                    log.info("[{}] Order history received, fetching recommendations",
                            Thread.currentThread().getName());
                    return apiService.fetchRecommendations(orderHistory);
                });

        try {
            Recommendations result = futureRecommendations.get(); // Block and wait
            log.info("[{}] Final result: {}", Thread.currentThread().getName(), result);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error in basic chaining", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Example 2: Combine multiple independent async operations
     */
    private void demonstrateCombining() {
        log.info("\n--- Example 2: Combining Independent Operations ---");
        String userId = "USER-456";

        CompletableFuture<UserProfile> profileFuture = apiService.fetchUserProfile(userId);
        CompletableFuture<OrderHistory> ordersFuture = apiService.fetchOrderHistory(userId);

        // Combine both results when both are ready
        CompletableFuture<String> combined = profileFuture.thenCombine(ordersFuture,
                (profile, orders) -> {
                    log.info("[{}] Combining results: {} with {} orders",
                            Thread.currentThread().getName(), profile.getName(), orders.getOrders().size());
                    return String.format("User: %s, Orders: %d", profile.getName(), orders.getOrders().size());
                });

        try {
            String result = combined.get();
            log.info("[{}] Combined result: {}", Thread.currentThread().getName(), result);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error in combining", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Example 3: Error handling with exceptionally and handle
     */
    private void demonstrateErrorHandling() {
        log.info("\n--- Example 3: Error Handling ---");

        // Success case
        CompletableFuture<String> successFuture = apiService.fetchWithPossibleError("USER-789", false)
                .exceptionally(ex -> {
                    log.warn("[{}] Handling error with fallback: {}",
                            Thread.currentThread().getName(), ex.getMessage());
                    return "FALLBACK_VALUE";
                });

        // Failure case with recovery
        CompletableFuture<String> failureFuture = apiService.fetchWithPossibleError("USER-999", true)
                .handle((result, ex) -> {
                    if (ex != null) {
                        log.warn("[{}] Error occurred, providing default: {}",
                                Thread.currentThread().getName(), ex.getCause().getMessage());
                        return "DEFAULT_VALUE";
                    }
                    return result;
                });

        try {
            log.info("[{}] Success case result: {}", Thread.currentThread().getName(), successFuture.get());
            log.info("[{}] Failure case result: {}", Thread.currentThread().getName(), failureFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error in error handling demo", e);
            Thread.currentThread().interrupt();
        }

        log.info("=== CompletableFuture Demo Completed ===");
    }
}
