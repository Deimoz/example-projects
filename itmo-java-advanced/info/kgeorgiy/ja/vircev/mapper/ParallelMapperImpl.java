package info.kgeorgiy.ja.vircev.mapper;

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
        for (int i = 0; i < numOfThreads; i++) {
            threads.add(new Thread(() -> {
                while (!Thread.interrupted()) {
                    Runnable newTask;
                    synchronized (tasks) {
                        try {
                            newTask = takeTask();
                        } catch (InterruptedException e) {
                            break;
                        }
                        tasks.notifyAll();
                    }
                    newTask.run();
                }
            }));
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
        AtomicInteger done = new AtomicInteger(0);
        for (int i = 0; i < args.size(); i++) {
            synchronized (tasks) {
                int finalI = i;
                tasks.add(() -> {
                    result.set(finalI, f.apply(args.get(finalI)));
                    synchronized (done) {
                        done.incrementAndGet();
                        done.notify();
                    }
                });
                tasks.notifyAll();
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
    }
}
