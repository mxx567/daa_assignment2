package datatypes;

public class DynamicArray<T> {
    private T[] array;
    private int size;

    public DynamicArray(T[] array) {
        this.array = array;
        size = array.length;
    }

    public void add(T x){
        if (size == array.length) {
            T[] newArray = (T[]) new Object[array.length * 2];

            for (int i = 0; i < size; i++) {
                newArray[i] = array[i];
            }

            array = newArray;
        }
        array[size++] = x;
    }


    public void add(int index, T x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
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

    public T remove(int index){
        T[] newArray = (T[]) new Object[array.length -1];
        T removed = array[index];
        for (int i = 0; i < index; i++) {
            newArray[i] = array[i];
        }
        for (int i = index; i < size; i++) {
            newArray[Math.max(i - 1, 0)] = array[i];
        }
        size--;
        array = newArray;
        return removed;
    }

    public T get(int index){
        return array[index];
    }

    public boolean contains(T x){
        for(T i : array){
            if(i == x){
                return true;
            }
        }
        return false;
    }

    public int len(){
        return array.length;
    }
}


