package com.burbn.util;

/**
 * CustomArrayList implementation demonstrating dynamic resizing array data structure.
 *
 * @param <T> Element type
 */
@SuppressWarnings("unchecked")
public class CustomArrayList<T> {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_CAPACITY = 10;

    public CustomArrayList() {
        this.elements = new Object[DEFAULT_CAPACITY];
    }

    public CustomArrayList(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.elements = new Object[initialCapacity];
    }

    public void add(T value) {
        if (size == elements.length) {
            resize();
        }
        elements[size++] = value;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (T) elements[index];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public T removeLast() {
        if (size == 0) {
            throw new IllegalStateException("Cannot remove from an empty list");
        }
        T value = get(size - 1);
        elements[--size] = null;
        return value;
    }

    private void resize() {
        Object[] newElements = new Object[elements.length * 2];
        System.arraycopy(elements, 0, newElements, 0, elements.length);
        elements = newElements;
    }
}
