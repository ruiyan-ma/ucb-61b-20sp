import java.io.IOException;
import java.io.Reader;

/**
 * Translating Reader: a stream that is a translation of an
 * existing reader.
 * <p>
 * TrReader继承Reader类，该类的实现可以参考StringReader类。
 *
 * @author ryan ma
 */

public class TrReader extends Reader {
	/*  A new TrReader that produces the stream of characters produced
	 *  by STR, converting all characters that occur in FROM to the
	 *  corresponding characters in TO.  That is, change occurrences of
	 *  FROM.charAt(i) to TO.charAt(i), for all i, leaving other characters
	 *  in STR unchanged.  FROM and TO must have the same length. */
	public TrReader(Reader str, String from, String to) {
		assert from != null;
		assert to != null;
		assert from.length() == to.length();

		_reader = str;
		_from = from;
		_to = to;
	}

	@Override
	public void close() throws IOException {
		_reader.close();
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		/* Java Reader官方文档中关于 read(cbuf, off, len) 的描述：
		 * Reads characters into a portion of an array.
		 * Returns the number of characters read, or -1 if the end
		 * of the stream has been reached.
		 * Return -1 only when you haven't read anything yet reach
		 * the end of the stream.
		 * TrReader类中重写read(cbuf, off, len)所需增加的额外功能：
		 * 将字符写入cbuf的过程中，将出现在_from中的字符均替换为_to中
		 * 对应位置的字符 */
		if ((off < 0) || (off > cbuf.length) || (len < 0) ||
					((off + len) > cbuf.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}
		int numChars = _reader.read(cbuf, off, len);
		for (int i = 0; i < cbuf.length; i++) {
			char c = cbuf[i];
			int index = _from.indexOf(c);
			if (index != -1) {
				cbuf[i] = _to.charAt(index);
			}
		}
		return numChars;
	}

	/**
	 * The Reader.
	 */
	Reader _reader;

	/**
	 * The from.
	 */
	String _from;

	/**
	 * The to.
	 */
	String _to;

}
