package cmsc420.sortedmap;

import java.util.Map;

import org.w3c.dom.Element;

public class AvlNode<K, V> implements Map.Entry<K, V>{
	K key;      // The data in the node
	V value;
	AvlNode<K ,V> left;         // Left child
	AvlNode<K, V> right;        // Right child
	AvlNode<K, V> parent;
	int leftHeight, rightHeight;       // Height

	// Constructors
	AvlNode(K key, V value) {
		this(key, value, null, null);
	}

	AvlNode(K key, V value, AvlNode<K, V> lt, AvlNode<K, V> rt) {
		this.key  = key;
		this.value = value;
		left = lt;
		right = rt;
		leftHeight = 0;
		rightHeight = 0;
		parent = null;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		V oldValue = this.value;
		this.value = value;

		return oldValue;
	}

	public void toXml(Element parent) {
		Element node;

		node = parent.getOwnerDocument().createElement("node");
		node.setAttribute("key", key.toString());
		node.setAttribute("value", value.toString());

		if (left != null)
			left.toXml(node);
		else 
			node.appendChild(parent.getOwnerDocument().createElement("emptyChild"));

		if (right != null)
			right.toXml(node);
		else
			node.appendChild(parent.getOwnerDocument().createElement("emptyChild"));

		parent.appendChild(node);
	}

	public int getBalance() {
		return leftHeight - rightHeight;
	}

	public int getHeight() {
		return 1 + Math.max(rightHeight, leftHeight);
	}

	public void fixHeight() {
		if (parent == null)
			return;

		if (parent.left == this)
			parent.leftHeight = getHeight();
		else
			parent.rightHeight = getHeight();

		parent.fixHeight();
	}

	public int hashCode() {
		int keyHash = key == null ? 0 : key.hashCode();
		int valueHash = value == null ? 0 : value.hashCode();
		return keyHash ^ valueHash;
	}

	public String toString() {
		return key + "=" + value;
	}
}