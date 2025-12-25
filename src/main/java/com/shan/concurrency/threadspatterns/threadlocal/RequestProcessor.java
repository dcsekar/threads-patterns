package com.shan.concurrency.threadspatterns.threadlocal;

import lombok.extern.slf4j.Slf4j;

/**
 * RequestProcessor simulates processing a user request.
 * Uses ThreadLocal to maintain request context throughout the processing chain.
 */
@Slf4j
public class RequestProcessor implements Runnable {

    private final RequestContext requestContext;

    public RequestProcessor(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public void run() {
        try {
            // Step 1: Set context for this thread
            RequestContextHolder.setContext(requestContext);
            log.info("[{}] Processing started: {}",
                    Thread.currentThread().getName(),
                    RequestContextHolder.getContext());

            // Step 2: Simulate multi-layer processing
            authenticateUser();
            processBusinessLogic();
            auditLog();

        } catch (InterruptedException e) {
            log.error("[{}] Request processing interrupted for: {}",
                    Thread.currentThread().getName(), requestContext.getRequestId());
            Thread.currentThread().interrupt();
        } finally {
            // Step 3: ALWAYS clear ThreadLocal to prevent memory leaks
            RequestContextHolder.clear();
            log.info("[{}] Context cleared for request: {}",
                    Thread.currentThread().getName(), requestContext.getRequestId());
        }
    }

    private void authenticateUser() throws InterruptedException {
        // Access context without passing it as parameter
        RequestContext ctx = RequestContextHolder.getContext();
        log.info("[{}] Authenticating user: {} (Request: {})",
                Thread.currentThread().getName(), ctx.getUserId(), ctx.getRequestId());

        Thread.sleep(200);

        log.info("[{}] Authentication successful for user: {}",
                Thread.currentThread().getName(), ctx.getUserId());
    }

    private void processBusinessLogic() throws InterruptedException {
        RequestContext ctx = RequestContextHolder.getContext();
        log.info("[{}] Processing business logic for request: {}",
                Thread.currentThread().getName(), ctx.getRequestId());

        Thread.sleep(300);

        log.info("[{}] Business logic completed for request: {}",
                Thread.currentThread().getName(), ctx.getRequestId());
    }

    private void auditLog() {
        RequestContext ctx = RequestContextHolder.getContext();
        log.info("[{}] Audit: Request {} by user {} from IP {} at {}",
                Thread.currentThread().getName(),
                ctx.getRequestId(),
                ctx.getUserId(),
                ctx.getIpAddress(),
                ctx.getTimestamp());
    }
}
