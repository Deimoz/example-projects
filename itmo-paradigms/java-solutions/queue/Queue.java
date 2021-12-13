package queue;

public interface Queue {

    // inv: size >= 0 && a[i] != null for i = first, ... , last - 1
    // inv - contained in all function

    // Pre: element != null
    // Post: size' == size + 1 && queue[i] == queue[i]' for i == first', ... , last && queue[last + 1] == element
    void enqueue(Object element);

    // Pre: size > 0
    // Post: res == queue[first] && size' == size - 1 && queue[i] == queue[i]' for i == first', ... , last - 1
    Object dequeue();

    // Pre: size > 0
    // Post: res == queue[first] && queue - immutable
    Object element();

    // Post: res == size && queue - immutable
    int size();

    // Post: res == (size == 0) && queue - immutable
    boolean isEmpty();

    // Post: size == 0 && queue == []
    void clear();

    // Post: res == String("[queue[first]], ... , [queue[last]]") && queue - immutable
    Object[] toArray();
}
