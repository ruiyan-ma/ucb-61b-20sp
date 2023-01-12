/**
 * TableFilter to filter for entries equal to a given string.
 *
 * @author Matthew Owen
 */
public class EqualityFilter extends TableFilter {

	/**
	 * Constructor method.
	 */
	public EqualityFilter(Table input, String colName, String match) {
		super(input);
		this._targetCol = input.colNameToIndex(colName);
		this._expected = match;
	}

	@Override
	protected boolean keep() {
		return this.candidateNext().getValue(this._targetCol).equals(this._expected);
	}

	/**
	 * The target column index.
	 */
	private int _targetCol;

	/**
	 * The expected entry.
	 */
	private String _expected;

}
