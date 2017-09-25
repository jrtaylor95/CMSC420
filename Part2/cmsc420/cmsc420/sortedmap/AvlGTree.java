package cmsc420.sortedmap;


import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

import org.w3c.dom.Element;

//Taken from Florida International University http://users.cis.fiu.edu/~weiss/dsaajava/code/DataStructures/AvlTree.java
//http://kukuruku.co/hub/cpp/avl-trees

public class AvlGTree<K, V> extends AbstractMap<K, V> implements SortedMap<K, V> {
	private AvlNode<K, V> root;
	int modCount;
	private int size;
	private int maxImbalance;
	private Comparator<? super K> comparator;

	/**
	 * Construct the tree.
	 */

	public AvlGTree(Comparator<? super K> comparator) {
		this.comparator = comparator;
		this.maxImbalance = 1;
		modCount = 0;
		size = 0;
		root = null;
	}

	public AvlGTree(Comparator<? super K> comparator, final int maxImbalance) {
		root = null;
		size = 0;
		modCount = 0;
		this.maxImbalance = maxImbalance;
		this.comparator = comparator;
	}

	private void balance(AvlNode<K, V> node) {
		//		node.fixHeight();

		if (node.getBalance() > maxImbalance) {
			if (node.left.getBalance() >= 0)
				node = rotateRight(node);
			else
				node = rotateLeftRight(node);
		} else if (node.getBalance() < -maxImbalance) {
			if (node.right.getBalance() <= 0)
				node = rotateLeft(node);
			else
				node = rotateRightLeft(node);
		}

		if (node.parent != null)
			balance(node.parent);
		else
			root = node;
	}

	/**
	 * Make the tree logically empty.
	 */
	public void clear() {
		modCount++;
		root = null;
		size = 0;
	}

	/**
	 * Test if the tree is logically empty.
	 * @return true if empty, false otherwise.
	 */
	public boolean isEmpty() {
		return root == null;
	}

	public int height() {
		return root.getHeight();
	}

	public int getG() {
		return maxImbalance;
	}

	/**
	 * Rotate binary tree node with left child.
	 * For AVL trees, this is a single rotation for case 1.
	 * Update heights, then return new root.
	 */
	private static <K, V> AvlNode<K, V> rotateLeft(AvlNode<K, V> node) {
		if (node == null)
			return null;

		AvlNode<K, V> right = node.right;
		node.right = right.left;
		if (right.left != null)
			right.left.parent = node;
		right.parent = node.parent;

		if (node.parent != null) {
			if (node.parent.left == node)
				node.parent.left = right;
			else
				node.parent.right = right;
		}
		right.left = node;
		node.parent = right;

		node.rightHeight = right.leftHeight;
		right.leftHeight = node.getHeight();
		right.fixHeight();

		return right;
	}

	/**
	 * Rotate binary tree node with right child.
	 * For AVL trees, this is a single rotation for case 4.
	 * Update heights, then return new root.
	 */
	private static <K, V> AvlNode<K, V> rotateRight(AvlNode<K, V> node) {
		if (node == null)
			return null;

		AvlNode<K, V> left = node.left;
		node.left = left.right;
		if (left.right != null)
			left.right.parent = node;
		left.parent = node.parent;

		if (node.parent != null) {
			if (node.parent.right == node)
				node.parent.right = left;
			else
				node.parent.left = left;
		}

		left.right = node;
		node.parent = left;

		node.leftHeight = left.rightHeight;
		left.rightHeight = node.getHeight();
		left.fixHeight();

		return left;
	}

	/**
	 * Double rotate binary tree node: first left child
	 * with its right child; then node k3 with new left child.
	 * For AVL trees, this is a double rotation for case 2.
	 * Update heights, then return new root.
	 */
	private static <K, V> AvlNode<K, V> rotateRightLeft(AvlNode<K, V> node) {
		node.right = rotateRight(node.right);
		return rotateLeft(node);
	}

	/**
	 * Double rotate binary tree node: first right child
	 * with its left child; then node k1 with new right child.
	 * For AVL trees, this is a double rotation for case 3.
	 * Update heights, then return new root.
	 */
	private static <K, V> AvlNode<K, V> rotateLeftRight(AvlNode<K, V> node) {
		node.left = rotateLeft(node.left);
		return rotateRight(node);
	}

	@SuppressWarnings("unchecked")
	public AvlNode<K, V> getNode(Object key) {
		if (root == null)
			return null;

		AvlNode<K, V> currNode = root;

		int comp;
		while (currNode != null) {
			comp = comparator.compare((K) key, currNode.key);

			if (comp > 0)
				currNode = currNode.right;
			else if (comp < 0)
				currNode = currNode.left;
			else
				return currNode;
		}

		return null;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean containsKey(Object key) {
		return getNode(key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null)
			throw new NullPointerException();

		for (AvlNode<K, V> node = getFirstNode(); node != null; node = successor(node)) {
			if (node.value.equals(value))
				return true;
		}

		return false;
	}

	@Override
	public V get(Object key) {
		if (key == null)
			throw new NullPointerException();

		AvlNode<K, V> node = getNode(key);
		return node == null ? null : node.value;
	}

	@Override
	public V put(K key, V value) {
		int comp;
		AvlNode<K, V> currNode = root, prevNode;

		if (key == null || value == null)
			throw new NullPointerException();

		if (root == null)
			root = new AvlNode<K, V>(key, value);
		else {
			do {
				comp = comparator.compare(key, currNode.key);
				prevNode = currNode;

				if (comp > 0) {
					currNode = currNode.right;
				} else if (comp < 0) {
					currNode = currNode.left;
				} else
					return currNode.setValue(value);
			} while (currNode != null);

			currNode = new AvlNode<K, V>(key, value);

			if (comp > 0) {
				prevNode.right = currNode;
				prevNode.rightHeight = 1;
			} else {
				prevNode.left = currNode;
				prevNode.leftHeight = 1;
			}

			currNode.parent = prevNode;
			prevNode.fixHeight();
			balance(currNode);
		}

		size++;
		modCount++;

		return null;
	}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		modCount++;
		super.putAll(m);
	}

	@Override
	public Comparator<? super K> comparator() {
		return comparator;
	}

	@Override
	public SortedMap<K, V> subMap(K fromKey, K toKey) {
		return new SubMap(fromKey, toKey);
	}

	@Override
	public K firstKey() {
		return getFirstNode().key;
	}

	@Override
	public K lastKey() {
		return getLastNode().key;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new EntrySet();
	}

	public static <K, V> AvlNode<K, V> successor(AvlNode<K, V> e) {
		if (e == null)
			return null;
		else if (e.right != null) {
			AvlNode<K, V> p = e.right;
			while (p.left != null)
				p = p.left;
			return p;
		} else {
			AvlNode<K, V> p = e.parent;
			AvlNode<K, V> ch = e;

			while (p != null && ch == p.right) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}

	public AvlNode<K, V> getFirstNode() {
		AvlNode<K, V> first = root;

		if (first != null) {
			while (first.left != null)
				first = first.left;
		}

		return first;
	}

	public AvlNode<K, V> getNearestLowestNode(K key) {
		AvlNode<K, V> currNode = root;

		while (currNode != null) {
			int comp = comparator.compare(key, currNode.key);
			if (comp < 0) {
				if (currNode.left != null)
					currNode = currNode.left;
				else
					return currNode;
			} else if (comp > 0) {
				if (currNode.right != null)
					currNode = currNode.right;
				else {
					AvlNode<K, V> parent = currNode.parent;
					AvlNode<K, V> child = currNode;
					while(parent != null && child == parent.right) {
						child = parent;
						parent = parent.parent;
					}
					return parent;
				}
			} else 
				return currNode;
		}
		return null;
	}

	public AvlNode<K, V> getNearestHighestNode(K key) {
		AvlNode<K, V> currNode = root;

		while (currNode != null) {
			int comp = comparator.compare(key, currNode.key);
			if (comp < 0) {
				if (currNode.right != null)
					currNode = currNode.right;
				else
					return currNode;
			} else if (comp > 0) {
				if (currNode.left != null)
					currNode = currNode.left;
				else {
					AvlNode<K, V> parent = currNode.parent;
					AvlNode<K, V> child = currNode;
					while (parent != null && child == parent.left) {
						child = parent;
						parent = parent.parent;
					}
					return parent;
				}
			} else 
				return currNode;
		}
		return null;
	}

	public AvlNode<K, V> getLastNode() {
		AvlNode<K, V> last = root;
		if (last != null) {
			while (last.right != null)
				last = last.right;
		}
		return last;
	}

	public void toXml(Element parent) {
		Element avlGTreeNode = parent.getOwnerDocument().createElement("AvlGTree");

		avlGTreeNode.setAttribute("cardinality", Integer.toString(size));
		avlGTreeNode.setAttribute("height", Integer.toString(height()));
		avlGTreeNode.setAttribute("maxImbalance", Integer.toString(maxImbalance));

		if (root != null)
			root.toXml(avlGTreeNode);
		else
			avlGTreeNode.appendChild(parent.getOwnerDocument().createElement("emptyNode"));
		parent.appendChild(avlGTreeNode);
	}

	@Override
	public SortedMap<K, V> headMap(K toKey) {
		return new SubMap(firstKey(), toKey);
	}

	@Override
	public SortedMap<K, V> tailMap(K fromKey) {
		return new SubMap(fromKey, lastKey());
	}

	protected class EntrySet extends AbstractSet<Entry<K, V>> {
		@Override
		public Iterator<Entry<K, V>> iterator() {
			return new EntrySetIterator(getFirstNode());
		}

		@Override
		public int size() {
			return AvlGTree.this.size();
		}

	}

	protected class EntrySetIterator implements Iterator<Entry<K, V>> {

		int expectedModCount;
		AvlNode<K, V> next;

		public EntrySetIterator(AvlNode<K, V> first) {
			expectedModCount = modCount;
			next = first;
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public AvlNode<K, V> next() {
			AvlNode<K, V> e = next;
			if (e == null)
				throw new NoSuchElementException();
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			next = successor(e);
			return e;
		}
	}

	protected class SubMap extends AbstractMap<K, V> implements SortedMap<K, V> {

		K fromKey, toKey;

		public SubMap(K fromKey, K toKey) {
			if (comparator.compare(fromKey, toKey) > 0)
				throw new IllegalArgumentException("fromKey > toKey");

			this.fromKey = fromKey;

			this.toKey = toKey;
		}

		@Override
		public int size() {
			int size = 0;
			Iterator<K> iter = this.keySet().iterator();

			while (iter.hasNext()) {
				size++;
				iter.next();
			}

			return size;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@SuppressWarnings("unchecked")
		boolean inRange(Object key) {
			return comparator.compare((K) key, fromKey) >= 0 && comparator.compare(toKey, (K) key) >= 0;
		}

		@Override
		public boolean containsKey(Object key) {
			return inRange(key) && AvlGTree.this.containsKey(key);
		}

		@Override
		public V get(Object key) {
			return inRange(key) ? AvlGTree.this.get(key) : null;
		}

		@Override
		public V put(K key, V value) {
			return inRange(key) ? AvlGTree.this.put(key, value) : null;
		}

		@Override
		public V remove(Object key) {
			return inRange(key) ? AvlGTree.this.remove(key) : null;
		}

		@Override
		public void clear() {
			AvlGTree.this.clear();
		}

		@Override
		public Comparator<? super K> comparator() {
			return AvlGTree.this.comparator;
		}

		@Override
		public SortedMap<K, V> subMap(K fromKey, K toKey) {
			return new SubMap(fromKey, toKey);
		}

		@Override
		public SortedMap<K, V> headMap(K toKey) {
			return new SubMap(this.fromKey, toKey);
		}

		@Override
		public SortedMap<K, V> tailMap(K fromKey) {
			return new SubMap(fromKey, this.toKey);
		}

		@Override
		public K firstKey() {
			return fromKey;
		}

		@Override
		public K lastKey() {
			return toKey;
		}

		@Override
		public Set<Entry<K, V>> entrySet() {
			return new SubMapEntrySet();
		}

		protected class SubMapEntrySet extends AbstractSet<Entry<K, V>> {
			@Override
			public Iterator<Entry<K, V>> iterator() {
				return new SubMapIterator(AvlGTree.this.getNearestLowestNode(fromKey), AvlGTree.this.getNearestLowestNode(toKey));
			}

			@Override
			public int size() {
				int size = 0;

				Iterator<Entry<K, V>> iter = iterator();
				while (iter.hasNext()) {
					size++;
					iter.next();
				}

				return size;
			}
		}

		protected class SubMapIterator implements Iterator<Entry<K, V>> {
			private final Object UNBOUNDED = new Object();
			AvlNode<K, V> next;
			int expectedModCount;
			Object fenceKey;

			public SubMapIterator(AvlNode<K, V> firstNode, AvlNode<K, V> fenceNode) {
				expectedModCount = AvlGTree.this.modCount;
				next = firstNode;
				
				fenceKey = fenceNode != null ? fenceNode.key : UNBOUNDED;
			}

			@Override
			public boolean hasNext() {
				return next != null && next.key != fenceKey;
			}

			@Override
			public AvlNode<K, V> next() {
				AvlNode<K, V> e = next;
				if (e == null || e.key == fenceKey)
					throw new NoSuchElementException();
				if (AvlGTree.this.modCount != expectedModCount)
					throw new ConcurrentModificationException();
				next = successor(e);
				return e;
			}

		}

	}
}

