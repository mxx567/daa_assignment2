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
            T[] newArray = (T[]) new Object[array.length + 1];

            for (int i = 0; i < size; i++) {
                newArray[i] = array[i];
            }

            array = newArray;
        }
        array[size++] = x;
    }


    public void add(int index, T x){
        if (size == array.length) {
            T[] newArray = (T[]) new Object[array.length + 1];

            for (int i = 0; i < index; i++) {
                newArray[i] = array[i];
            }
            for (int i = index + 1; i < size + 1; i++) {
                newArray[i] = array[i-1];
            }
            size++;
            array = newArray;
        }
        array[index] = x;
    }

    public void remove(int index){
        T[] newArray = (T[]) new Object[array.length -1];

        for (int i = 0; i < index-1; i++) {
            newArray[i] = array[i];
        }
        for (int i = index; i < size; i++) {
            newArray[i-1] = array[i];
        }
        size--;
        array = newArray;
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


