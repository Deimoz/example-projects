package queue;

public abstract class AbstractQueue implements Queue {
    protected int size = 0;

    @Override
    public void enqueue(Object element) {
        assert element != null;

        localEnqueue(element);
        size++;
    }

    protected abstract void localEnqueue(Object element);

    @Override
    public Object dequeue() {
        assert size > 0;

        Object result = localDequeue();
        size--;
        return result;
    }

    protected abstract Object localDequeue();

    @Override
    public Object element() {
        assert size > 0;

        return localElement();
    }

    protected abstract Object localElement();

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear(){
        size = 0;
        localClear();
    }

    protected abstract void localClear();

    @Override
    public Object[] toArray() {
        return localToArray();
    }

    // :NOTE: for what this method?
    protected abstract Object[] localToArray();
}
