package com.shan.concurrency.threadspatterns.semaphore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class SemaphoreDemoTest {

    @Autowired
    private SemaphoreDemo semaphoreDemo;

    @Test
    void testSemaphoreDemo() {
        assertDoesNotThrow(() -> semaphoreDemo.demonstrate(),
                "Semaphore demo should execute without throwing exceptions");
    }
}
