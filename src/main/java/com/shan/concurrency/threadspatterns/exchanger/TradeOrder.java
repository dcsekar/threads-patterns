package com.shan.concurrency.threadspatterns.exchanger;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TradeOrder represents a trade order that will be exchanged between traders.
 */
@Data
@AllArgsConstructor
public class TradeOrder {
    private String traderId;
    private String asset;
    private int quantity;
    private double price;

    @Override
    public String toString() {
        return String.format("TradeOrder{trader='%s', asset='%s', qty=%d, price=$%.2f}",
                traderId, asset, quantity, price);
    }
}
