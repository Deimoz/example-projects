package queue;

// :NOTE: test class in sources
public class LinkedQueueTest {
    public static void fill(Queue queue) {
        for (int i = 0; i < 10; i++) {
            queue.enqueue(i);
        }
    }

    public static void dump(Queue queue) {
        while (!queue.isEmpty()) {
            System.out.println(queue.size() + " " +
                    queue.element() + " " + queue.dequeue());
        }
    }

    public static void main(String[] args) {
        Queue queue = new ArrayQueue();
        System.out.println(queue.size());
        queue.enqueue(99);
        System.out.println(queue.size());
        fill(queue);
        dump(queue);
    }
}
