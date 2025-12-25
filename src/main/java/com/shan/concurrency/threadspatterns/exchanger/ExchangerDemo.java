package com.shan.concurrency.threadspatterns.exchanger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Exchanger Demo - Trade Exchange Between Two Traders
 *
 * Use Case: Bidirectional data exchange between two threads
 * Real-world Example: Two traders exchanging trade orders
 *
 * How it works:
 * 1. Create an Exchanger<T> for the data type to exchange
 * 2. Two threads each call exchange(data)
 * 3. Both threads block until both have called exchange()
 * 4. When both arrive, data is swapped and both threads continue
 * 5. Each thread receives the data from the other thread
 */
@Slf4j
@Component
public class ExchangerDemo {

    public void demonstrate() {
        log.info("=== Exchanger Demo: Trade Exchange ===");
        log.info("Scenario: Two traders exchanging trade orders");

        // Step 1: Create Exchanger for TradeOrder objects
        Exchanger<TradeOrder> exchanger = new Exchanger<>();

        // Step 2: Create trade orders
        TradeOrder order1 = new TradeOrder("Trader-A", "STOCK-AAPL", 100, 150.50);
        TradeOrder order2 = new TradeOrder("Trader-B", "STOCK-GOOGL", 50, 2800.75);

        // Step 3: Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            // Step 4: Submit traders
            executor.submit(new Trader("Trader-A", exchanger, order1));
            executor.submit(new Trader("Trader-B", exchanger, order2));

            log.info("[{}] Both traders submitted", Thread.currentThread().getName());

        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(30, TimeUnit.SECONDS);
                log.info("=== Exchanger Demo Completed ===");
            } catch (InterruptedException e) {
                log.error("Executor termination interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
