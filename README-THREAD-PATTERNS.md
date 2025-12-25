# Java Concurrency Patterns - Comprehensive Guide

## Table of Contents
1. [CountDownLatch](#1-countdownlatch)
2. [CyclicBarrier](#2-cyclicbarrier)
3. [Phaser](#3-phaser)
4. [Semaphore](#4-semaphore)
5. [Exchanger](#5-exchanger)
6. [ThreadLocal](#6-threadlocal)
7. [ReentrantLock](#7-reentrantlock)
8. [BlockingQueue](#8-blockingqueue)
9. [ForkJoinPool](#9-forkjoinpool)
10. [CompletableFuture](#10-completablefuture)
11. [Virtual Threads](#11-virtual-threads)
12. [Comparison Matrix](#comparison-matrix)

---

## 1. CountDownLatch

### Visual Representation
```
Main Thread               Worker Threads
    |                          |
    |--- Start Workers ------->|
    |                      Task1 --|
    |                      Task2 ----|
    |                      Task3 ------| (countDown)
    | await()              Task4 --------| (countDown)
    | (blocked)            Task5 ----------| (countDown)
    |                          |
    |<--- All Complete --------|
    | (Continue)
```

### Usage
- **Purpose**: Wait for multiple tasks to complete before proceeding
- **Real-world Example**: Batch job processing - wait for all data files to be processed before aggregating

### Advantages
- Simple one-time synchronization
- Thread-safe without explicit locking
- Efficient waiting mechanism
- Can check count without blocking

### Disadvantages
- Cannot be reset (one-time use)
- No barrier action support
- Fixed count at creation

### When to Use
- Waiting for N parallel operations to complete
- Starting multiple threads simultaneously (inverted use)
- One-time synchronization points

### Code Example
```java
CountDownLatch latch = new CountDownLatch(5);
for (int i = 0; i < 5; i++) {
    executor.submit(() -> {
        // Do work
        latch.countDown();
    });
}
latch.await(); // Wait for all 5 tasks
```

---

## 2. CyclicBarrier

### Visual Representation
```
Thread1    Thread2    Thread3
  |          |          |
  |-- Work --|-- Work --|-- Work
  |          |          |
  |------- Barrier ------|
  |    (Wait for all)    |
  |------- Action -------|
  |          |          |
  |-- Work --|-- Work --|-- Work
  |          |          |
  |------- Barrier ------|  (Reusable!)
```

### Usage
- **Purpose**: Synchronize threads at a common barrier point, with optional barrier action
- **Real-world Example**: Matrix processing - all rows must complete before column processing

### Advantages
- Reusable (cyclic)
- Barrier action executes when all parties arrive
- Automatic reset after barrier is tripped
- Knows number of waiting parties

### Disadvantages
- All parties must arrive (can't reduce count)
- Fixed party count
- Barrier action runs in last arriving thread

### When to Use
- Iterative parallel algorithms with synchronization points
- Multi-phase parallel computations
- When you need a barrier action between phases

### Code Example
```java
CyclicBarrier barrier = new CyclicBarrier(3, () -> {
    System.out.println("All threads reached barrier!");
});
for (int i = 0; i < 3; i++) {
    executor.submit(() -> {
        // Phase 1 work
        barrier.await();
        // Phase 2 work
        barrier.await(); // Can reuse!
    });
}
```

---

## 3. Phaser

### Visual Representation
```
Party Registration: Dynamic
Phase 0:  P1 --|-- P2 --|-- P3 --|-- (All arrived, advance)
Phase 1:  P1 --|-- P2 --|-- P3 --|-- P4 (new)--|
Phase 2:  P1 --|-- P2 --|----------| (P3,P4 deregistered)
```

### Usage
- **Purpose**: Multi-phase synchronization with dynamic party registration
- **Real-world Example**: Multi-round game where players can join/leave

### Advantages
- Dynamic party registration/deregistration
- Multiple phases support
- onAdvance() callback for phase completion
- Can terminate phases programmatically
- More flexible than CyclicBarrier

### Disadvantages
- More complex API
- Higher overhead than simpler synchronizers
- Can be harder to debug

### When to Use
- Multi-phase algorithms with dynamic participants
- When parties need to join/leave during execution
- Need fine control over phase advancement

### Code Example
```java
Phaser phaser = new Phaser(1); // Main thread registered
for (int i = 0; i < 3; i++) {
    phaser.register(); // Register each party
    executor.submit(() -> {
        // Phase 1
        phaser.arriveAndAwaitAdvance();
        // Phase 2
        phaser.arriveAndAwaitAdvance();
        phaser.arriveAndDeregister(); // Leave
    });
}
phaser.arriveAndDeregister(); // Main thread done
```

---

## 4. Semaphore

### Visual Representation
```
Semaphore(3)  [permits available: 3]

Thread1: acquire() -> [2] -> use resource -> release() -> [3]
Thread2: acquire() -> [2] -> use resource
Thread3: acquire() -> [1] -> use resource
Thread4: acquire() -> [0] -> use resource
Thread5: acquire() -> BLOCKED (waiting)
                         |
Thread2: release() -> [1] -> Thread5 unblocked!
```

### Usage
- **Purpose**: Limit concurrent access to a resource
- **Real-world Example**: Connection pool, ATM machines with limited terminals

### Advantages
- Controls concurrent access
- Fairness option prevents starvation
- tryAcquire() for non-blocking attempts
- Can acquire/release multiple permits

### Disadvantages
- No ownership concept (any thread can release)
- Easy to forget release (use try-finally)
- Not reentrant

### When to Use
- Rate limiting
- Resource pools with limited capacity
- Throttling concurrent operations

### Code Example
```java
Semaphore semaphore = new Semaphore(3, true); // 3 permits, fair
semaphore.acquire();
try {
    // Access limited resource
} finally {
    semaphore.release();
}
```

---

## 5. Exchanger

### Visual Representation
```
Thread A                    Thread B
   |                           |
   |----- Data A ----->  Exchange Point
   |                           |
   |<----- Data B -------------|
   |                           |
```

### Usage
- **Purpose**: Exchange data between two threads
- **Real-world Example**: Trading system, genetic algorithms (crossover)

### Advantages
- Type-safe bidirectional exchange
- Built-in synchronization
- Timeout support
- Simple API for two-party exchange

### Disadvantages
- Only works with exactly 2 threads
- Both threads must arrive (blocking)
- Not suitable for more than 2 parties

### When to Use
- Pipeline stages exchanging data
- Genetic algorithm crossover
- Producer-consumer with full/empty buffer swap

### Code Example
```java
Exchanger<String> exchanger = new Exchanger<>();

// Thread A
String dataFromB = exchanger.exchange("Data from A");

// Thread B
String dataFromA = exchanger.exchange("Data from B");
```

---

## 6. ThreadLocal

### Visual Representation
```
ThreadLocal<Context>

Thread-1: [Context-1] ---> Isolated storage
Thread-2: [Context-2] ---> Isolated storage
Thread-3: [Context-3] ---> Isolated storage

Each thread sees only its own copy
```

### Usage
- **Purpose**: Per-thread isolated storage
- **Real-world Example**: Web request context (user session, transaction ID)

### Advantages
- No synchronization needed
- Thread isolation
- Simplifies passing context down call stack
- Better than passing parameters everywhere

### Disadvantages
- Memory leaks if not cleaned up
- Incompatible with thread pools (stale data)
- Harder to test
- Can hide dependencies

### When to Use
- Request-scoped data in web applications
- Thread-specific formatting (DateFormat)
- Security context propagation

### Code Example
```java
ThreadLocal<RequestContext> context = new ThreadLocal<>();

// Set for current thread
context.set(new RequestContext("user123"));

// Get in any method in call stack
RequestContext ctx = context.get();

// MUST clean up
context.remove();
```

---

## 7. ReentrantLock

### Visual Representation
```
Lock (fair/unfair mode)

Thread1: lock() --> [LOCKED] --> critical section --> unlock()
Thread2: lock() --> [WAITING]
Thread3: lock() --> [WAITING]
                       |
Thread1: unlock() --> Thread2 acquires (fair: longest waiting)
```

### Usage
- **Purpose**: Explicit locking with advanced features
- **Real-world Example**: Bank account operations requiring transaction atomicity

### Advantages
- Fairness option
- tryLock() non-blocking attempt
- Interruptible locking
- Lock condition variables
- Can query lock state
- Reentrant (same thread can acquire multiple times)

### Disadvantages
- Manual unlock required (easy to forget)
- More verbose than synchronized
- Must use try-finally pattern
- No automatic release

### When to Use
- Need fairness guarantee
- Need try-lock or timed locking
- Complex synchronization patterns
- Hand-over-hand locking

### Code Example
```java
ReentrantLock lock = new ReentrantLock(true); // fair mode

lock.lock();
try {
    // Critical section
} finally {
    lock.unlock(); // MUST unlock
}

// Try-lock variant
if (lock.tryLock(1, TimeUnit.SECONDS)) {
    try {
        // Got lock
    } finally {
        lock.unlock();
    }
}
```

---

## 8. BlockingQueue

### Visual Representation
```
Producer-Consumer Pattern

Producers              Queue [capacity: 5]        Consumer
   P1 --put()-->  [item1|item2|item3| | ] --take()--> C
   P2 --put()-->  (blocks if full)            (blocks if empty)
   P3 --put()-->
```

### Usage
- **Purpose**: Thread-safe queue with blocking operations
- **Real-world Example**: Producer-consumer log writer, task queue

### Advantages
- Thread-safe without explicit synchronization
- Blocks on empty (take) or full (put)
- Multiple implementations (Array, Linked, Priority)
- Built-in backpressure handling
- Poison pill pattern for shutdown

### Disadvantages
- Bounded queues can deadlock if misused
- May need separate shutdown mechanism
- Can waste memory with large capacity

### When to Use
- Producer-consumer patterns
- Work queues
- Event processing pipelines
- Buffering between fast/slow components

### Code Example
```java
BlockingQueue<Task> queue = new ArrayBlockingQueue<>(100);

// Producer
queue.put(task); // Blocks if full

// Consumer
Task task = queue.take(); // Blocks if empty

// Shutdown with poison pill
queue.put(POISON_PILL);
```

---

## 9. ForkJoinPool

### Visual Representation
```
Divide-and-Conquer with Work Stealing

Task [0-1000]
    |
    |--- Fork
    |         \
Task[0-500]   Task[500-1000]
    |              |
    |--- Fork      |--- Fork
    |       \      |        \
[0-250] [250-500] [500-750] [750-1000]
    |       |        |          |
  Compute Compute  Compute    Compute
    |       |        |          |
    |--- Join -------|          |
    |                           |
    |-------- Join -------------|
```

### Usage
- **Purpose**: Parallel divide-and-conquer algorithms with work stealing
- **Real-world Example**: Image processing, parallel sorting, tree traversal

### Advantages
- Work stealing for load balancing
- Optimized for recursive algorithms
- Efficient thread utilization
- Built-in thread pool management
- Good for CPU-intensive tasks

### Disadvantages
- Overhead for small tasks
- Not ideal for I/O-bound work
- More complex than simple thread pools
- Requires understanding of fork/join pattern

### When to Use
- Divide-and-conquer algorithms
- Recursive parallel processing
- Large array/collection operations
- Tree structure processing

### Code Example
```java
class Task extends RecursiveTask<Integer> {
    protected Integer compute() {
        if (small enough) {
            return computeDirectly();
        }
        Task left = new Task(leftHalf);
        left.fork(); // Async
        Task right = new Task(rightHalf);
        int rightResult = right.compute();
        int leftResult = left.join(); // Wait
        return combine(leftResult, rightResult);
    }
}

ForkJoinPool pool = ForkJoinPool.commonPool();
int result = pool.invoke(new Task(data));
```

---

## 10. CompletableFuture

### Visual Representation
```
Async Pipeline with Error Handling

supplyAsync()
      |
      v
thenApply() ---(transform)----> thenCompose()
      |                              |
      |                         (chain future)
      v                              v
thenCombine() <----(combine)---- otherFuture
      |
      v
exceptionally() (error recovery)
      |
      v
   Result
```

### Usage
- **Purpose**: Composable asynchronous programming
- **Real-world Example**: Chained API calls (profile -> orders -> recommendations)

### Advantages
- Composable async operations
- Built-in error handling
- Combine multiple futures
- Non-blocking
- Rich API (thenApply, thenCompose, etc.)
- Can specify executor

### Disadvantages
- Complex error propagation
- Easy to create callback hell
- Debugging can be difficult
- Exception handling tricky

### When to Use
- Async I/O operations
- API call chains
- Independent parallel operations
- Non-blocking workflows

### Code Example
```java
CompletableFuture<User> userFuture =
    CompletableFuture.supplyAsync(() -> fetchUser(id))
        .thenCompose(user -> fetchOrders(user.getId()))
        .thenApply(orders -> processOrders(orders))
        .exceptionally(ex -> defaultValue)
        .handle((result, ex) -> {
            if (ex != null) return fallback;
            return result;
        });

// Combine multiple
CompletableFuture<Combined> combined =
    future1.thenCombine(future2, (r1, r2) -> merge(r1, r2));
```

---

## 11. Virtual Threads

### Visual Representation
```
Platform Threads vs Virtual Threads

Platform Threads (1:1 mapping):
App Thread 1 ----> OS Thread 1
App Thread 2 ----> OS Thread 2
...
App Thread 1000 -> OS Thread 1000 (EXPENSIVE!)

Virtual Threads (M:N mapping):
VThread 1 ---|
VThread 2 ---|
...          |---> Platform Thread 1
VThread 999--|     Platform Thread 2
VThread 1000-|     ...
(Millions OK!)    Platform Thread N (few)
```

### Usage
- **Purpose**: Lightweight threads for high-throughput I/O-bound workloads
- **Real-world Example**: Web server handling thousands of concurrent requests

### Advantages
- Extremely lightweight (millions possible)
- Cheap creation and context switching
- Same Thread API
- Perfect for I/O-bound tasks
- Simplified async programming
- No callback hell

### Disadvantages
- Not for CPU-intensive tasks
- Pinning issues (synchronized blocks)
- Requires Java 21+
- Not all libraries optimized yet
- Debugging tools still evolving

### When to Use
- High-concurrency web servers
- Massive I/O operations
- Replacing async frameworks
- Database connection per request
- Microservices communication

### Code Example
```java
// Single virtual thread
Thread vThread = Thread.ofVirtual().start(() -> {
    // I/O-bound work
});

// Executor with virtual threads
ExecutorService executor =
    Executors.newVirtualThreadPerTaskExecutor();

for (int i = 0; i < 100_000; i++) {
    executor.submit(() -> handleRequest());
}
```

---

## Comparison Matrix

| Pattern | Reusable | Dynamic Parties | Error Handling | Best For | Complexity |
|---------|----------|-----------------|----------------|----------|------------|
| **CountDownLatch** | ❌ | ❌ | ⚠️ Manual | One-time sync | Low |
| **CyclicBarrier** | ✅ | ❌ | ⚠️ Manual | Iterative phases | Low |
| **Phaser** | ✅ | ✅ | ⚠️ Manual | Dynamic multi-phase | Medium |
| **Semaphore** | ✅ | N/A | ⚠️ Manual | Resource limiting | Low |
| **Exchanger** | ✅ | ❌ (2 only) | ⚠️ Manual | Two-party exchange | Low |
| **ThreadLocal** | ✅ | N/A | N/A | Thread isolation | Low |
| **ReentrantLock** | ✅ | N/A | ⚠️ Manual | Fine-grained locking | Medium |
| **BlockingQueue** | ✅ | N/A | ⚠️ Manual | Producer-consumer | Low |
| **ForkJoinPool** | ✅ | N/A | ⚠️ Manual | Divide-conquer | High |
| **CompletableFuture** | ✅ | N/A | ✅ Built-in | Async chains | Medium |
| **Virtual Threads** | ✅ | N/A | ⚠️ Manual | High I/O concurrency | Low |

## Performance Characteristics

| Pattern | Overhead | Scalability | Thread Usage | Memory |
|---------|----------|-------------|--------------|--------|
| CountDownLatch | Very Low | Excellent | Efficient | Minimal |
| CyclicBarrier | Low | Good | Efficient | Low |
| Phaser | Medium | Good | Efficient | Medium |
| Semaphore | Very Low | Excellent | Efficient | Minimal |
| Exchanger | Low | Limited (2) | Efficient | Low |
| ThreadLocal | Low | Per-thread | N/A | Per-thread |
| ReentrantLock | Low | Good | Efficient | Low |
| BlockingQueue | Low | Excellent | Efficient | Based on capacity |
| ForkJoinPool | Medium | Excellent | Work-stealing | Medium |
| CompletableFuture | Medium | Good | Async | Medium |
| Virtual Threads | Very Low | Exceptional | Massive | Very Low |

## Use Case Decision Tree

```
Need coordination? ---NO---> Need isolation? ---YES---> ThreadLocal
      |                              |
     YES                            NO
      |                              |
Need to wait for N tasks?          Need async?
      |                              |
     YES ----> CountDownLatch       YES ----> CompletableFuture
      |                              |
     NO                             NO
      |                              |
Multiple phases?                 Need locking?
      |                              |
     YES -------> Phaser             YES ----> ReentrantLock
      |                              |
     NO                             NO
      |                              |
Limit resources? --> Semaphore    CPU-intensive parallel?
      |                              |
     NO                             YES --> ForkJoinPool
      |                              |
Two-party exchange?                NO
      |                              |
     YES --> Exchanger           I/O-bound?
      |                              |
     NO                             YES --> Virtual Threads
      |                              |
Producer-Consumer? --> BlockingQueue
```

## Best Practices

### General
1. **Always clean up**: Use try-finally for locks and resources
2. **Avoid deadlocks**: Acquire locks in consistent order
3. **Minimize critical sections**: Hold locks for shortest time
4. **Use appropriate tool**: Don't over-engineer simple problems

### ThreadLocal
```java
// ✅ Good
try {
    context.set(value);
    doWork();
} finally {
    context.remove(); // Clean up!
}

// ❌ Bad
context.set(value);
doWork();
// Forgot to remove - memory leak in thread pools!
```

### Locks
```java
// ✅ Good
lock.lock();
try {
    // Critical section
} finally {
    lock.unlock(); // Always in finally
}

// ❌ Bad
lock.lock();
// Critical section
lock.unlock(); // Exception = deadlock!
```

### CompletableFuture
```java
// ✅ Good - handle errors
future.exceptionally(ex -> fallback)
      .thenApply(result -> process(result));

// ❌ Bad - no error handling
future.thenApply(result -> process(result));
// Exception = silent failure
```

## Common Pitfalls

1. **Forgetting to release**: Locks, semaphores → deadlocks
2. **ThreadLocal leaks**: Not removing in thread pools
3. **Wrong synchronizer**: Using CyclicBarrier when CountDownLatch would suffice
4. **Blocking virtual threads**: Using synchronized blocks (pinning)
5. **ForkJoin for I/O**: Should use Virtual Threads instead

## Migration Guide

### From synchronized to ReentrantLock
```java
// Before
synchronized(this) {
    // critical section
}

// After
lock.lock();
try {
    // critical section
} finally {
    lock.unlock();
}
```

### From Thread to Virtual Threads
```java
// Before
for (int i = 0; i < 10000; i++) {
    new Thread(() -> handleRequest()).start(); // BAD!
}

// After
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 10000; i++) {
        executor.submit(() -> handleRequest()); // GOOD!
    }
}
```

---

## Running the Demos

Each module includes:
- Working implementation with real-world examples
- Comprehensive logging showing thread behavior
- Unit tests to validate functionality

To run all demos:
```bash
mvn clean install
mvn spring-boot:run
```

To run individual tests:
```bash
mvn test -Dtest=CountDownLatchDemoTest
mvn test -Dtest=VirtualThreadsDemoTest
```

---

## Further Reading

- [Java Concurrency in Practice](https://jcip.net/)
- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [java.util.concurrent JavaDoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/package-summary.html)

---

**Project**: Java 21 Concurrency Patterns
**Spring Boot**: 4.0.1
**Java Version**: 21 LTS
