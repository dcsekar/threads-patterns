package com.shan.concurrency.threadspatterns.completablefuture;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfile {
    private String userId;
    private String name;
    private String email;
}
