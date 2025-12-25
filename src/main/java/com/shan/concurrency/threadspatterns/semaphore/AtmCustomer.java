package com.shan.concurrency.threadspatterns.semaphore;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

/**
 * AtmCustomer represents a bank customer trying to use an ATM terminal.
 * Only a limited number of customers can use ATMs simultaneously (controlled by semaphore).
 */
@Slf4j
public class AtmCustomer implements Runnable {

    private final String customerName;
    private final Semaphore atmSemaphore;
    private final int transactionTimeMs;

    public AtmCustomer(String customerName, Semaphore atmSemaphore, int transactionTimeMs) {
        this.customerName = customerName;
        this.atmSemaphore = atmSemaphore;
        this.transactionTimeMs = transactionTimeMs;
    }

    @Override
    public void run() {
        try {
            log.info("[{}] Customer '{}' arrived at ATM (Available terminals: {})",
                    Thread.currentThread().getName(), customerName, atmSemaphore.availablePermits());

            // Try to acquire ATM terminal (permit)
            log.info("[{}] Customer '{}' waiting for ATM terminal...",
                    Thread.currentThread().getName(), customerName);

            atmSemaphore.acquire();

            // Critical section: Using ATM
            log.info("[{}] Customer '{}' ACQUIRED ATM terminal (Available terminals: {})",
                    Thread.currentThread().getName(), customerName, atmSemaphore.availablePermits());

            performTransaction();

        } catch (InterruptedException e) {
            log.error("[{}] Customer '{}' was interrupted",
                    Thread.currentThread().getName(), customerName);
            Thread.currentThread().interrupt();
        } finally {
            // Release the ATM terminal
            atmSemaphore.release();
            log.info("[{}] Customer '{}' RELEASED ATM terminal (Available terminals: {})",
                    Thread.currentThread().getName(), customerName, atmSemaphore.availablePermits());
        }
    }

    private void performTransaction() throws InterruptedException {
        log.info("[{}] Customer '{}' performing transaction...",
                Thread.currentThread().getName(), customerName);

        Thread.sleep(transactionTimeMs);

        log.info("[{}] Customer '{}' transaction completed",
                Thread.currentThread().getName(), customerName);
    }
}
