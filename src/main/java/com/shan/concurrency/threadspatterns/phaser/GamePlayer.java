package com.shan.concurrency.threadspatterns.phaser;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Phaser;

/**
 * GamePlayer represents a player in a multi-phase game.
 * Each player must complete each phase before the game proceeds to the next phase.
 */
@Slf4j
public class GamePlayer implements Runnable {

    private final String playerName;
    private final Phaser phaser;
    private final int numberOfPhases;

    public GamePlayer(String playerName, Phaser phaser, int numberOfPhases) {
        this.playerName = playerName;
        this.phaser = phaser;
        this.numberOfPhases = numberOfPhases;
        phaser.register(); // Register this player with the phaser
    }

    @Override
    public void run() {
        try {
            log.info("[{}] Player '{}' joined the game (Total players: {})",
                    Thread.currentThread().getName(), playerName, phaser.getRegisteredParties());

            for (int phase = 1; phase <= numberOfPhases; phase++) {
                performPhase(phase);

                // Wait for all players to complete current phase
                log.info("[{}] Player '{}' waiting at phase {} (Arrived: {}/{})",
                        Thread.currentThread().getName(), playerName, phase,
                        phaser.getArrivedParties() + 1, phaser.getRegisteredParties());

                phaser.arriveAndAwaitAdvance();

                log.info("[{}] Player '{}' advancing from phase {}",
                        Thread.currentThread().getName(), playerName, phase);
            }

            log.info("[{}] Player '{}' completed all phases!", Thread.currentThread().getName(), playerName);

        } catch (InterruptedException e) {
            log.error("[{}] Player '{}' was interrupted", Thread.currentThread().getName(), playerName);
            Thread.currentThread().interrupt();
        } finally {
            phaser.arriveAndDeregister(); // Deregister when done
            log.info("[{}] Player '{}' left the game", Thread.currentThread().getName(), playerName);
        }
    }

    private void performPhase(int phase) throws InterruptedException {
        log.info("[{}] Player '{}' performing Phase-{}: {}",
                Thread.currentThread().getName(), playerName, phase, getPhaseDescription(phase));

        // Simulate phase work with variable time
        Thread.sleep(500 + (phase * 300));

        log.info("[{}] Player '{}' completed Phase-{}", Thread.currentThread().getName(), playerName, phase);
    }

    private String getPhaseDescription(int phase) {
        return switch (phase) {
            case 1 -> "Setup & Character Selection";
            case 2 -> "Main Gameplay";
            case 3 -> "Final Challenge";
            default -> "Unknown Phase";
        };
    }
}
