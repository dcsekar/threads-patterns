package com.shan.concurrency.threadspatterns.blockingqueue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class BlockingQueueDemoTest {

    @Autowired
    private BlockingQueueDemo blockingQueueDemo;

    @Test
    void testBlockingQueueDemo() {
        assertDoesNotThrow(() -> blockingQueueDemo.demonstrate(),
                "BlockingQueue demo should execute without throwing exceptions");
    }
}
