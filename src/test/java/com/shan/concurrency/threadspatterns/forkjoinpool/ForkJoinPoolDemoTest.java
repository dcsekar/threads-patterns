package com.shan.concurrency.threadspatterns.forkjoinpool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ForkJoinPoolDemoTest {

    @Autowired
    private ForkJoinPoolDemo forkJoinPoolDemo;

    @Test
    void testForkJoinPoolDemo() {
        assertDoesNotThrow(() -> forkJoinPoolDemo.demonstrate(),
                "ForkJoinPool demo should execute without throwing exceptions");
    }
}
