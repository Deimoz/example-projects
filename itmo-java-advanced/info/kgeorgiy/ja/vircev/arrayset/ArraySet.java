package info.kgeorgiy.ja.vircev.arrayset;

import java.util.*;

public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {
    private final ArrayList<T> elements;
    private final Comparator<? super T> comparator;

    public ArraySet() {
        this(new ArrayList<>(), null);
    }

    public ArraySet(Comparator<? super T> comparator) {
        this(new ArrayList<>(), comparator);
    }

    // NOTE: please, re-use constructors
    public ArraySet(Collection<T> elements) {
        this(elements, null);
    }

    public ArraySet(Collection<T> elements, Comparator<? super T> comparator) {
        TreeSet<T> tempSet = new TreeSet<>(comparator);
        tempSet.addAll(elements);
        this.elements = new ArrayList<>(tempSet);
        this.comparator = comparator;
    }

    // NOTE-no-minus: remove only semantic copypaste
    private int search(T element, boolean notNegative) {
        int index = Collections.binarySearch(elements, element, comparator);
        if (notNegative) {
            if (index < 0) index = -index - 1;
        }
        return index;
    }

    // NOTE: please stick to Java coding style (one-liners of all kinds, formatting issues etc)

    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    private SortedSet<T> subSetWithCheck(T fromElement, T toElement, boolean rightBorderInclusive) {
        int index1 = search(fromElement, true);
        int index2 = search(toElement, true);
        if (rightBorderInclusive) {
            index2++;
        }
        return new ArraySet<T>(elements.subList(index1, index2), comparator);
    }

    // all elements fromElement <= x < toElement
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        if (comparator != null && comparator.compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException();
        }
        return subSetWithCheck(fromElement, toElement, false);
    }

    // all elements < toElement
    @Override
    public SortedSet<T> headSet(T toElement) {
        if (isEmpty()) {
            return new ArraySet<T>(comparator);
        }
        return subSetWithCheck(elements.get(0), toElement, false);
    }

    // all elements >= fromElement
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        if (isEmpty()) {
            return new ArraySet<T>(comparator);
        }
        return subSetWithCheck(fromElement, elements.get(elements.size() - 1), true);
    }

    private T getElement(int index) {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return elements.get(index);
    }

    @Override
    public T first() {
        return getElement(0);
    }

    @Override
    public T last() {
        return getElement(size() - 1);
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.size() == 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        if (isEmpty()) {
            return false;
        }
        return search((T) o, false) >= 0;
    }

    // NOTE: the iterator is not unmodifiable
    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(elements).iterator();
    }
}
