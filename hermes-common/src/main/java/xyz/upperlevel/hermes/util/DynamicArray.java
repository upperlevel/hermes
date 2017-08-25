package xyz.upperlevel.hermes.util;

import java.util.Arrays;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class DynamicArray<E> implements Iterable<E> {
    private Object[] data;
    private final int maxCapacity;

    public DynamicArray(int defaultCapacity, int maxCapacity) {
        this.data = new Object[defaultCapacity];
        this.maxCapacity = maxCapacity;
    }

    public DynamicArray(int defaultCapacity) {
        this(defaultCapacity, 2147483639);
    }


    public int capacity() {
        return data.length;
    }

    public int maxLength() {
        return maxCapacity;
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        checkBounds(index);
        if (index >= data.length)
            return null;
        else
            return (E) data[index];
    }

    public void set(int index, E value) {
        checkBounds(index);
        ensureCapacity(index);
        data[index] = value;
    }

    protected void checkBounds(int index) {
        if(index < 0)
            throw new IndexOutOfBoundsException("Index must be positive!");
        if(index > maxCapacity)
            throw new IndexOutOfBoundsException("Max length reached: " + index + "(max: " + maxCapacity + ")");
    }

    protected void ensureCapacity(int index) {
        if(index >= data.length) {
            grow(index + 1);
        }
    }

    protected void grow(int newCapacity) {
        int len = data.length + (data.length >> 1);
        if(len < newCapacity) {
            len = newCapacity;
        }
        if(len > maxCapacity)
            len = maxCapacity;
        data = Arrays.copyOf(data, len);
    }

    public Object[] getArray() {
        return data;
    }

    @Override
    public ListIterator<E> iterator() {
        return new DynamicArrayIterator(0);
    }

    public ListIterator<E> iterator(int index) {
        return new DynamicArrayIterator(index);
    }

    private class DynamicArrayIterator implements ListIterator<E> {
        private int index;
        private int lastReturn = -1;


        public DynamicArrayIterator(int index) {
            this.index = index;
        }

        @Override
        public boolean hasNext() {
            return index < maxCapacity;
        }

        @Override
        public E next() {
            if(!hasNext()) throw new NoSuchElementException();
            return get(lastReturn = index++);
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public E previous() {
            if(!hasPrevious()) throw new NoSuchElementException();
            return get(lastReturn = --index);
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            set(null);
        }

        @Override
        public void set(E e) {
            if(lastReturn < 0)
                throw new IllegalStateException();
            data[lastReturn] = e;
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException();
        }
    }
}
