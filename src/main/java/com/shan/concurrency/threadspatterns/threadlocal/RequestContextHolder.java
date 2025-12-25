package com.shan.concurrency.threadspatterns.threadlocal;

/**
 * RequestContextHolder manages ThreadLocal storage for request context.
 * Each thread has its own isolated RequestContext that doesn't interfere with other threads.
 */
public class RequestContextHolder {

    // ThreadLocal variable - each thread gets its own copy
    private static final ThreadLocal<RequestContext> contextHolder = new ThreadLocal<>();

    /**
     * Set the RequestContext for the current thread
     */
    public static void setContext(RequestContext context) {
        contextHolder.set(context);
    }

    /**
     * Get the RequestContext for the current thread
     */
    public static RequestContext getContext() {
        return contextHolder.get();
    }

    /**
     * Clear the RequestContext for the current thread
     * IMPORTANT: Always clear ThreadLocal to prevent memory leaks
     */
    public static void clear() {
        contextHolder.remove();
    }
}
