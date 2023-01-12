/**
 * TableFilter to filter for entries greater than a given string.
 *
 * @author Matthew Owen
 */
public class GreaterThanFilter extends TableFilter {

	/**
	 * Constructor method.
	 */
	public GreaterThanFilter(Table input, String colName, String ref) {
		super(input);
		this._targetCol = input.colNameToIndex(colName);
		this._ref = ref;
	}

	@Override
	protected boolean keep() {
		return this.candidateNext().getValue(this._targetCol).compareTo(this._ref) > 0;
	}

	/**
	 * The target column index.
	 */
	private int _targetCol;

	/**
	 * The reference string.
	 */
	private String _ref;

}
