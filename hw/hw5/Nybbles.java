/**
 * Represents an array of integers each in the range -8..7.
 * Such integers may be represented in 4 bits (called nybbles).
 *
 * @author ryan ma
 */

public class Nybbles {

	/**
	 * 任务：将一些范围在(-8,7)的小整数按0bxxxx的4bits形式表示
	 * 并将8个4bits合成一个32bits的int型整数存在数组中
	 * 第一个小整数存在0-3bit，第二个小整数存在4-7bit，以此类推.
	 * 如[-4,0,7,0,-2,3,0,1]转换为0001 0000 0101 1110 0000 0111 0000 1100
	 * => 274597644
	 * 每八个小整数合为一个int，最后剩下不足八个的合为一个int
	 *
	 * Maximum positive value of a Nybble.
	 */
	public static final int MAX_VALUE = 7;

	/**
	 * Return an array of size N.
	 * DON'T CHANGE THIS.
	 */
	public Nybbles(int N) {
		/* 数组只存储(N+7)/8个int，但实际上表示着N个小整数
		 * +7是为了将最后剩下不足8个的补成一个int */
		_data = new int[(N + 7) / 8];
		_n = N;
	}

	/**
	 * Return the size of THIS.
	 */
	public int size() {
		return _n;
	}

	/**
	 * Return the Kth integer in THIS array, numbering from 0.
	 * Assumes 0 <= K < N.
	 */
	public int get(int k) {
		if (k < 0 || k >= _n) {
			throw new IndexOutOfBoundsException();
		} else {
			int index = k / 8;
			/* 首先找到该整数存在哪个int中 */
			int subIndex = k % 8;
			/* 再找在该int中的哪段位置 */
			int elem = _data[index] >>> subIndex * 4;
			elem = elem & 0b1111;
			/* elem的范围是-8到7，如果elem>7则说明elem实际上是负数 */
			if (elem > 7) {
				return -((-elem) & 0b1111);
			} else {
				return elem;
			}
		}
	}

	/**
	 * Set the Kth integer in THIS array to VAL.  Assumes
	 * 0 <= K < _n and -8 <= VAL < 8.
	 */
	public void set(int k, int val) {
		if (k < 0 || k >= _n) {
			throw new IndexOutOfBoundsException();
		} else if (val < (-MAX_VALUE - 1) || val > MAX_VALUE) {
			throw new IllegalArgumentException();
		} else {
			int index = k / 8;
			int subIndex = k % 8;
			/* 先把这个位置的4bit取出来，将原数减去这部分 */
			int elem = _data[index] >>> subIndex * 4;
			elem = elem << subIndex * 4;
			_data[index] -= elem;
			/* 再把新的值加上，此处要分开处理val为正和为负 */
			val = val & 0b1111;
			val = val << subIndex * 4;
			_data[index] += val;
		}
	}

	/* DON'T CHANGE OR ADD TO THESE.*/
	/**
	 * Size of current array (in nybbles).
	 */
	private final int _n;

	/**
	 * The array data, packed 8 nybbles to an int.
	 */
	private final int[] _data;

}
