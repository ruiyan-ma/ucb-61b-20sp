/**
 * Disjoint sets of contiguous integers that allows: 
 * (a) finding whether two integers are in the same set and 
 * (b) unioning two sets together.
 * 
 * At any given time, for a structure partitioning the integers 1 to N,
 * into sets, each set is represented by a unique member of that
 * set, called its representative.
 *
 * @author ryan ma
 */
public class UnionFind {

	/**
	 * A union-find structure consisting of the sets { 1 }, { 2 }, ... { N }.
	 */
	public UnionFind(int N) {
		_parent = new int[N + 1];
		_size = new int[N + 1];
		for (int i = 1; i < N + 1; i++) {
			_parent[i] = i;
			_size[i] = 1;
		}
	}

	/**
	 * Implement path compression.
	 * Compress the path from V to ROOT.
	 */
	private void pathCompress(int v, int root) {
		while (_parent[v] != root) {
			v = _parent[v];
			_parent[v] = root;
		}
	}

	/**
	 * Return the representative of the set currently containing V.
	 * Assumes V is contained in one of the sets.
	 */
	public int find(int v) {
		int root = v;
		while (_parent[root] != root) {
			root = _parent[root];
		}
		pathCompress(v, root);
		return root;
	}

	/**
	 * Return true iff U and V are in the same set.
	 */
	public boolean samePartition(int u, int v) {
		return find(u) == find(v);
	}

	/**
	 * Union U and V into a single set, returning its representative.
	 */
	public int union(int u, int v) {
		if (samePartition(u, v)) {
			return find(u);
		} else {
			int smallSet, bigSet;
			int rootU = find(u), rootV = find(v);
			int sizeU = _size[rootU], sizeV = _size[rootV];
			if (sizeU > sizeV) {
				bigSet = rootU;
				smallSet = rootV;
			} else {
				bigSet = rootV;
				smallSet = rootU;
			}
			_parent[smallSet] = _parent[bigSet];
			_size[bigSet] += _size[smallSet];
			return bigSet;
		}
	}

	private final int[] _parent;
	private final int[] _size;
}
