/**
 * Simple Red-Black tree implementation, where the keys are of type T.
 *
 * @ author ryan ma
 */
public class RedBlackTree<T extends Comparable<T>> {

	/**
	 * Root of the tree.
	 */
	private RBTreeNode<T> root;

	/**
	 * Empty constructor.
	 */
	public RedBlackTree() {
		root = null;
	}

	/**
	 * Constructor that builds this from given BTree (2-3-4) tree.
	 *
	 * @param tree BTree (2-3-4 tree).
	 */
	public RedBlackTree(BTree<T> tree) {
		root = buildRedBlackTree(tree.root);
		root.isBlack = true;
	}

	/**
	 * Builds a RedBlack tree that has isometry with given 2-3-4 tree rooted at
	 * given node r, and returns the root node.
	 *
	 * @param r root of the 2-3-4 tree.
	 * @return root of the Red-Black tree for given 2-3-4 tree.
	 */
	RBTreeNode<T> buildRedBlackTree(BTree.Node<T> r) {
		// Turn every BTree node into a cluster of RBTreeNodes, and
		// then call this method recursively on its child.
		if (r == null) {
			return null;
		}
		if (r.getItemCount() != 3 && r.getItemCount() != 2) {
			throw new IllegalArgumentException(
					"A BTree node must have 2 or 3 items! ");
		}

		RBTreeNode<T> root = new RBTreeNode<>(true, r.getItemAt(1),
				new RBTreeNode<>(false, r.getItemAt(0)), null);
		root.left.left = buildRedBlackTree(r.getChildAt(0));
		root.left.right = buildRedBlackTree(r.getChildAt(1));
		if (r.getItemCount() == 2) {
			root.right = buildRedBlackTree(r.getChildAt(2));
		} else if (r.getItemCount() == 3) {
			root.right = new RBTreeNode<>(false, r.getItemAt(2));
			root.right.left = buildRedBlackTree(r.getChildAt(2));
			root.right.right = buildRedBlackTree(r.getChildAt(3));
		}

		fixUp(root.left);
		fixUp(root.right);
		fixUp(root);
		return root;
	}

	/**
	 * Rotates the (sub)tree rooted at given node to the right, and returns the
	 * new root of the (sub)tree. If rotation is not possible somehow,
	 * immediately return the input node.
	 * Transfer the color from the original root to the new root, and color
	 * the original root red.
	 *
	 * @param node root of the given (sub)tree.
	 * @return new root of the (sub)tree.
	 */
	RBTreeNode<T> rotateRight(RBTreeNode<T> node) {
		RBTreeNode<T> newRoot = node.left;
		newRoot.isBlack = node.isBlack;
		node.isBlack = false;
		node.left = newRoot.right;
		newRoot.right = node;
		return newRoot;
	}

	/**
	 * Rotates the (sub)tree rooted at given node to the left, and returns the
	 * new root of the (sub)tree. If rotation is not possible somehow,
	 * immediately return the input node.
	 * Transfer the color from the original root to the new root, and color
	 * the original root red.
	 *
	 * @param node root of the given (sub)tree.
	 * @return new root of the (sub)tree.
	 */
	RBTreeNode<T> rotateLeft(RBTreeNode<T> node) {
		RBTreeNode<T> newRoot = node.right;
		newRoot.isBlack = node.isBlack;
		node.isBlack = false;
		node.right = newRoot.left;
		newRoot.left = node;
		return newRoot;
	}

	/**
	 * Flips the color of the node and its children. Assume that the node has
	 * both left and right children.
	 *
	 * @param node tree node
	 */
	void flipColors(RBTreeNode<T> node) {
		node.isBlack = !node.isBlack;
		node.left.isBlack = !node.left.isBlack;
		node.right.isBlack = !node.right.isBlack;
	}

	/**
	 * Returns whether a given node is red. null nodes (children of leaf) are
	 * automatically considered black.
	 *
	 * @param node node
	 * @return node is red.
	 */
	private boolean isRed(RBTreeNode<T> node) {
		return node != null && !node.isBlack;
	}

	/**
	 * Insert given item into this tree.
	 *
	 * @param item item
	 */
	void insert(T item) {
		root = insert(root, item);
		root.isBlack = true;
	}

	/**
	 * Recursively insert item into this tree. returns the (new) root of the
	 * subtree rooted at given node after insertion. node == null implies that
	 * we are inserting a new node at the bottom.
	 *
	 * @param node node
	 * @param item item
	 * @return (new) root of the subtree rooted at given node.
	 */
	private RBTreeNode<T> insert(RBTreeNode<T> node, T item) {

		// If the tree is empty, let this be the root with black color.
		if (node == null) {
			return new RBTreeNode<>(false, item);
		}

		// Handle normal binary search tree insertion.
		int comp = item.compareTo(node.item);
		if (comp == 0) {
			// already have a node has the same item as ITEM, do nothing.
			return node;
		} else if (comp < 0) {
			node.left = insert(node.left, item);
		} else {
			node.right = insert(node.right, item);
		}

		fixUp(node);
		return node;
	}

	/**
	 * Do fix up work.
	 *
	 * @param node node.
	 */
	private void fixUp(RBTreeNode<T> node) {
		// handle case C and "Right-leaning" situation.
		if (isRed(node.right) && !isRed(node.left)) {
			node = rotateLeft(node);
		}

		// handle case B
		if (isRed(node.left) && isRed(node.left.left)) {
			node = rotateRight(node);
		}

		// handle case A
		if (isRed(node.left) && isRed(node.right)) {
			flipColors(node);
		}
	}

	/**
	 * Public accessor method for the root of the tree.
	 */
	public RBTreeNode<T> graderRoot() {
		return root;
	}


	/**
	 * RedBlack tree node.
	 *
	 * @param <T> type of item.
	 */
	static class RBTreeNode<T> {

		/**
		 * Item.
		 */
		protected final T item;

		/**
		 * True if the node is black.
		 */
		protected boolean isBlack;

		/**
		 * Pointer to left child.
		 */
		protected RBTreeNode<T> left;

		/**
		 * Pointer to right child.
		 */
		protected RBTreeNode<T> right;

		/**
		 * A node that is black iff BLACK, containing VALUE, with empty
		 * children.
		 */
		RBTreeNode(boolean black, T value) {
			this(black, value, null, null);
		}

		/**
		 * Node that is black iff BLACK, contains VALUE, and has children
		 * L AND R.
		 */
		RBTreeNode(boolean black, T value,
				   RBTreeNode<T> l, RBTreeNode<T> r) {
			isBlack = black;
			item = value;
			left = l;
			right = r;
		}
	}
}
