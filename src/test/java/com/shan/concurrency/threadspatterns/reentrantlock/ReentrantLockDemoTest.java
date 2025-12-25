package com.shan.concurrency.threadspatterns.reentrantlock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ReentrantLockDemoTest {

    @Autowired
    private ReentrantLockDemo reentrantLockDemo;

    @Test
    void testReentrantLockDemo() {
        assertDoesNotThrow(() -> reentrantLockDemo.demonstrate(),
                "ReentrantLock demo should execute without throwing exceptions");
    }
}
