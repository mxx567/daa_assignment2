import datatypes.*;

public class Main {
    void main(String[] args){
        Integer[] arr = {1,2,3,4,5};
        DynamicArray<Integer> array = new DynamicArray<>(arr);

        array.remove(3);
        array.add(2);
        array.add(0,-1);
        System.out.println(array.contains(-1));

        LinkedList<Integer> ll = new LinkedList<>();
        ll.add(5);
        ll.add(4);
        ll.add(3);

        ll.add(0,-2);

        System.out.println(ll.get(3).getValue());

        ll.show();
    }

}
