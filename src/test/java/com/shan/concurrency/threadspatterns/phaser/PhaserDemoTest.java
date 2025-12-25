package com.shan.concurrency.threadspatterns.phaser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class PhaserDemoTest {

    @Autowired
    private PhaserDemo phaserDemo;

    @Test
    void testPhaserDemo() {
        assertDoesNotThrow(() -> phaserDemo.demonstrate(),
                "Phaser demo should execute without throwing exceptions");
    }
}
