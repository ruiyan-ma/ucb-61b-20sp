import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Simple DataBase System which allows for joins and filtering.
 *
 * @author ryan ma
 */

public class Table implements Iterable<Table.TableRow> {

	/**
	 * Initialize a Table without a header or any rows.
	 */
	private Table() {
		_columnMap = new HashMap<>();
		_rows = new ArrayList<>();
	}

	/**
	 * Initialize a Table from a file.
	 */
	public Table(String file) {
		/* Take this file's first line as column names.
		 * Then store column names into _columnMap, implements the conversion
		 * from column name to column index.
		 * From the second line of this file, take every line as a TableRow
		 * and add them into this table. */
		this();
		try {
			File f = new File(file);
			Scanner reader = new Scanner(f);
			String headerRow = reader.nextLine();
			initColumnMap(headerRow);
			while (reader.hasNextLine()) {
				String dataRow = reader.nextLine();
				addRow(dataRow);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			throw new TableException(e.getMessage());
		}
	}

	/**
	 * Initialize a mapping from column name to column index.
	 */
	private void initColumnMap(String headerRow) {
		/* \s means a whitespace character (See in Pattern class). */
		initColumnMap(
				Arrays.asList(headerRow.trim().split("[\\s]*,[\\s]*", -1)));
	}

	/**
	 * Initialize a mapping from column name to column index.
	 */
	private void initColumnMap(List<String> headerList) {
		for (int i = 0; i < headerList.size(); i++) {
			_columnMap.put(headerList.get(i), i);
		}
	}

	/**
	 * Add a row to this. Errors if the data is not the correct size.
	 */
	private void addRow(String dataRow) {
		addRow(new TableRow(
				Arrays.asList(dataRow.trim().split("[\\s]*," + "[\\s]*", -1))));
	}

	/**
	 * Add a row to this. Errors if the data is not the correct size.
	 */
	private void addRow(TableRow row) {
		if (row.size() != numColumns()) {
			throw new TableException("Row contains incorrect number of values");
		}
		_rows.add(row);
	}

	/**
	 * Returns a string representation of the column header of this
	 */
	private String headerRow() {
		/* static String join(CharSequence delimiter, CharSequence elements):
		 * Returns a new String composed of copies of the CharSequence ELEMENTS
		 * joined together with a copy of the specified delimiter.
		 * For example:
		 * String message = String.join("-", "Java", "is", "cool");
		 * message returned is: "Java-is-cool" */
		return String.join(",", headerList());
	}

	/**
	 * Returns the int corresponding to the column named colName.
	 */
	public int colNameToIndex(String colName) {
		return _columnMap.get(colName);
	}

	/**
	 * Returns the number of columns in this.
	 */
	public int numColumns() {
		return _columnMap.size();
	}

	/**
	 * Returns the number of rows in this.
	 */
	public int numRows() {
		return _rows.size();
	}

	/**
	 * Returns the list of columns in this table, in the correct order.
	 * Implementation here uses streams to sort the values in the keySet (the
	 * column names) by their values in the _columnMap(their column indices).
	 * The sorted stream is then collected into a List to be returned.
	 */
	public List<String> headerList() {
		return _columnMap.keySet().stream().sorted(
				Comparator.comparingInt(_columnMap::get)).collect(
				Collectors.toList());
	}

	/**
	 * Return the ith row of this.
	 */
	public TableRow getRow(int i) {
		return _rows.get(i);
	}

	/**
	 * Returns the result of doing a cross join on two tables.
	 * This implementation first creates a new header row for the joined
	 * table which contains the same names as before prepended with "t1." or
	 * "t2.". A new table is initialized and the column map is initialized.
	 * Then a JoinIterator is used to add each of the joined rows into the
	 * new table.
	 */
	public static Table join(Table t1, Table t2) {
		/* Collection: default Stream<E> stream()
		 * returns a sequential Stream with this collection as its source.
		 * Stream: <R> Stream<R> map(Function<? super T,? extends R> mapper)
		 * returns a stream consisting of the results of applying the given
		 * function to the elements of this stream.
		 * */
		List<String> t1HeaderList = t1.headerList().stream().map(
				(x) -> "t1." + x).collect(Collectors.toList());
		List<String> t2HeaderList = t2.headerList().stream().map(
				(x) -> "t2." + x).collect(Collectors.toList());
		List<String> headerlist = new ArrayList<>();
		headerlist.addAll(t1HeaderList);
		headerlist.addAll(t2HeaderList);

		Table joinedTable = new Table();
		joinedTable.initColumnMap(headerlist);

		for (TableRow row : new JoinIterator(t1, t2)) {
			// Enhanced For Loop uses the iterator inside this collection to
			// traverse items.
			joinedTable.addRow(row);
		}

		return joinedTable;
	}

	/**
	 * Returns the result of doing a filtering a table using filter.
	 * This implementation first creates a new table is initialized and the
	 * column map is initialized. Then the filter is used to iterate over the
	 * filtered rows which are then added to the new table.
	 */
	public static Table filter(TableFilter filter) {
		Table filteredTable = new Table();
		filteredTable.initColumnMap(filter.headerList());

		for (TableRow row : filter) {
			filteredTable.addRow(row);
		}

		return filteredTable;
	}

	@Override
	public String toString() {
		return headerRow() + "\n" + _rows.stream().map(
				TableRow::toString).collect(Collectors.joining("\n"));
	}

	/**
	 * Returns an iterator over the rows of the Table
	 */
	public Iterator<TableRow> iterator() {
		return _rows.iterator();
	}

	/**
	 * Map which stores column name to column index key-value pairs.
	 */
	private final HashMap<String, Integer> _columnMap;

	/**
	 * List which stores TableRows.
	 */
	private final ArrayList<TableRow> _rows;

	/**
	 * Iterator over TableRows which returns joined rows from the cartesian
	 * product of two tables.
	 */
	private static class JoinIterator implements Iterator<TableRow>, Iterable<TableRow> {

		private JoinIterator(Table t1, Table t2) {
			_table2 = t2;
			_tableIter1 = t1.iterator();
			_tableIter2 = t2.iterator();
			_currRow1 = _tableIter1.next();
		}

		@Override
		public boolean hasNext() {
			/* This method overrides hasNext() in Iterator interface.
			 * We use JoinIterator to provide us with a way to traverse the data
			 * structure. We use it to get what we want when we need to get a
			 * item from the joined table. JoinIterator does not really join two
			 * tables into a larger one, instead it use these two tables'
			 * iterators to traverse them and give us what we want.
			 *
			 * Every time hasNext() creates a new _nextRow for next() to
			 * return when _nextRow is null, and next() will return the
			 * _nextRow that hasNext() provides for it and then set _nextRow
			 * to null for hasNext(). _nextRow is the signal which
			 * communicate these two methods. */
			if (_nextRow == null) {
				if (this._tableIter2.hasNext()) {
					this._nextRow = TableRow.joinRows(this._currRow1,
							this._tableIter2.next());
				} else if (this._tableIter1.hasNext()) {
					this._currRow1 = this._tableIter1.next();
					this._tableIter2 = this._table2.iterator();
					this._nextRow = TableRow.joinRows(this._currRow1,
							this._tableIter2.next());
				}
			}
			return _nextRow != null;
		}

		@Override
		public TableRow next() {
			/* This method overrides next() in Iterator interface. */
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			TableRow returnRow = _nextRow;
			_nextRow = null;
			return returnRow;
		}

		@Override
		public final Iterator<Table.TableRow> iterator() {
			/* This method overrides iterator() method in Iterable interface. */
			return this;
		}

		/**
		 * Iterator over the rows of the first table.
		 */
		private Iterator<TableRow> _tableIter1;

		/**
		 * Iterator over the rows of the second table.
		 */
		private Iterator<TableRow> _tableIter2;

		/**
		 * The next joined row to be returned by .next()
		 */
		private TableRow _nextRow;

		/**
		 * The current row from the first table.
		 */
		private TableRow _currRow1;

		/**
		 * Store table2 for convenient creation of iterators when we "reset".
		 */
		private final Table _table2;

	}

	/**
	 * Class that represents a single row in a Table.
	 */
	public static class TableRow {

		/**
		 * Constructor method. Use an ArrayList<String> to implement a
		 * TableRow.
		 */
		public TableRow(List<String> data) {
			_values = new ArrayList<>();
			_values.addAll(data);
		}

		/**
		 * Returns the ith value in this TableRow
		 */
		public String getValue(int i) {
			return _values.get(i);
		}

		/**
		 * Returns a TableRow which is the result of joining two table rows
		 */
		public static TableRow joinRows(TableRow tr1, TableRow tr2) {
			/* Example:
			 * TableRow tr1._value contains "sky" and "blue".
			 * TableRow tr2._value contains "sky" and "no".
			 * So when we join these two rows, we get a ArrayList which
			 * contains "sky", "blue", "sky", "no". */
			ArrayList<String> newData = new ArrayList<>();
			newData.addAll(tr1._values);
			newData.addAll(tr2._values);
			return new TableRow(newData);
		}

		/**
		 * Return the size of this TableRow.
		 */
		public int size() {
			return _values.size();
		}

		@Override
		public String toString() {
			return String.join(",", _values);
		}

		/**
		 * List of string values in this TableRow.
		 */
		private final ArrayList<String> _values;

	}

	/**
	 * Class that represents exceptions in the creation and manipulation of
	 * Tables.
	 */
	public static class TableException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public TableException(String errorMessage) {
			super(errorMessage);
		}
	}

}
