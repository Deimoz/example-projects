package info.kgeorgiy.ja.vircev.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class IterativeParallelism implements ScalarIP {
    private ParallelMapper mapper = null;

    public IterativeParallelism() {
    }

    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return completeTasks(threads, values, vals -> vals.max(comparator).get(), vals -> vals.max(comparator).get());
    }

    // :NOTE: code duplication
    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return maximum(threads, values, Collections.reverseOrder(comparator));
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return completeTasks(threads, values, vals -> vals.allMatch(predicate), vals -> vals.allMatch(elem -> elem == true));
    }

    // same here, duplication
    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return !all(threads, values, predicate.negate());
    }

    private <T, E> E completeTasks(int threads, List<? extends T> values, Function<Stream<? extends T>, ? extends E> function,
                                   Function<Stream<? extends E>, ? extends E> collector) throws InterruptedException {
        if (values == null) {
            throw new IllegalArgumentException("List of values are null");
        }
        List<E> result = parallelism(threads, values, function);
        return applyResult(result, collector);
    }

    private <T, E> List<E> parallelism(int threads, List<? extends T> values, Function<Stream<? extends T>, ? extends E> function) throws InterruptedException {
        if (values.isEmpty()) {
            return List.of();
        } else if (values.size() < threads) {
            threads = values.size();
        }
        int sizeOfSubtask = values.size() / threads;
        int sizeOfTail = values.size() % threads;
        List<Stream<? extends T>> tasks = new ArrayList<>();
        int l = 0;
        for (int i = 0; i < threads; i++) {
            int r = l + sizeOfSubtask;
            if (sizeOfTail > 0) {
                r++;
                sizeOfTail--;
            }
            int finalR = r;
            int finalL = l;
            tasks.add(values.subList(finalL, finalR).stream());
            l = r;
        }
        if (mapper != null) {
            return mapper.map(function, tasks);
        }
        List<E> res = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            res.add(null);
        }
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            int finalI = i;
            threadList.add(new Thread(() -> res.set(finalI, function.apply(tasks.get(finalI)))));
            threadList.get(i).start();
        }
        for (int i = 0; i < threads; i++) {
            // :NOTE-2: all threads should be joined before the function ends
            try {
                threadList.get(i).join();
            } catch (InterruptedException ignored) {}
        }
        return res;
    }

    private <T> T applyResult(List<? extends T> values, Function<Stream<? extends T>, ? extends T> collector) {
        if (values.isEmpty()) {
            return null;
        }
        return collector.apply(values.stream());
    }
}
