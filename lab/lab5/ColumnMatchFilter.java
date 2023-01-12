/**
 * TableFilter to filter for entries whose two columns match.
 *
 * @author Matthew Owen
 */
public class ColumnMatchFilter extends TableFilter {

	/**
	 * Constructor method.
	 */
	public ColumnMatchFilter(Table input, String colName1, String colName2) {
		super(input);
		// Get the column index from these two column names.
		this._col1 = input.colNameToIndex(colName1);
		this._col2 = input.colNameToIndex(colName2);
	}

	@Override
	protected boolean keep() {
		return this.candidateNext().getValue(_col1).equals(this._next.getValue(_col2));
	}

	/**
	 * The first column's index.
	 */
	private int _col1;

	/**
	 * The second column's index.
	 */
	private int _col2;

}
