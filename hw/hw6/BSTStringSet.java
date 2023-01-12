import java.util.*;

/**
 * Implementation of a BST based String Set.
 *
 * @author ryan ma
 */
public class BSTStringSet implements StringSet, Iterable<String>, SortedStringSet {

	/**
	 * Creates a new empty set.
	 */
	public BSTStringSet() {
		_root = null;
	}

	@Override
	public void put(String s) {
		if (_root == null) {
			_root = new Node(s);
			return;
		}
		Node n = _root;
		while (true) {
			int result = n.s.compareTo(s);
			if (result > 0) {
				if (n.left == null) {
					n.left = new Node(s);
					return;
				} else {
					n = n.left;
				}
			} else if (result < 0) {
				if (n.right == null) {
					n.right = new Node(s);
					return;
				} else {
					n = n.right;
				}
			} else {
				return;
			}
		}
	}

	@Override
	public boolean contains(String s) {
		Node n = _root;
		while (n != null) {
			int result = n.s.compareTo(s);
			if (result > 0) {
				n = n.left;
			} else if (result < 0) {
				n = n.right;
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> asList() {
		ArrayList<String> setList = new ArrayList<>();
		for (String s : this) {
			setList.add(s);
		}
		return setList;
	}

	/**
	 * Represents a single Node of the tree.
	 */
	private static class Node {

		/**
		 * Creates a Node containing SP.
		 */
		Node(String sp) {
			s = sp;
		}

		/**
		 * String stored in this Node.
		 */
		private String s;

		/**
		 * Left child of this Node.
		 */
		private Node left;

		/**
		 * Right child of this Node.
		 */
		private Node right;

	}

	/**
	 * An iterator over BSTs.
	 */
	private static class BSTIterator implements Iterator<String> {
		/*
		 * Stack of nodes to be delivered. The values to be delivered
		 * are (a) the label of the top of the stack, then (b)
		 * the labels of the right child of the top of the stack in inorder,
		 * then (c) the nodes in the rest of the stack (i.e., the result
		 * of recursively applying this rule to the result of popping
		 * the stack.)
		 * 使用stack构造一个迭代器类, 迭代顺序为inorder traverse(中序遍历),
		 * 因BST本身遵循binary tree property(左<中<右), 因此中序遍历就是从小
		 * 到大的遍历顺序。
		 */
		protected Stack<Node> _toDo = new Stack<>();

		/**
		 * A new iterator over the labels in NODE.
		 */
		BSTIterator(Node node) {
			addTree(node);
		}

		@Override
		public boolean hasNext() {
			return !_toDo.empty();
		}

		@Override
		public String next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			Node node = _toDo.pop();
			// 中序遍历遍历完结点后轮到结点的右子女
			addTree(node.right);
			return node.s;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * Add the relevant subtrees of the tree rooted at NODE.
		 */
		private void addTree(Node node) {
			// 中序遍历最先遍历左子女，故将左子女放在栈顶
			while (node != null) {
				_toDo.push(node);
				node = node.left;
			}
		}
	}

	@Override
	public Iterator<String> iterator() {
		/* Override iterator() method in interface Iterable, return a
		 * iterator of this class. */
		return new BSTIterator(_root);
	}

	/**
	 * An iterator over BSTs with ranges.
	 */
	private static class BSTIteratorWithRange extends BSTIterator {

		/**
		 * Redefine addTree method.
		 */
		private void addTree(Node node) {
			if (node != null) {
				int comLeft = node.s.compareTo(_low);
				int comRight = node.s.compareTo(_high);
				if (comLeft >= 0 && comRight < 0) {
					_toDo.push(node);
				}
				if (comLeft > 0) {
					addTree(node.left);
				}
			}
		}

		@Override
		public String next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			Node node = _toDo.pop();
			int comRight = node.s.compareTo(_high);
			if (comRight < 0) {
				addTree(node.right);
			}
			return node.s;
		}

		/**
		 * Constructor function.
		 */
		BSTIteratorWithRange(Node node, String low, String high) {
			super(node);
			_low = low;
			_high = high;
		}

		/**
		 * 在Iterator中存储range.
		 */
		private final String _low;
		private final String _high;
	}

	@Override
	public Iterator<String> iterator(String low, String high) {
		return new BSTIteratorWithRange(_root, low, high);
	}

	/**
	 * Root node of the tree.
	 */
	private Node _root;

}
