package util;

public class
MyArrayList<T> {
    private Object[] arr;
    private int size = 0;

    public MyArrayList() {
        arr = new Object[10];
    }

    public void add(T value) {
        if (size == arr.length) {
            Object[] newArr = new Object[arr.length * 2];
            for (int i = 0; i < arr.length; i++) newArr[i] = arr[i];
            arr = newArr;
        }
        arr[size++] = value;
    }

    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return (T) arr[index];
    }

    public int size() {
        return size;
    }

    public T removeLast() {
        if (size == 0) throw new RuntimeException("No elements");
        T value = get(size - 1);
        arr[--size] = null;
        return value;
    }
}
