package com.shan.concurrency.threadspatterns.completablefuture;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Recommendations {
    private String userId;
    private List<String> recommendedProducts;
}
