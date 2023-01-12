import java.util.ArrayList;

/**
 * A Generic heap class. It stores any type of object
 * (represented by type T) and an associated priority value.
 *
 * @author ryan ma
 */

public class ArrayHeap<T> {

    /**
     * An ArrayList that stores the nodes in this binary heap.
     * Use an ArrayList to represent a heap.
     */
    private final ArrayList<Node> contents;

    /**
     * A constructor that initializes an empty ArrayHeap.
     */
    public ArrayHeap() {
        contents = new ArrayList<>();
        contents.add(null);
    }

    /**
     * Returns the number of elements in the priority queue.
     */
    public int size() {
        return contents.size() - 1;
    }

    /**
     * Returns the node at index INDEX.
     */
    private Node getNode(int index) {
        if (index >= contents.size()) {
            return null;
        } else {
            return contents.get(index);
        }
    }

    /**
     * Sets the node at INDEX to N
     */
    private void setNode(int index, Node n) {
        while (index + 1 > contents.size()) {
            contents.add(null);
        }
        contents.set(index, n);
    }

    /**
     * Returns and removes the node located at INDEX.
     */
    private Node removeNode(int index) {
        if (index >= contents.size()) {
            return null;
        } else {
            return contents.remove(index);
        }
    }

    /**
     * Swap the nodes at the two indices.
     */
    private void swap(int index1, int index2) {
        Node node1 = getNode(index1);
        Node node2 = getNode(index2);
        contents.set(index1, node2);
        contents.set(index2, node1);
    }

    /**
     * A Node class that stores items and their associated priorities.
     */
    public class Node implements Comparable<Node> {

        /**
         * Item.
         */
        private final T _item;

        /**
         * Priority.
         */
        private double _priority;

        /**
         * Constructor function.
         */
        private Node(T item, double priority) {
            this._item = item;
            this._priority = priority;
        }

        /**
         * Return the item of this node.
         */
        public T item() {
            return this._item;
        }

        /**
         * Set this node's priority to PRIORITY.
         */
        public void setPriority(double priority) {
            this._priority = priority;
        }

        @Override
        public int compareTo(Node node) {
            return Double.compare(this._priority, node._priority);
        }

        @Override
        public String toString() {
            return this._item.toString() + ", " + this._priority;
        }
    }

    /**
     * Returns the index of the left child of the node at i.
     */
    private int getLeftChild(int i) {
        return 2 * i;
    }

    /**
     * Returns the index of the right child of the node at i.
     */
    private int getRightChild(int i) {
        return 2 * i + 1;
    }

    /**
     * Returns the index of the node that is the parent of the
     * node at i.
     */
    private int getParent(int i) {
        return i / 2;
    }

    /**
     * Returns the index of the node with smaller priority. If one
     * node is null, then returns the index of the non-null node.
     * Precondition: at least one of the nodes is not null.
     */
    private int min(int index1, int index2) {
        Node n1 = getNode(index1);
        Node n2 = getNode(index2);
        if (n1 == null) {
            return index2;
        } else if (n2 == null) {
            return index1;
        } else {
            if (n1.compareTo(n2) <= 0) {
                return index1;
            }
            return index2;
        }
    }

    /**
     * Returns the item with the smallest priority value, but does
     * not remove it from the heap. If multiple items have the minimum
     * priority value, returns any of them. Returns null if heap is
     * empty.
     */
    public T peek() {
        if (size() == 0) {
            return null;
        } else {
            return getNode(1).item();
        }
    }

    /**
     * Bubbles up the node currently at the given index until no longer
     * needed.
     */
    private void bubbleUp(int index) {
        int parent = getParent(index);
        if (getNode(parent) == null) return;

        if (getNode(index).compareTo(getNode(parent)) < 0) {
            swap(index, parent);
            bubbleUp(parent);
        }
    }

    /**
     * Bubbles down the node currently at the given index until no longer
     * needed.
     */
    private void bubbleDown(int index) {
        int leftIndex = getLeftChild(index);
        int rightIndex = getRightChild(index);

        Node node = getNode(index);
        Node leftChild = getNode(leftIndex);
        Node rightChild = getNode(rightIndex);

        boolean needBubbleDown = false;
        if (leftChild != null) {
            needBubbleDown = node.compareTo(leftChild) > 0;
        }
        if (rightChild != null) {
            needBubbleDown = needBubbleDown || node.compareTo(rightChild) > 0;
        }

        if (needBubbleDown) {
            int minChildIndex = min(leftIndex, rightIndex);
            swap(index, minChildIndex);
            bubbleDown(minChildIndex);
        }
    }

    /**
     * Inserts an item with the given priority value. Assume that item is
     * not already in the heap. Same as enqueue, or offer.
     */
    public void insert(T item, double priority) {
        Node node = new Node(item, priority);
        setNode(size() + 1, node);
        bubbleUp(size());
    }

    /**
     * Returns the element with the smallest priority value, and removes
     * it from the heap. If multiple items have the minimum priority value,
     * removes any of them. Returns null if the heap is empty. Same as
     * dequeue, or poll.
     */
    public T removeMin() {
        if (size() == 0) {
            return null;
        }
        Node minNode = getNode(1);
        assert minNode != null;
        swap(1, size());
        removeNode(size());
        bubbleDown(1);
        return minNode.item();
    }

    /**
     * Changes the node in this heap with the given item to have the given
     * priority. You can assume the heap will not have two nodes with the
     * same item. Does nothing if the item is not in the heap. Check for
     * item equality with .equals(), not ==
     */
    public void changePriority(T item, double priority) {
        int i = 1;
        boolean exist = false;
        for (; i <= size(); i++) {
            if (getNode(i).item() == item) {
                exist = true;
                break;
            }
        }
        if (exist) {
            getNode(i).setPriority(priority);
            bubbleUp(i);
            bubbleDown(i);
        }
    }
}