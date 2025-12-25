package com.shan.concurrency.threadspatterns.exchanger;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Exchanger;

/**
 * Trader represents a trader who exchanges trade orders with another trader.
 * Demonstrates bidirectional data exchange between two threads.
 */
@Slf4j
public class Trader implements Runnable {

    private final String traderId;
    private final Exchanger<TradeOrder> exchanger;
    private final TradeOrder myOrder;

    public Trader(String traderId, Exchanger<TradeOrder> exchanger, TradeOrder myOrder) {
        this.traderId = traderId;
        this.exchanger = exchanger;
        this.myOrder = myOrder;
    }

    @Override
    public void run() {
        try {
            log.info("[{}] Trader '{}' preparing to exchange order: {}",
                    Thread.currentThread().getName(), traderId, myOrder);

            // Simulate order preparation
            Thread.sleep(500 + (int)(Math.random() * 1000));

            log.info("[{}] Trader '{}' ready to exchange at exchange point...",
                    Thread.currentThread().getName(), traderId);

            // Exchange orders with the other trader
            TradeOrder receivedOrder = exchanger.exchange(myOrder);

            log.info("[{}] Trader '{}' EXCHANGED! Sent: {} | Received: {}",
                    Thread.currentThread().getName(), traderId, myOrder, receivedOrder);

            // Process the received order
            processReceivedOrder(receivedOrder);

        } catch (InterruptedException e) {
            log.error("[{}] Trader '{}' was interrupted during exchange",
                    Thread.currentThread().getName(), traderId);
            Thread.currentThread().interrupt();
        }
    }

    private void processReceivedOrder(TradeOrder order) throws InterruptedException {
        log.info("[{}] Trader '{}' processing received order: {}",
                Thread.currentThread().getName(), traderId, order);

        Thread.sleep(300);

        log.info("[{}] Trader '{}' completed processing received order",
                Thread.currentThread().getName(), traderId);
    }
}
