package map;

public class LinkedListMap<K extends Comparable<K>, V>
		implements SimpleMap<K, V> {
	/* K, V are type parameters in this class. */

	@Override
	public void put(K key, V value) {
		if (_head != null) {
			MapNode ptr = _head;
			while (ptr._next != null) {
				if (ptr._key.equals(key)) {
					ptr._value = value;
					return;
				}
				ptr = ptr._next;
			}
			ptr._next = new MapNode(key, value, null);
		}
		_head = new MapNode(key, value, _head);
	}

	@Override
	public V get(K key) {
		MapNode ptr = _head;
		while (ptr != null) {
			if (ptr._key.equals(key)) {
				return ptr._value;
			}
			ptr = ptr._next;
		}
		return null;
	}

	@Override
	public void clear() {
		_head = null;
	}

	private MapNode _head;

	private class MapNode {
		/**
		 * Inner class MapNode. Use a linked list to implement
		 * a Map structure.
		 */
		private MapNode(K key, V value, MapNode next) {
			_key = key;
			_value = value;
			_next = next;
		}

		public String toString() {
			return "(" + _key.toString() + " -> " + _value.toString() + ")";
		}

		private MapNode _next;
		private K _key;
		private V _value;
	}
}
