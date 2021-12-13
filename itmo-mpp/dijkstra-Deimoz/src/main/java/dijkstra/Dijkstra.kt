package dijkstra

import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import java.util.*
import java.util.concurrent.Phaser
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.Comparator
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

private val NODE_DISTANCE_COMPARATOR = Comparator<Node> { o1, o2 -> Integer.compare(o1!!.distance, o2!!.distance) }

// Returns `Integer.MAX_VALUE` if a path has not been found.
fun shortestPathParallel(start: Node) {
    val workers = Runtime.getRuntime().availableProcessors()
    // The distance to the start node is `0`
    start.distance = 0
    // Create a priority (by distance) queue and add the start node into it
    val q = MultiQueue(workers)
    q.enqueue(start)
    val active = AtomicInteger(1)
    // Run worker threads and wait until the total work is done
    val onFinish = Phaser(workers + 1) // `arrive()` should be invoked at the end by each worker
    repeat(workers) {
        thread {
            while (active.get() > 0) {
                val u = q.dequeue() ?: continue
                for (edge in u.outgoingEdges) {
                    while (true) {
                        val currDist = edge.to.distance
                        val update = u.distance + edge.weight
                        if (currDist > update) {
                            if (edge.to.casDistance(currDist, update)) {
                                q.enqueue(edge.to)
                                active.incrementAndGet()
                                break
                            }
                            continue
                        }
                        break
                    }
                }
                active.decrementAndGet()
            }
            onFinish.arrive()
        }
    }
    onFinish.arriveAndAwaitAdvance()
}

class MultiQueue(threads: Int) {
    private val numOfQueues: Int = threads * 2
    private val queues = Array(numOfQueues) {
        LocalPriorityQueue()
    }

    fun enqueue(elem: Node) {
        val currIndex = Random().nextInt(numOfQueues)
        val currQueue = queues[currIndex]
        currQueue.lock.withLock {
            currQueue.queue.add(elem)
        }

    }

    fun dequeue(): Node? {
        var index1 = Random().nextInt(numOfQueues)
        var index2 = Random().nextInt(numOfQueues)
        while (index1 == index2) {
            index2 = Random().nextInt(numOfQueues)
        }
        if (index1 > index2) {
            val temp = index1
            index1 = index2
            index2 = temp
        }
        val queue1 = queues[index1]
        val queue2 = queues[index2]
        queue1.lock.withLock {
            queue2.lock.withLock {
                val elem1 = queue1.queue.peek()
                val elem2 = queue2.queue.peek()
                when {
                    elem1 == null && elem2 == null -> return null
                    elem1 == null -> return queue2.queue.poll()
                    elem2 == null -> return queue1.queue.poll()
                    else -> {
                        if (NODE_DISTANCE_COMPARATOR.compare(elem1, elem2) > 0) {
                            return queue2.queue.poll()
                        }
                        return queue1.queue.poll()
                    }
                }
            }
        }
    }

    class LocalPriorityQueue {
        val queue: PriorityQueue<Node> = PriorityQueue(NODE_DISTANCE_COMPARATOR)
        val lock: ReentrantLock = ReentrantLock()
    }
}