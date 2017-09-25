package cmsc420.meeshquest.part1;

import java.util.Comparator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

//Taken from Florida International University http://users.cis.fiu.edu/~weiss/dsaajava/code/DataStructures/AvlTree.java
//http://kukuruku.co/hub/cpp/avl-trees

// BinarySearchTree class
//
// CONSTRUCTION: with no initializer
//
// ******************PUBLIC OPERATIONS*********************
// void insert( x )       --> Insert x
// void remove( x )       --> Remove x (unimplemented)
// Comparable find( x )   --> Return item that matches x
// Comparable findMin( )  --> Return smallest item
// Comparable findMax( )  --> Return largest item
// boolean isEmpty( )     --> Return true if empty; else false
// void makeEmpty( )      --> Remove all items
// void printTree( )      --> Print tree in sorted order

/**
 * Implements an AVL tree.
 * Note that all "matching" is based on the compareTo method.
 * @author Mark Allen Weiss
 * @param <T>
 */
class AvlNode {
	// Constructors
	AvlNode(City element) {
		this(element, null, null);
	}

	AvlNode(City element, AvlNode lt, AvlNode rt) {
		this.element  = element;
		left     = lt;
		right    = rt;
		height   = 0;
	}

	// Friendly data; accessible by other package routines
	City element;      // The data in the node
	AvlNode left;         // Left child
	AvlNode right;        // Right child
	int height;       // Height
}

public class AvlTree {
	/**
	 * Construct the tree.
	 */
	public AvlTree(Comparator<City> comparator) {
		root = null;
		numNodes = 0;
		maxImbalance = 1;
		this.comparator = comparator;
	}

	public AvlTree(Comparator<City> comparator, int maxImbalance) {
		root = null;
		numNodes = 0;
		this.maxImbalance = maxImbalance;
		this.comparator = comparator;
	}

	/**
	 * Insert into the tree; duplicates are ignored.
	 * @param x the item to insert.
	 */
	public void insert(City x) {
		root = insert(x, root);
		
	}

	private int balanceFactor(AvlNode node) {
		return height(node.right) - height(node.left);
	}
	
	private AvlNode balance(AvlNode node) {
		fixHeight(node);
		
		if (balanceFactor(node) == (maxImbalance + 1)) {
			if (balanceFactor(node.right) < 0)
				node.right = rotateRight(node.right);
			return rotateLeft(node);
		} else if (balanceFactor(node) == -(maxImbalance + 1)) {
			if (balanceFactor(node.left) > 0)
				node.left = rotateLeft(node.left);
			return rotateRight(node);
		}
		
		return node;
	}
	/**
	 * Internal method to insert into a subtree.
	 * @param x the item to insert.
	 * @param t the node that roots the tree.
	 * @return the new root.
	 */
	private AvlNode insert(City x, AvlNode t) {
		if (t == null) {
			t = new AvlNode(x);
			numNodes++;
			return t;
		} else if(comparator.compare(x, t.element) < 0)
			t.left = insert(x, t.left);
		else if (comparator.compare(x, t.element) > 0)
			t.right = insert(x, t.right);
		return balance(t);
	}
	/**
	 * Remove from the tree. Nothing is done if x is not found.
	 * @param x the item to remove.
	 */
	
	public void remove(City x) {
		System.out.println("Sorry, remove unimplemented");
	}

	/**
	 * Find the smallest item in the tree.
	 * @return smallest item or null if empty.
	 */
	public City findMin() {
		return elementAt(findMin(root));
	}

	/**
	 * Internal method to find the smallest item in a subtree.
	 * @param t the node that roots the tree.
	 * @return node containing the smallest item.
	 */
	private AvlNode findMin(AvlNode t) {
		if (t.left == null)
			return t;
		else
			return findMin(t.left);
	}

	/**
	 * Find the largest item in the tree.
	 * @return the largest item of null if empty.
	 */
	public City findMax() {
		return elementAt(findMax(root));
	}

	/**
	 * Internal method to find the largest item in a subtree.
	 * @param t the node that roots the tree.
	 * @return node containing the largest item.
	 */
	private AvlNode findMax(AvlNode t) {
		if (t.right == null)
			return t;
		else
			return findMax(t.right);
	}

	/**
	 * Find an item in the tree.
	 * @param x the item to search for.
	 * @return the matching item or null if not found.
	 */
	public City find(City x) {
		return elementAt(find(x, root));
	}
	
	/**
	 * Internal method to find an item in a subtree.
	 * @param x is item to search for.
	 * @param t the node that roots the tree.
	 * @return node containing the matched item.
	 */
	private AvlNode find(City x, AvlNode t) {
		while (t != null) {
			if (comparator.compare(x, t.element) < 0)
				t = t.left;
			else if (comparator.compare(x, t.element) > 0 )
				t = t.right;
			else
				return t;    // Match
		}

		return null;   // No match
	}
	
	/**
	 * Make the tree logically empty.
	 */
	public void clear() {
		root = null;
		numNodes = 0;
	}

	/**
	 * Test if the tree is logically empty.
	 * @return true if empty, false otherwise.
	 */
	public boolean isEmpty() {
		return root == null;
	}

	/**
	 * Print the tree contents in sorted order.
	 */
	public void printTree() {
		if (isEmpty())
			System.out.println( "Empty tree" );
		else
			printTree(root);
	}

	/**
	 * Internal method to print a subtree in sorted order.
	 * @param t the node that roots the tree.
	 */
	private void printTree(AvlNode t) {
		if (t != null) {
			printTree(t.left);
			System.out.println(t.element.getName() + " " + height(t));
			printTree(t.right);
		}
	}

	public Element printToXML(Document results) {
		Element avlgTree = results.createElement("AvlGTree");
		avlgTree.setAttribute("cardinality", Integer.toString(numNodes));
		avlgTree.setAttribute("height", Integer.toString(height(root)));
		avlgTree.setAttribute("maxImbalance", Integer.toString(maxImbalance));
		printToXml(results, avlgTree, root);

		return avlgTree;

	}

	private void printToXml(Document results, Element parent, AvlNode t) {
		Element node;
		if (t == null) {
			node = results.createElement("emptyChild");
		} else {
			node = results.createElement("node");
			node.setAttribute("name", t.element.getName());
			node.setAttribute("radius", Integer.toString((int) t.element.getRadius()));
			printToXml(results, node, t.left);
			printToXml(results, node, t.right);
		}
		parent.appendChild(node);
	}

	/**
	 * Internal method to get element field.
	 * @param t the node.
	 * @return the element field or null if t is null.
	 */
	private City elementAt(AvlNode t) {
		return t == null ? null : t.element;
	}

	/**
	 * Return the height of node t, or -1, if null.
	 */
	private static int height(AvlNode t) {
		return t == null ? -1 : t.height;
	}

	private static void fixHeight(AvlNode t) {
		t.height = Math.max(height(t.left), height(t.right)) + 1;
	}
	/**
	 * Rotate binary tree node with left child.
	 * For AVL trees, this is a single rotation for case 1.
	 * Update heights, then return new root.
	 */
	private static AvlNode rotateLeft(AvlNode node) {
		AvlNode temp = node.right;
		node.right = temp.left;
		temp.left = node;
		fixHeight(node);
		fixHeight(temp);
		
		return temp;
	}

	/**
	 * Rotate binary tree node with right child.
	 * For AVL trees, this is a single rotation for case 4.
	 * Update heights, then return new root.
	 */
	private static AvlNode rotateRight(AvlNode node) {
		AvlNode temp = node.left;
		node.left = temp.right;
		temp.right = node;
		fixHeight(node);
		fixHeight(temp);

		return temp;
	}

	/**
	 * Double rotate binary tree node: first left child
	 * with its right child; then node k3 with new left child.
	 * For AVL trees, this is a double rotation for case 2.
	 * Update heights, then return new root.
	 */
	private static AvlNode rotateRightLeft(AvlNode k3) {
		k3.left = rotateRight(k3.left);
		return rotateLeft(k3);
	}

	/**
	 * Double rotate binary tree node: first right child
	 * with its left child; then node k1 with new right child.
	 * For AVL trees, this is a double rotation for case 3.
	 * Update heights, then return new root.
	 */
	private static AvlNode rotateLeftRight(AvlNode k1 ) {
		k1.right = rotateLeft(k1.right);
		return rotateRight(k1);
	}

	/** The tree root. */
	private AvlNode root;
	private int numNodes;
	private int maxImbalance;
	private Comparator<City> comparator;
}