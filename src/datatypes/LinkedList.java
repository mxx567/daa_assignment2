package datatypes;

public class LinkedList<T> {
    Node<T> head;

    public void add(T x){
        Node<T> node = new Node<T>();
        node.setValue(x);
        if(head == null){
            head = node;
        }
        else {
            Node<T> n = head;
            while(n.getNext() != null){
                n = n.getNext();
            }
            n.setNext(node);
        }
    }

    public void add(int index, T x){
        Node<T> node = new Node<T>();
        node.setValue(x);
        if(head == null && index == 0){
            head = node;
        }
        else if(index > getLength() - 1){
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds!");
        }
        else {
            Node<T> n = head;
            for(int i = 0; i < index - 1; i++){
                n = n.getNext();
            }
            node.setNext(n.getNext());
            n.setNext(node);
        }
    }

    public void remove(int index){
        if(index > getLength() - 1){
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds!");
        }
        else if(head != null && index == 0){
            head = head.getNext();
        }
        else {
            Node<T> n = head;
            for(int i = 0; i < index - 1; i++){
                n = n.getNext();
            }
            n.setNext(n.getNext().getNext());
        }
    }

    public boolean contains(T x){
        if(head == null){
            return false;
        }
        else {
            Node<T> n = head;
            while(n.getNext() != null){
                if(n.getValue() == x){
                    return true;
                }
                n = n.getNext();
            }
            return n.getValue() == x;
        }
    }

    public int getLength(){
        int len = 1;
        Node<T> node = head;
        if(node == null){
            return 0;
        }
        while(node.getNext() != null){
            len++;
            node = node.getNext();
        }
        return len;
    }



    public void show(){
        Node<T> node = head;
        while(node.getNext() != null){
            System.out.print(node.getValue() + " ");
            node = node.getNext();
        }
        System.out.print(node.getValue());
    }
}
