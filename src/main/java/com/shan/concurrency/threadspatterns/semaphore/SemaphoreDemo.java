package com.shan.concurrency.threadspatterns.semaphore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Semaphore Demo - ATM Access Control
 *
 * Use Case: Limit concurrent access to a shared resource
 * Real-world Example: Bank with 3 ATM terminals serving 10 customers
 *
 * How it works:
 * 1. Create Semaphore with number of permits (available resources)
 * 2. Threads call acquire() to get permit (blocks if none available)
 * 3. Thread uses the resource
 * 4. Thread calls release() to return permit
 * 5. Supports fairness to prevent starvation
 */
@Slf4j
@Component
public class SemaphoreDemo {

    private static final int NUMBER_OF_ATM_TERMINALS = 3;
    private static final int NUMBER_OF_CUSTOMERS = 10;

    public void demonstrate() {
        log.info("=== Semaphore Demo: ATM Access Control ===");
        log.info("Scenario: {} ATM terminals serving {} customers", NUMBER_OF_ATM_TERMINALS, NUMBER_OF_CUSTOMERS);

        // Step 1: Create Semaphore with permits = number of ATM terminals
        // Use fairness = true to prevent starvation
        Semaphore atmSemaphore = new Semaphore(NUMBER_OF_ATM_TERMINALS, true);

        // Step 2: Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_CUSTOMERS);

        try {
            // Step 3: Submit customer tasks
            for (int i = 1; i <= NUMBER_OF_CUSTOMERS; i++) {
                String customerName = "Customer-" + i;
                int transactionTime = 1000 + (i * 200); // Variable transaction time
                executor.submit(new AtmCustomer(customerName, atmSemaphore, transactionTime));
                Thread.sleep(100); // Stagger arrivals
            }

            log.info("[{}] All customers submitted to queue", Thread.currentThread().getName());

        } catch (InterruptedException e) {
            log.error("Demo interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(60, TimeUnit.SECONDS);
                log.info("=== Semaphore Demo Completed ===");
            } catch (InterruptedException e) {
                log.error("Executor termination interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
