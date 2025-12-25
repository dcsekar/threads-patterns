package com.shan.concurrency.threadspatterns.reentrantlock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * BankAccount represents a shared resource that must be thread-safe.
 * Uses ReentrantLock for explicit locking with optional fairness.
 */
@Slf4j
public class BankAccount {

    private double balance;
    private final Lock lock;
    private final String accountId;

    public BankAccount(String accountId, double initialBalance, boolean fair) {
        this.accountId = accountId;
        this.balance = initialBalance;
        // ReentrantLock with fairness parameter
        // fair = true: longest-waiting thread gets lock (prevents starvation)
        // fair = false: no guarantee (better performance)
        this.lock = new ReentrantLock(fair);
        log.info("BankAccount '{}' created with balance ${} (Fair mode: {})",
                accountId, initialBalance, fair);
    }

    public void deposit(double amount, String transactionId) {
        // Acquire lock
        lock.lock();
        try {
            log.info("[{}] {} LOCKED | Depositing ${} (Current: ${}) | Waiting threads: {}",
                    Thread.currentThread().getName(),
                    transactionId,
                    amount,
                    balance,
                    ((ReentrantLock) lock).getQueueLength());

            // Simulate processing time
            Thread.sleep(200);

            balance += amount;

            log.info("[{}] {} COMPLETED | New balance: ${} | Hold count: {}",
                    Thread.currentThread().getName(),
                    transactionId,
                    balance,
                    ((ReentrantLock) lock).getHoldCount());

        } catch (InterruptedException e) {
            log.error("[{}] {} interrupted", Thread.currentThread().getName(), transactionId);
            Thread.currentThread().interrupt();
        } finally {
            // ALWAYS release lock in finally block
            lock.unlock();
            log.info("[{}] {} UNLOCKED", Thread.currentThread().getName(), transactionId);
        }
    }

    public void withdraw(double amount, String transactionId) {
        lock.lock();
        try {
            log.info("[{}] {} LOCKED | Withdrawing ${} (Current: ${}) | Waiting threads: {}",
                    Thread.currentThread().getName(),
                    transactionId,
                    amount,
                    balance,
                    ((ReentrantLock) lock).getQueueLength());

            Thread.sleep(200);

            if (balance >= amount) {
                balance -= amount;
                log.info("[{}] {} COMPLETED | New balance: ${}",
                        Thread.currentThread().getName(), transactionId, balance);
            } else {
                log.warn("[{}] {} FAILED | Insufficient funds (Balance: ${}, Requested: ${})",
                        Thread.currentThread().getName(), transactionId, balance, amount);
            }

        } catch (InterruptedException e) {
            log.error("[{}] {} interrupted", Thread.currentThread().getName(), transactionId);
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
            log.info("[{}] {} UNLOCKED", Thread.currentThread().getName(), transactionId);
        }
    }

    public double getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }
}
