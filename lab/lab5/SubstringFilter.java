/**
 * TableFilter to filter for containing substrings.
 *
 * @author Matthew Owen
 */
public class SubstringFilter extends TableFilter {

	/**
	 * Constructor method.
	 */
	public SubstringFilter(Table input, String colName, String subStr) {
		super(input);
		this._targetCol = input.colNameToIndex(colName);
		this._subStr = subStr;
	}

	@Override
	protected boolean keep() {
		return this.candidateNext().getValue(_targetCol).contains(this._subStr);
	}

	/**
	 * The target column's index.
	 */
	private int _targetCol;

	/**
	 * The reference substring.
	 */
	private String _subStr;

}
