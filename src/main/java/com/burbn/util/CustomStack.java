package com.burbn.util;

/**
 * CustomStack implementation using CustomArrayList for LIFO operations.
 *
 * @param <T> Element type
 */
public class CustomStack<T> {
    private final CustomArrayList<T> list = new CustomArrayList<>();

    public void push(T value) {
        list.add(value);
    }

    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return list.removeLast();
    }

    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return list.get(list.size() - 1);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }
}
