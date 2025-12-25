package com.shan.concurrency.threadspatterns.forkjoinpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RecursiveTask;

/**
 * ImageProcessor uses Fork/Join framework to process image pixels in parallel.
 * Real-world example: Image filtering/transformation using divide-and-conquer.
 *
 * RecursiveTask<T> returns a result (use RecursiveAction for void tasks)
 */
@Slf4j
public class ImageProcessor extends RecursiveTask<int[]> {

    private final int[] pixels;
    private final int start;
    private final int end;
    private static final int THRESHOLD = 1000; // Process directly if less than threshold

    public ImageProcessor(int[] pixels, int start, int end) {
        this.pixels = pixels;
        this.start = start;
        this.end = end;
    }

    @Override
    protected int[] compute() {
        int length = end - start;

        log.info("[{}] Processing pixels [{} to {}] (length: {})",
                Thread.currentThread().getName(), start, end - 1, length);

        // Base case: Small enough to process directly
        if (length <= THRESHOLD) {
            return processDirectly();
        }

        // Recursive case: Split into subtasks
        int mid = start + length / 2;

        log.info("[{}] Forking task: splitting [{} to {}] into [{} to {}] and [{} to {}]",
                Thread.currentThread().getName(), start, end - 1,
                start, mid - 1, mid, end - 1);

        // Fork left subtask
        ImageProcessor leftTask = new ImageProcessor(pixels, start, mid);
        leftTask.fork(); // Asynchronously execute in another thread

        // Process right subtask in current thread
        ImageProcessor rightTask = new ImageProcessor(pixels, mid, end);
        int[] rightResult = rightTask.compute();

        // Join left subtask (wait for completion)
        int[] leftResult = leftTask.join();

        log.info("[{}] Joining results from [{} to {}] and [{} to {}]",
                Thread.currentThread().getName(), start, mid - 1, mid, end - 1);

        // Combine results
        return mergeResults(leftResult, rightResult);
    }

    private int[] processDirectly() {
        log.info("[{}] Processing directly: {} pixels",
                Thread.currentThread().getName(), end - start);

        int[] result = new int[end - start];

        for (int i = start; i < end; i++) {
            // Apply filter: brightness increase by 20%
            int pixel = pixels[i];
            int r = (pixel >> 16) & 0xFF;
            int g = (pixel >> 8) & 0xFF;
            int b = pixel & 0xFF;

            // Increase brightness (clamp to 255)
            r = Math.min(255, (int)(r * 1.2));
            g = Math.min(255, (int)(g * 1.2));
            b = Math.min(255, (int)(b * 1.2));

            result[i - start] = (r << 16) | (g << 8) | b;
        }

        log.info("[{}] Completed direct processing of {} pixels",
                Thread.currentThread().getName(), end - start);

        return result;
    }

    private int[] mergeResults(int[] left, int[] right) {
        int[] merged = new int[left.length + right.length];
        System.arraycopy(left, 0, merged, 0, left.length);
        System.arraycopy(right, 0, merged, left.length, right.length);
        return merged;
    }
}
