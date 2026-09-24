import datatypes.DynamicArray;

public class Main {
    void main(String[] args){
        Integer[] arr = {1,2,3,4,5};
        DynamicArray<Integer> array = new DynamicArray<>(arr);

        array.remove(3);
        array.add(2);
        array.add(0,-1);
        System.out.println(array.contains(-1));

        for(int i = 0; i< array.len(); i++){
            System.out.print(array.get(i) + " ");
        }
    }

}
