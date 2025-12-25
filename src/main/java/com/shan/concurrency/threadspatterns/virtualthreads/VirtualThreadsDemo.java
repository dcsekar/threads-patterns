package com.shan.concurrency.threadspatterns.virtualthreads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * VirtualThreads Demo - High-Throughput Web Server Simulation
 *
 * Use Case: Handle millions of concurrent I/O-bound tasks efficiently
 * Real-world Example: Web server handling thousands of concurrent requests
 *
 * What are Virtual Threads (Java 21+):
 * - Lightweight threads managed by the JVM (not OS)
 * - Can create millions without performance penalty
 * - Perfect for I/O-bound tasks (network calls, file I/O, database queries)
 * - Automatically yield during blocking operations
 * - Same Thread API, different implementation
 *
 * How it works:
 * 1. Thread.ofVirtual() - Create single virtual thread
 * 2. Executors.newVirtualThreadPerTaskExecutor() - Executor that creates virtual thread per task
 * 3. Virtual threads are cheap: 1000s of virtual threads = few platform threads
 * 4. JVM automatically multiplexes virtual threads onto platform threads
 */
@Slf4j
@Component
public class VirtualThreadsDemo {

    private static final int NUMBER_OF_REQUESTS = 1000;

    public void demonstrate() {
        log.info("=== VirtualThreads Demo: High-Throughput Web Server ===");

        log.info("\n--- Comparison: Platform Threads vs Virtual Threads ---");
        log.info("Handling {} concurrent requests\n", NUMBER_OF_REQUESTS);

        // Compare platform threads vs virtual threads
        demonstratePlatformThreads();
        demonstrateVirtualThreads();
        demonstrateStructuredConcurrency();
    }

    /**
     * Traditional approach: Platform threads (OS threads)
     * Limited by OS thread count, expensive to create
     */
    private void demonstratePlatformThreads() {
        log.info("--- Using Platform Threads (Traditional) ---");

        try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
            long startTime = System.currentTimeMillis();

            for (int i = 1; i <= NUMBER_OF_REQUESTS; i++) {
                executor.submit(new WebRequest(i, "Platform"));
                if (i % 100 == 0) {
                    Thread.sleep(50); // Throttle submission
                }
            }

            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.SECONDS);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Platform threads completed {} requests in {} ms\n", NUMBER_OF_REQUESTS, duration);

        } catch (InterruptedException e) {
            log.error("Platform threads demo interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Modern approach: Virtual threads (JVM-managed)
     * Millions of threads possible, cheap to create
     */
    private void demonstrateVirtualThreads() {
        log.info("--- Using Virtual Threads (Java 21+) ---");

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            long startTime = System.currentTimeMillis();

            for (int i = 1; i <= NUMBER_OF_REQUESTS; i++) {
                executor.submit(new WebRequest(i, "Virtual"));
            }

            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.SECONDS);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Virtual threads completed {} requests in {} ms\n", NUMBER_OF_REQUESTS, duration);

        } catch (InterruptedException e) {
            log.error("Virtual threads demo interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Create individual virtual threads
     */
    private void demonstrateStructuredConcurrency() {
        log.info("--- Creating Individual Virtual Threads ---");

        try {
            // Create and start a virtual thread
            Thread vThread1 = Thread.ofVirtual()
                    .name("VirtualThread-Custom-1")
                    .start(() -> {
                        log.info("[{}] Custom virtual thread executing",
                                Thread.currentThread().getName());
                        try {
                            Thread.sleep(Duration.ofMillis(500));
                            log.info("[{}] Custom virtual thread completed",
                                    Thread.currentThread().getName());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });

            // Create unstarted virtual thread
            Thread vThread2 = Thread.ofVirtual()
                    .name("VirtualThread-Custom-2")
                    .unstarted(() -> {
                        log.info("[{}] Another virtual thread executing",
                                Thread.currentThread().getName());
                    });

            vThread2.start();

            // Wait for completion
            vThread1.join();
            vThread2.join();

            log.info("Individual virtual threads completed");

        } catch (InterruptedException e) {
            log.error("Structured concurrency demo interrupted", e);
            Thread.currentThread().interrupt();
        }

        log.info("\n=== VirtualThreads Demo Completed ===");
    }
}
