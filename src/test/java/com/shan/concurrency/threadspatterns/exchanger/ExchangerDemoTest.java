package com.shan.concurrency.threadspatterns.exchanger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ExchangerDemoTest {

    @Autowired
    private ExchangerDemo exchangerDemo;

    @Test
    void testExchangerDemo() {
        assertDoesNotThrow(() -> exchangerDemo.demonstrate(),
                "Exchanger demo should execute without throwing exceptions");
    }
}
