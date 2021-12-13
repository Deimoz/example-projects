package queue;

public class ArrayQueue extends AbstractQueue {
    private int first;
    private Object[] elements = new Object[5];

    private int last() {
        return (size + first) % elements.length;
    }

    @Override
    public void localEnqueue(Object element) {
        ensureCapacity(size + 1);
        elements[last()] = element;
    }

    @Override
    public Object localDequeue() {
        Object value = elements[first++];
        first %= elements.length;
        return value;
    }

    @Override
    public Object localElement() {
        return elements[first];
    }

    @Override
    public void localClear() {
        elements = new Object[5];
        first = 0;
    }

    @Override
    public Object[] localToArray() {
        Object[] temp = new Object[size];
        // :NOTE: by-hand copy of array?
        for (int i = first; i < size + first; i++) {
            temp[i - first] = elements[i % elements.length];
        }
        return temp;
    }

    private void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            Object[] copyElements = new Object[2 * capacity];
            System.arraycopy(elements, first, copyElements, 0, elements.length - last());
            System.arraycopy(elements, 0, copyElements, elements.length - last(), last());
            elements = copyElements;
            first = 0;
        }
    }
}