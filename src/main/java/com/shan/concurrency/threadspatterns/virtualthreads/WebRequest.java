package com.shan.concurrency.threadspatterns.virtualthreads;

import lombok.extern.slf4j.Slf4j;

/**
 * WebRequest simulates handling a web request that involves I/O operations.
 * Virtual threads are perfect for I/O-bound tasks.
 */
@Slf4j
public class WebRequest implements Runnable {

    private final int requestId;
    private final String threadType;

    public WebRequest(int requestId, String threadType) {
        this.requestId = requestId;
        this.threadType = threadType;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        log.info("[{}] ({}) Request-{} started",
                Thread.currentThread().getName(), threadType, requestId);

        try {
            // Simulate database query
            performDatabaseQuery();

            // Simulate external API call
            callExternalApi();

            // Simulate response processing
            processResponse();

            long duration = System.currentTimeMillis() - startTime;
            log.info("[{}] ({}) Request-{} completed in {} ms",
                    Thread.currentThread().getName(), threadType, requestId, duration);

        } catch (InterruptedException e) {
            log.error("[{}] Request-{} interrupted", Thread.currentThread().getName(), requestId);
            Thread.currentThread().interrupt();
        }
    }

    private void performDatabaseQuery() throws InterruptedException {
        log.info("[{}] Request-{} querying database...", Thread.currentThread().getName(), requestId);
        Thread.sleep(100); // Simulate I/O wait
    }

    private void callExternalApi() throws InterruptedException {
        log.info("[{}] Request-{} calling external API...", Thread.currentThread().getName(), requestId);
        Thread.sleep(150); // Simulate I/O wait
    }

    private void processResponse() throws InterruptedException {
        log.info("[{}] Request-{} processing response...", Thread.currentThread().getName(), requestId);
        Thread.sleep(50);
    }
}
