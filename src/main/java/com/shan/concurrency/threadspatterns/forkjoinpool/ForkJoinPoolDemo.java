package com.shan.concurrency.threadspatterns.forkjoinpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ForkJoinPool;

/**
 * ForkJoinPool Demo - Parallel Image Processing
 *
 * Use Case: Divide-and-conquer parallel processing
 * Real-world Example: Image filtering with recursive pixel processing
 *
 * How it works:
 * 1. Create ForkJoinPool (uses common pool or custom)
 * 2. Extend RecursiveTask<T> (returns result) or RecursiveAction (void)
 * 3. Implement compute(): if small enough, process directly; else fork/join
 * 4. fork() - execute subtask asynchronously
 * 5. join() - wait for subtask result
 * 6. Optimized for recursive divide-and-conquer algorithms
 * 7. Uses work-stealing for load balancing
 */
@Slf4j
@Component
public class ForkJoinPoolDemo {

    private static final int IMAGE_SIZE = 5000; // 5000 pixels

    public void demonstrate() {
        log.info("=== ForkJoinPool Demo: Parallel Image Processing ===");
        log.info("Scenario: Processing {}-pixel image with brightness filter", IMAGE_SIZE);

        // Step 1: Generate sample image data
        int[] imagePixels = generateImageData(IMAGE_SIZE);
        log.info("Generated image with {} pixels", imagePixels.length);

        // Step 2: Get ForkJoinPool (using common pool)
        ForkJoinPool pool = ForkJoinPool.commonPool();
        log.info("Using ForkJoinPool with parallelism level: {}", pool.getParallelism());

        // Step 3: Create main task
        ImageProcessor task = new ImageProcessor(imagePixels, 0, imagePixels.length);

        // Step 4: Execute and get result
        log.info("[{}] Starting image processing...", Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        int[] processedPixels = pool.invoke(task);

        long endTime = System.currentTimeMillis();

        // Step 5: Report results
        log.info("[{}] Image processing completed!", Thread.currentThread().getName());
        log.info("Processed {} pixels in {} ms", processedPixels.length, (endTime - startTime));
        log.info("Pool stats - Active threads: {}, Steal count: {}, Queued tasks: {}",
                pool.getActiveThreadCount(), pool.getStealCount(), pool.getQueuedTaskCount());

        log.info("=== ForkJoinPool Demo Completed ===");
    }

    private int[] generateImageData(int size) {
        int[] pixels = new int[size];
        for (int i = 0; i < size; i++) {
            // Generate random RGB values
            int r = (int)(Math.random() * 200);
            int g = (int)(Math.random() * 200);
            int b = (int)(Math.random() * 200);
            pixels[i] = (r << 16) | (g << 8) | b;
        }
        return pixels;
    }
}
