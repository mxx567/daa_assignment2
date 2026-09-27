package datatypes;

public class DynamicArray<T> {
    private T[] array;
    private int size;

    public DynamicArray(T[] array) {
        this.array = array;
        size = array.length;
    }

    public void add(T x) {
        if (size == array.length) {
            T[] newArray = (T[]) new Object[Math.max(1, array.length * 2)];

            for (int i = 0; i < size; i++) {
                newArray[i] = array[i];
            }

            array = newArray;
        }

        array[size++] = x;
    }

    public void add(int index, T x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        if (size == array.length) {
            T[] newArray = (T[]) new Object[Math.max(1, array.length * 2)];

            for (int i = 0; i < size; i++) {
                newArray[i] = array[i];
            }

            array = newArray;
        }

        for (int i = size; i > index; i--) {
            array[i] = array[i - 1];
        }

        array[index] = x;
        size++;
    }

    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        T removed = array[index];

        for (int i = index; i < size - 1; i++) {
            array[i] = array[i + 1];
        }

        array[--size] = null;
        return removed;
    }

    public T get(int index){
        return array[index];
    }

    public boolean contains(T x) {
        for (int i = 0; i < size; i++) {
            if (java.util.Objects.equals(array[i], x)) {
                return true;
            }
        }
        return false;
    }

    public int len(){
        return array.length;
    }
}


