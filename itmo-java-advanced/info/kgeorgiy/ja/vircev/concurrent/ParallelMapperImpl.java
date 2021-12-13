package info.kgeorgiy.ja.vircev.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {
    private final Queue<Runnable> tasks;
    private final List<Thread> threads;

    public ParallelMapperImpl(int numOfThreads) {
        tasks = new ArrayDeque<>();
        threads = new ArrayList<>();
        Runnable threadTask = () -> {
            while (!Thread.interrupted()) {
                Runnable newTask;
                synchronized (tasks) {
                    try {
                        newTask = takeTask();
                    } catch (InterruptedException e) {
                        break;
                    }
                    // :NOTE: notifyAll is more expensive, notify() is just fine here
                    // :NOTE-2: actually not needed at all
                }
                newTask.run();
            }
        };
        for (int i = 0; i < numOfThreads; i++) {
            // :NOTE-2: you create n runnables, while you can create just one
            threads.add(new Thread(threadTask));
            threads.get(i).start();
        }
    }


    private Runnable takeTask() throws InterruptedException {
        while (tasks.isEmpty()) {
            tasks.wait();
        }
        return tasks.poll();
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        List<R> result = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            result.add(null);
        }
        // :NOTE: no concurrent utilities
        ThreadInt done = new ThreadInt(0);
        for (int i = 0; i < args.size(); i++) {
            synchronized (tasks) {
                int finalI = i;
                tasks.add(() -> {
                    result.set(finalI, f.apply(args.get(finalI)));
                    synchronized (done) {
                        done.increment();
                        // :NOTE: too many notifies
                        if (done.get() >= args.size()) {
                            done.notify();
                        }
                    }
                });
                tasks.notify();
            }
        }
        synchronized (done) {
            while (done.get() < args.size()) {
                done.wait();
            }
        }
        return result;
    }

    @Override
    public void close() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {}
        }
    }

    private static class ThreadInt {
        private int x;

        public ThreadInt(int x) {
            this.x = x;
        }

        public int get() {
            return x;
        }

        public void increment() {
            x++;
        }
    }
}
