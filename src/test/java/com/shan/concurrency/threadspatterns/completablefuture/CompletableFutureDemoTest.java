package com.shan.concurrency.threadspatterns.completablefuture;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class CompletableFutureDemoTest {

    @Autowired
    private CompletableFutureDemo completableFutureDemo;

    @Test
    void testCompletableFutureDemo() {
        assertDoesNotThrow(() -> completableFutureDemo.demonstrate(),
                "CompletableFuture demo should execute without throwing exceptions");
    }
}
