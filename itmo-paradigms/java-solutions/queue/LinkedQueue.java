package queue;

public class LinkedQueue extends AbstractQueue {
    private Node head, tail;

    @Override
    public void localEnqueue(Object element) {
        if (size == 0) {
            tail = new Node(0, null);
            Node temp = new Node(element, tail);
            tail.next = temp;
            head = new Node(0, temp);
        } else {
            Node temp = new Node(element, tail);
            tail.next.next = temp;
            tail.next = temp;
        }
    }

    @Override
    public Object localDequeue() {
        Object result = head.next.value;
        head = head.next;
        return result;
    }

    @Override
    public Object localElement() {
        return head.next.value;
    }

    @Override
    public void localClear() {
        head = null; tail = null;
    }

    @Override
    public Object[] localToArray() {
        Object[] temp = new Object[size];
        Node pointer = new Node(0, head.next);
        for (int i = 0; i < size; i++) {
            temp[i] = pointer.next.value;
            pointer = pointer.next;
        }
        return temp;
    }

    private class Node {
        // :NOTE: are references mutable? where is `final` modifier?
        private Object value;
        private Node next;

        public Node(Object value, Node next) {
            assert value != null;

            this.value = value;
            this.next = next;
        }
    }
}
