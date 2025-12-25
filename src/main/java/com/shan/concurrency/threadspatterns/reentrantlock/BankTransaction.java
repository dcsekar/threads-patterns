package com.shan.concurrency.threadspatterns.reentrantlock;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BankTransaction represents a banking transaction (deposit or withdrawal).
 */
@Slf4j
@AllArgsConstructor
public class BankTransaction implements Runnable {

    private final BankAccount account;
    private final String transactionId;
    private final TransactionType type;
    private final double amount;

    @Override
    public void run() {
        log.info("[{}] Transaction {} started: {} ${}",
                Thread.currentThread().getName(), transactionId, type, amount);

        switch (type) {
            case DEPOSIT -> account.deposit(amount, transactionId);
            case WITHDRAW -> account.withdraw(amount, transactionId);
        }
    }

    public enum TransactionType {
        DEPOSIT, WITHDRAW
    }
}
