package com.shan.concurrency.threadspatterns.cyclicbarrier;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class CyclicBarrierDemoTest {

    @Autowired
    private CyclicBarrierDemo cyclicBarrierDemo;

    @Test
    void testCyclicBarrierDemo() {
        assertDoesNotThrow(() -> cyclicBarrierDemo.demonstrate(),
                "CyclicBarrier demo should execute without throwing exceptions");
    }
}
