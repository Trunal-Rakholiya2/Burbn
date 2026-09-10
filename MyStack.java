package util;

public class MyStack<T> {
    private MyArrayList<T> list = new MyArrayList<>();

    public void push(T value) {
        list.add(value);
    }
    public T pop() {
        if (isEmpty()) throw new RuntimeException("Stack empty");
        return list.removeLast();
    }
    public boolean isEmpty() {
        return list.size() == 0;
    }
}
