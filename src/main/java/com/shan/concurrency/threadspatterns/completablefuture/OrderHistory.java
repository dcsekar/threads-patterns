package com.shan.concurrency.threadspatterns.completablefuture;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderHistory {
    private String userId;
    private List<String> orders;
}
