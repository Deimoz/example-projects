package linked_list_set;

import kotlinx.atomicfu.AtomicRef;

public class SetImpl implements Set {
    private class Node {
        AtomicRef<Pair> currPair;
        int x;

        Node(int x, Pair currPair) {
            this.currPair = new AtomicRef<>(currPair);
            this.x = x;
        }
    }

    class Pair {
        public Node next;
        public boolean removed;

        public Pair(Node next, boolean removed) {
            this.next = next;
            this.removed = removed;
        }
    }

    private class Window {
        Node cur, next;
    }

    private final AtomicRef<Node> head
            = new AtomicRef<>(new Node(Integer.MIN_VALUE,
                new Pair(new Node(Integer.MAX_VALUE,  new Pair(null, false)), false)));

    /**
     * Returns the {@link Window}, where cur.x < x <= next.x
     */
    private Window findWindow(int x) {
        while (true) {
            Window w = new Window();
            w.cur = head.getValue();
            w.next = w.cur.currPair.getValue().next;
            boolean removed = w.cur.currPair.getValue().removed;
            while (w.next.x < x) {
                if (w.cur.currPair.getValue().removed) {
                    break;
                }
                Pair nextP = w.next.currPair.getValue();
                if (removed) {
                    if (!w.cur.currPair.compareAndSet(nextP, new Pair(nextP.next, false))) {
                        break;
                    }
                    w.next = nextP.next;
                } else {
                    w.cur = w.next;
                    w.next = w.cur.currPair.getValue().next;
                }
            }
            return w;
        }
    }

    @Override
    public boolean add(int x) {
        while (true) {
            Window w = findWindow(x);
            Pair currPair = w.cur.currPair.getValue();
            if (w.next.x == x) {
                return false;
            }
            Pair nextP = new Pair(w.next, false);
            Node nextNode = new Node(x, nextP);
            if (!currPair.removed && w.cur.currPair.compareAndSet(currPair, new Pair(nextNode, false))) {
                return true;
            }
        }
    }

    @Override
    public boolean remove(int x) {
        while (true) {
            Window w = findWindow(x);
            if (w.next.x != x) {
                return false;
            }
            Pair currP = w.cur.currPair.getValue();
            Node next = currP.next;
            Pair nextP = w.next.currPair.getValue();
            if (!nextP.removed &&
                    next.currPair.compareAndSet(nextP, new Pair(w.next.currPair.getValue().next, true))) {
                w.cur.currPair.compareAndSet(currP, new Pair(next, false));
                return true;
            }
        }
    }

    @Override
    public boolean contains(int x) {
        Window w = findWindow(x);
        return w.next.x == x;
    }
}