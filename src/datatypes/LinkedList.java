package datatypes;


public class LinkedList<T> {
    Node<T> head;
    private int size;

    public void add(T x) {
        Node<T> node = new Node<>();
        node.setValue(x);

        if (head == null) {
            head = node;
        } else {
            Node<T> current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(node);
        }
        size++;
    }

    public void add(int index, T x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds!");
        }

        Node<T> node = new Node<>();
        node.setValue(x);

        if (index == 0) {
            node.setNext(head);
            head = node;
        } else {
            Node<T> previous = head;
            for (int i = 0; i < index - 1; i++) {
                previous = previous.getNext();
            }
            node.setNext(previous.getNext());
            previous.setNext(node);
        }
        size++;
    }

    public Node<T> remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds!");
        }

        Node<T> removed;
        if (index == 0) {
            removed = head;
            head = head.getNext();
        } else {
            Node<T> previous = head;
            for (int i = 0; i < index - 1; i++) {
                previous = previous.getNext();
            }
            removed = previous.getNext();
            previous.setNext(removed.getNext());
        }
        removed.setNext(null);
        size--;
        return removed;
    }

    public boolean contains(T x) {
        Node<T> current = head;
        while (current != null) {
            if (java.util.Objects.equals(current.getValue(), x)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    public int getLength() {
        return size;
    }

    public Node<T> get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds!");
        }

        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        return current;
    }

    public void show() {
        Node<T> current = head;
        while (current != null) {
            System.out.print(current.getValue() + " ");
            current = current.getNext();
        }
    }
}
