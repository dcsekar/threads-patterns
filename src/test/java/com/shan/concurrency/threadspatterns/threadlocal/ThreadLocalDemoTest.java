package com.shan.concurrency.threadspatterns.threadlocal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ThreadLocalDemoTest {

    @Autowired
    private ThreadLocalDemo threadLocalDemo;

    @Test
    void testThreadLocalDemo() {
        assertDoesNotThrow(() -> threadLocalDemo.demonstrate(),
                "ThreadLocal demo should execute without throwing exceptions");
    }
}
