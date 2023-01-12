import java.util.Arrays;
import java.util.Comparator;

/**
 * Minimal spanning tree utility.
 *
 * @author ryan ma
 */
public class MST {

	/**
	 * Given an undirected, weighted, connected graph whose vertices are
	 * numbered 1 to V, and an array E of edges, returns an array of edges
	 * in E that form a minimal spanning tree of the input graph.
	 * Each edge in E is a three-element int array of the form (u, v, w),
	 * where 0 < u < v <= V are vertex numbers, and 0 <= w is the weight
	 * of the edge. The result is an array containing edges from E.
	 * Neither E nor the arrays in it may be modified.  There may be
	 * multiple edges between vertices.  The objects in the returned array
	 * are a subset of those in E (they do not include copies of the
	 * original edges, just the original edges themselves.)
	 */
	public static int[][] mst(int V, int[][] E) {
		E = Arrays.copyOf(E, E.length);
		// Copy E to a new array to avoid modifying E.
		int numEdgesInResult = V - 1;
		// FIXME: how many edges should there be in our MST?
		// Answer: need V-1 edges to connect V vertices.
		int[][] result = new int[numEdgesInResult][];
		// FIXME: what other data structures do I need?
		Arrays.sort(E, EDGE_WEIGHT_COMPARATOR);
		UnionFind disjointSet = new UnionFind(V);
		// FIXME: do Kruskal's Algorithm
		int indexOfResult = 0;
		for (int[] ints : E) {
			int u = ints[0], v = ints[1];
			if (!disjointSet.samePartition(u, v)) {
				result[indexOfResult] = ints;
				indexOfResult++;
				disjointSet.union(u, v);
			}
		}
		return result;
	}

	/**
	 * An ordering of edges by weight.
	 */
	private static final Comparator<int[]> EDGE_WEIGHT_COMPARATOR =
			Comparator.comparingInt(e0 -> e0[2]);
}
