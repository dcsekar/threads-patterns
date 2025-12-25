package com.shan.concurrency.threadspatterns.threadlocal;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * RequestContext holds per-request information.
 * Each thread processing a request has its own isolated copy.
 */
@Data
@AllArgsConstructor
public class RequestContext {
    private String requestId;
    private String userId;
    private LocalDateTime timestamp;
    private String ipAddress;

    @Override
    public String toString() {
        return String.format("RequestContext{requestId='%s', userId='%s', timestamp=%s, ip='%s'}",
                requestId, userId, timestamp, ipAddress);
    }
}
