package com.shan.concurrency.threadspatterns.threadlocal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ThreadLocal Demo - Per-Thread Logging Context
 *
 * Use Case: Maintain thread-specific data without passing it as parameters
 * Real-world Example: Web request context (user, session, request ID) available throughout call stack
 *
 * How it works:
 * 1. Create a ThreadLocal<T> variable
 * 2. Each thread gets its own isolated copy of the variable
 * 3. Call set(value) to store, get() to retrieve
 * 4. MUST call remove() when done to prevent memory leaks
 * 5. Perfect for request-scoped data in web applications
 */
@Slf4j
@Component
public class ThreadLocalDemo {

    private static final int NUMBER_OF_REQUESTS = 5;

    public void demonstrate() {
        log.info("=== ThreadLocal Demo: Per-Thread Request Context ===");
        log.info("Scenario: Processing {} concurrent user requests with isolated contexts", NUMBER_OF_REQUESTS);

        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            // Submit multiple request processors
            for (int i = 1; i <= NUMBER_OF_REQUESTS; i++) {
                RequestContext context = new RequestContext(
                        "REQ-" + i,
                        "User-" + i,
                        LocalDateTime.now(),
                        "192.168.1." + (100 + i)
                );

                executor.submit(new RequestProcessor(context));
                Thread.sleep(50); // Stagger submissions
            }

            log.info("[{}] All requests submitted", Thread.currentThread().getName());

        } catch (InterruptedException e) {
            log.error("Demo interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(30, TimeUnit.SECONDS);
                log.info("=== ThreadLocal Demo Completed ===");
            } catch (InterruptedException e) {
                log.error("Executor termination interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
