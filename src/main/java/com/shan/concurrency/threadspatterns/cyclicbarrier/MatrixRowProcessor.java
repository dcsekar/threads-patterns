package com.shan.concurrency.threadspatterns.cyclicbarrier;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * MatrixRowProcessor simulates processing a row of a matrix.
 * All rows must be processed before moving to the next phase (e.g., column processing).
 */
@Slf4j
public class MatrixRowProcessor implements Runnable {

    private final int rowNumber;
    private final int[] rowData;
    private final CyclicBarrier barrier;

    public MatrixRowProcessor(int rowNumber, int[] rowData, CyclicBarrier barrier) {
        this.rowNumber = rowNumber;
        this.rowData = rowData;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            // Phase 1: Process row data
            log.info("[{}] Processing Row-{}: Starting...",
                    Thread.currentThread().getName(), rowNumber);

            int sum = 0;
            for (int value : rowData) {
                sum += value;
                Thread.sleep(100); // Simulate processing time
            }

            log.info("[{}] Processing Row-{}: Completed. Sum = {}",
                    Thread.currentThread().getName(), rowNumber, sum);

            // Wait at barrier for all rows to complete
            log.info("[{}] Row-{} waiting at barrier (parties waiting: {})",
                    Thread.currentThread().getName(), rowNumber, barrier.getNumberWaiting() + 1);

            barrier.await();

            // Phase 2: After barrier - all rows are now processed
            log.info("[{}] Row-{}: Barrier crossed! All rows processed. Proceeding to next phase.",
                    Thread.currentThread().getName(), rowNumber);

        } catch (InterruptedException e) {
            log.error("[{}] Row-{} was interrupted", Thread.currentThread().getName(), rowNumber);
            Thread.currentThread().interrupt();
        } catch (BrokenBarrierException e) {
            log.error("[{}] Row-{} barrier was broken", Thread.currentThread().getName(), rowNumber);
        }
    }
}
