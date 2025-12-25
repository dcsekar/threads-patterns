package com.shan.concurrency.threadspatterns.reentrantlock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ReentrantLock Demo - Thread-Safe Bank Account
 *
 * Use Case: Explicit lock control with advanced features
 * Real-world Example: Bank account with concurrent deposits/withdrawals
 *
 * How it works:
 * 1. Create ReentrantLock(fair) - fair mode prevents thread starvation
 * 2. Call lock() to acquire (blocks if held by another thread)
 * 3. Execute critical section
 * 4. ALWAYS call unlock() in finally block
 * 5. Advantages over synchronized: tryLock(), interruptible, fairness, lock monitoring
 */
@Slf4j
@Component
public class ReentrantLockDemo {

    private static final int NUMBER_OF_TRANSACTIONS = 8;

    public void demonstrate() {
        log.info("=== ReentrantLock Demo: Thread-Safe Bank Account ===");
        demonstrateWithFairness(false);
        demonstrateWithFairness(true);
    }

    private void demonstrateWithFairness(boolean fair) {
        log.info("\n--- Testing with Fairness: {} ---", fair);

        BankAccount account = new BankAccount("ACC-001", 1000.0, fair);

        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            // Submit mixed deposit and withdrawal transactions
            for (int i = 1; i <= NUMBER_OF_TRANSACTIONS; i++) {
                String txId = "TX-" + i;
                BankTransaction.TransactionType type = (i % 2 == 0)
                        ? BankTransaction.TransactionType.DEPOSIT
                        : BankTransaction.TransactionType.WITHDRAW;
                double amount = 100.0 + (i * 50);

                executor.submit(new BankTransaction(account, txId, type, amount));
                Thread.sleep(50); // Small delay between submissions
            }

            log.info("[{}] All transactions submitted (Fair mode: {})",
                    Thread.currentThread().getName(), fair);

        } catch (InterruptedException e) {
            log.error("Demo interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(30, TimeUnit.SECONDS);
                log.info("Final balance: ${}", account.getBalance());
                log.info("=== Fairness {} Test Completed ===\n", fair);
            } catch (InterruptedException e) {
                log.error("Executor termination interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
