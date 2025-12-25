package com.shan.concurrency.threadspatterns.phaser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * Phaser Demo - Multi-Phase Game Rounds
 *
 * Use Case: Coordinate multiple threads through multiple phases
 * Real-world Example: Multiplayer game where all players must complete each round before advancing
 *
 * How it works:
 * 1. Create a Phaser (optionally with initial parties)
 * 2. Threads register() to join and arriveAndAwaitAdvance() at each phase
 * 3. When all registered parties arrive, phase advances
 * 4. Threads can arriveAndDeregister() when done
 * 5. More flexible than CyclicBarrier: dynamic registration, multiple phases
 */
@Slf4j
@Component
public class PhaserDemo {

    private static final int NUMBER_OF_PLAYERS = 4;
    private static final int NUMBER_OF_PHASES = 3;

    public void demonstrate() {
        log.info("=== Phaser Demo: Multi-Phase Game Rounds ===");
        log.info("Scenario: {} players playing a {}-phase game", NUMBER_OF_PLAYERS, NUMBER_OF_PHASES);

        // Step 1: Create Phaser with initial party (main thread coordinates)
        Phaser phaser = new Phaser(1) {
            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                log.info("[{}] *** PHASE {} COMPLETED *** (Parties: {})",
                        Thread.currentThread().getName(), phase, registeredParties);
                return phase >= NUMBER_OF_PHASES - 1 || registeredParties == 0;
            }
        };

        // Step 2: Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_PLAYERS);

        try {
            // Step 3: Create and submit players
            for (int i = 1; i <= NUMBER_OF_PLAYERS; i++) {
                String playerName = "Player-" + i;
                executor.submit(new GamePlayer(playerName, phaser, NUMBER_OF_PHASES));
                Thread.sleep(100); // Stagger player joins
            }

            log.info("[{}] All players created. Game starting...", Thread.currentThread().getName());

            // Main thread coordinates by arriving at each phase
            for (int phase = 0; phase < NUMBER_OF_PHASES; phase++) {
                phaser.arriveAndAwaitAdvance();
                log.info("[{}] Main thread: Phase {} synchronized", Thread.currentThread().getName(), phase);
            }

            phaser.arriveAndDeregister(); // Main thread done coordinating

            log.info("[{}] Game coordinator finished", Thread.currentThread().getName());

        } catch (InterruptedException e) {
            log.error("Demo interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(60, TimeUnit.SECONDS);
                log.info("=== Phaser Demo Completed ===");
            } catch (InterruptedException e) {
                log.error("Executor termination interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
