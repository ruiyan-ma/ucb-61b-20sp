import java.util.*;

/**
 * A set of String values, implementing with a hash table, as HashSet in Java.
 *
 * @author ryan ma
 */

class ECHashStringSet implements StringSet {

	/**
	 * Default constructor function.
	 */
	public ECHashStringSet() {
		hashTable = new Table(5);
	}

	/**
	 * Constructor function.
	 *
	 * @param size: the size of this table.
	 */
	public ECHashStringSet(int size) {
		hashTable = new Table(size);
	}

	/**
	 * Update and check if the load factor is too high.
	 * If it is, extend the table.
	 */
	private void updateLoadFactor() {
		loadFactor = (loadFactor * hashTable.size() + 1) / hashTable.size();
		if (loadFactor >= 5) {
			loadFactor = 0;
			int itemNum=0;
			Table newTable = new Table(hashTable.size() * 2);
			for(LinkedList<String> list : hashTable) {
				for(String str : list) {
					newTable.put(str, hash(str, newTable.size()));
					itemNum++;
				}
			}
			loadFactor = (double) itemNum/newTable.size();
			hashTable = newTable;
		}
	}

	/**
	 * The hash function for this hash table.
	 */
	int hash(String s, int size) {
		return (s.hashCode() & 0x7fffffff) % size;
	}

	@Override
	public void put(String s) {
		hashTable.put(s, hash(s, hashTable.size()));
		updateLoadFactor();
	}

	@Override
	public boolean contains(String s) {
		return hashTable.contains(s, hash(s, hashTable.size()));
	}

	@Override
	public List<String> asList() {
		return hashTable.asList();
	}

	/**
	 * The nested class Table.
	 * This class is used to provide an array of list.
	 */
	private static class Table implements Iterable<LinkedList<String>>{

		/**
		 * Constructor function.
		 */
		Table(int size) {
			table = new Vector<>(size);
			for (int i = 0; i < size; i++) {
				table.add(new LinkedList<>());
			}
		}

		/**
		 * Return the size of this hash table.
		 */
		int size() {
			return table.size();
		}

		/**
		 * Put a String in table[INDEX].
		 */
		void put(String s, int index) {
			table.get(index).add(s);
		}

		/**
		 * Check if this String is in table[INDEX].
		 */
		boolean contains(String s, int index) {
			return table.get(index).contains(s);
		}

		/**
		 * Return a ArrayList contains all the Strings stored in this table.
		 */
		List<String> asList() {
			ArrayList<String> list = new ArrayList<>();
			for (int i = 0; i < size(); i++) {
				list.addAll(table.get(i));
			}
			return list;
		}

		/**
		 * The table.
		 */
		private Vector<LinkedList<String>> table;

		@Override
		public Iterator<LinkedList<String>> iterator() {
			return table.iterator();
		}

	}

	/**
	 * Load factor.
	 */
	private double loadFactor = 0;

	/**
	 * Hash table.
	 */
	private Table hashTable;

}
