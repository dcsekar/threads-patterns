package com.shan.concurrency.threadspatterns.countdownlatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class CountDownLatchDemoTest {

    @Autowired
    private CountDownLatchDemo countDownLatchDemo;

    @Test
    void testCountDownLatchDemo() {
        assertDoesNotThrow(() -> countDownLatchDemo.demonstrate(),
                "CountDownLatch demo should execute without throwing exceptions");
    }
}
