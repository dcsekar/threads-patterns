package com.shan.concurrency.threadspatterns.virtualthreads;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class VirtualThreadsDemoTest {

    @Autowired
    private VirtualThreadsDemo virtualThreadsDemo;

    @Test
    void testVirtualThreadsDemo() {
        assertDoesNotThrow(() -> virtualThreadsDemo.demonstrate(),
                "VirtualThreads demo should execute without throwing exceptions");
    }
}
