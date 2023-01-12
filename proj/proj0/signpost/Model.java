package signpost;

import java.util.*;

import static signpost.Place.PlaceList;
import static signpost.Place.pl;
import static signpost.Utils.badArgs;
import static signpost.Utils.deepCopy;

/**
 * The state of a Signpost puzzle. Each cell has coordinates (x, y),
 * where 0 <= x < width(), 0 <= y < height(). The upper-left corner of the
 * puzzle has coordinates (0, height() - 1), and the lower-right corner is
 * at (width() - 1, 0).
 *
 * <p>A constructor initializes the squares according to a particular solution.
 * A solution is an assignment of sequence numbers from 1 to size() == width()
 * height() to square positions so that squares with adjacent numbers are
 * separated by queen moves. A queen move is a move from one square to another
 * horizontally, vertically, or diagonally. The effect is to give each square
 * whose number in the solution is less than size() an arrow direction,
 * 1 <= d <= 8, indicating the direction of the next higher numbered square
 * in the solution: d * 45 degrees clockwise from straight up (i.e., toward
 * higher y coordinates). Thus, direction 1 is "northeast", 2 is "east",
 * ..., and 8 is "north". The highest-numbered square has direction 0.
 * Certain squares can have their values fixed to those in the solution.
 * 某些方格的值可以固定为solution中的值。
 *
 * <p>Initially, the only two squares with fixed values are those with the
 * lowest and highest sequence numbers in the solution. Before the puzzle is
 * presented to the user, however, the program fixes other numbers so as to
 * make the solution unique for the given arrows.
 * 在棋盘中指定某几个方格的sequence number，以确保解的唯一性
 *
 * <p>At any given time after initialization, a square whose value is not
 * fixed may have an unknown value, represented as 0, or a tentative(试探性的)
 * number (not necessarily that of the solution) between 1 and size(). Squares
 * may be connected together, indicating that their sequence numbers (unknown
 * or not) are consecutive(连续的).
 *
 * <p>When square S0 is connected to S1, we say that S1 is the successor of S0,
 * and S0 is the predecessor of S1. Sequences of connected squares with
 * as-yet(至今仍) unknown values (denoted by 0) form a group, identified by a
 * unique group number. Numbered cells (whether linked or not) are in group 0.
 * Unnumbered, unlinked cells are in group -1. On the board displayed to the
 * user, cells in the same group indicate their grouping and sequence with
 * labels such as "a", "a+1", "a+2", etc., with a different letter for each
 * different group. The first square in a group is called the head of the
 * group. Each unnumbered square points to the head of its group (if the square
 * is unlinked, it points to itself).
 * 已连接但未分配值的方格组成一个组，每个组组号唯一
 * 已连接的组，组中sq的组号以链首_head的组号为准 （见Sq类中的group函数）
 * 已分配值(无论是否连接)的方格组成一个组，组号为0.
 * 未连接并且未分配值的方格组成一个组，组号为-1.
 * 每个未分配值的方格指向自己所在组的head；未连接的方格指向自己
 *
 * <p>Squares are represented as objects of the inner class Sq (Model.Sq).
 * A Model object is itself iterable(可迭代的). That is, if M is of type Model,
 * one can write for (Model.Sq s : M) { ... } to sequence through all its
 * squares in unspecified order.
 *
 * <p>The puzzle is solved when all cells are contained in a single sequence
 * of consecutively numbered cells (therefore all in group 0) and all cells
 * with fixed sequence numbers appear at the corresponding position in that
 * sequence.
 *
 * @author shaw ma
 */
class Model implements Iterable<Model.Sq> {

	Model(int[][] solution) {
		if (solution.length == 0 | solution.length * solution[0].length < 2) {
			throw badArgs("must have at least 2 squares");
		}
		initialize(solution);
	}

	/**
	 * Initialize instance variable with SOLUTION.
	 */
	private void initialize(int[][] solution) {
		_width = solution.length;
		_height = solution[0].length;
		_allSuccessors = Place.successorCells(_width, _height);
		_solution = new int[_width][_height];
		deepCopy(solution, _solution);
		initSolnNumToPlace();
		initBoard();
		initAllSquare();
		setSuccessors();
		setPredecessor();
		_unconnected = size() - 1;
	}

	/**
	 * Initialize _solnNumToPlace.
	 */
	private void initSolnNumToPlace() {
		/* 构建_solnNumToPlace
		 * _solnNumToPlace需要从1存到size()，0空下 */
		_solnNumToPlace = new Place[size() + 1];
		for (int num = 1; num <= size(); num++) {
			boolean state = true;
			for (int i = 0; i < width() & state; i++) {
				for (int j = 0; j < height() & state; j++) {
					if (_solution[i][j] == num) {
						_solnNumToPlace[num] = pl(i, j);
						state = false;
					}
				}
			}
		}
		for (int i = 1; i <= size(); i++) {
			if (_solnNumToPlace[i] == null) {
				throw badArgs("_solnNumToPlace isn't complete.");
			}
		}
	}

	/**
	 * Initialize _board.
	 */
	private void initBoard() {
		/* 构建_board
		 * 1和last需单独处理. */
		_board = new Sq[width()][height()];
		int x = solnNumToPlace(1).x;
		int y = solnNumToPlace(1).y;
		_board[x][y] = new Sq(x, y, 1, true, arrowDirection(x, y), 0);
		x = solnNumToPlace(size()).x;
		y = solnNumToPlace(size()).y;
		_board[x][y] = new Sq(x, y, size(), true, arrowDirection(x, y), 0);
		for (int num = 2; num < size(); num++) {
			x = solnNumToPlace(num).x;
			y = solnNumToPlace(num).y;
			_board[x][y] = new Sq(x, y, 0, false, arrowDirection(x, y), -1);
		}
	}

	/**
	 * Initialize _allSquare.
	 */
	private void initAllSquare() {
		/* 构建_allSquares */
		for (Sq[] col : _board) {
			_allSquares.addAll(Arrays.asList(col));
		}
	}

	/**
	 * Set _successors.
	 */
	private void setSuccessors() {
		/* 设置_successors */
		for (int i = 0; i < width(); i++) {
			for (int j = 0; j < height(); j++) {
				_board[i][j]._successors = new PlaceList();
				int dir = _board[i][j].direction();
				for (Place pl : _allSuccessors[i][j][dir]) {
					_board[i][j]._successors.add(pl);
				}
			}
		}
	}

	/**
	 * Set _predecessors.
	 */
	private void setPredecessor() {
		/* 设置_predecessors
		 * 对于每个sq, 搜索整个_allSuccessors. */
		for (int i = 0; i < width(); i++) {
			for (int j = 0; j < height(); j++) {
				_board[i][j]._predecessors = new PlaceList();
				for (int x = 0; x < width(); x++) {
					for (int y = 0; y < height(); y++) {
						int dir = _board[x][y].direction();
						for (Place pl : _allSuccessors[x][y][dir]) {
							if (_board[i][j].pl.equals(pl)) {
								_board[i][j]._predecessors.add(_board[x][y].pl);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Initializes a copy of MODEL.
	 */
	Model(Model model) {
		copyVariables(model);
		copyBoard(model);
		copySuccessor(model);
	}

	/**
	 * Copy instance variables with MODEL.
	 */
	private void copyVariables(Model model) {
		_width = model.width();
		_height = model.height();
		_unconnected = model._unconnected;
		_solnNumToPlace = model._solnNumToPlace;
		_solution = model._solution;
		_usedGroups.addAll(model._usedGroups);
		_allSuccessors = model._allSuccessors;
	}

	/**
	 * Copy board and allSquare with MODEL.
	 */
	private void copyBoard(Model model) {
		/* FIXME:
		 * Initialize _board and _allSquares to contain copies of the
		 * the Sq objects in MODEL other than their _successor,
		 * _predecessor, and _head fields (which can't necessarily be
		 * set until all the Sq objects are first created.) */
		_board = new Sq[width()][height()];
		for (int i = 0; i < width(); i++) {
			for (int j = 0; j < height(); j++) {
				_board[i][j] = new Sq(model._board[i][j]);
			}
		}
		for (Sq[] col : _board) {
			Collections.addAll(_allSquares, col);
		}
	}

	/**
	 * Copy successor, predecessor and head with MODEL.
	 */
	private void copySuccessor(Model model) {
		/* FIXME:
		 * Once all the new Sq objects are in place, fill in their
		 * _successor, _predecessor, and _head fields.  For example,
		 * if in MODEL, the _successor field of the Sq at
		 * position (2, 3) pointed to the Sq in MODEL at position
		 * (4, 1), then the Sq at position (2, 3) in this copy
		 * will have a _successor field pointing to the Sq at
		 * position (4, 1) in this copy.  Be careful NOT to have
		 * any of these fields in the copy pointing at the old Sqs in
		 * MODEL. */
		for (int i = 0; i < width(); i++) {
			for (int j = 0; j < height(); j++) {
				/* _successor */
				_board[i][j]._successor = get(model._board[i][j].successor());
				/* _predecessor */
				_board[i][j]._predecessor =
						get(model._board[i][j].predecessor());
				/* _head */
				_board[i][j]._head = get(model._board[i][j].head());
			}
		}
	}

	/**
	 * Returns the width (number of columns of cells) of the board.
	 */
	final int width() {
		return _width;
	}

	/**
	 * Returns the height (number of rows of cells) of the board.
	 */
	final int height() {
		return _height;
	}

	/**
	 * Returns the number of cells (and thus, the sequence number
	 * of the final cell).
	 */
	final int size() {
		return _width * _height;
	}

	/**
	 * Returns true iff (X, Y) is a valid cell location.
	 */
	final boolean isCell(int x, int y) {
		return 0 <= x && x < width() && 0 <= y && y < height();
	}

	/**
	 * Remove all connections and non-fixed sequence numbers.
	 */
	void restart() {
		for (Sq sq : this) {
			sq.disconnect();
		}
		assert _unconnected == _width * _height - 1;
	}

	/**
	 * Return the number array that solves the current puzzle. The result
	 * must not be subsequently modified.
	 */
	final int[][] solution() {
		return _solution;
	}

	/**
	 * Return the position of the cell with sequence number N in this
	 * board's * solution.
	 */
	Place solnNumToPlace(int n) {
		/* _solnNumToPlace[]存储按solution seqNum排序的place */
		return _solnNumToPlace[n];
	}

	/**
	 * Return the Sq with sequence number N in this board's solution.
	 */
	Sq solnNumToSq(int n) {
		/* get返回一个cell（一个sq） */
		return get(solnNumToPlace(n));
	}

	/**
	 * Return the current number of unconnected cells.
	 */
	final int unconnected() {
		return _unconnected;
	}

	/**
	 * Returns true iff the puzzle is solved.
	 */
	final boolean solved() {
		/* 因为每次连接是有条件的，只要全都连上就解决问题了。 */
		return _unconnected == 0;
	}

	/**
	 * Return the cell at (X, Y).
	 */
	final Sq get(int x, int y) {
		/* Sq[][] _board按position存储所有sq */
		return _board[x][y];
	}

	/**
	 * Return the cell at P.
	 */
	final Sq get(Place p) {
		return p == null ? null : _board[p.x][p.y];
	}

	/**
	 * Return the cell at the same position as SQ (generally from another
	 * board), or null if SQ is
	 * null.
	 */
	final Sq get(Sq sq) {
		return sq == null ? null : _board[sq.x][sq.y];
	}

	/**
	 * Connect all numbered cells with successive numbers that as yet are
	 * unconnected and are separated by a queen move. Returns true iff any
	 * changes were made.
	 */
	boolean autoConnect() {
		/* 因为是遍历所有Sq，succ和predec查一个就够了 */
		boolean change = false;
		for (int i = 0; i < width(); i++) {
			for (int j = 0; j < height(); j++) {
				if (_board[i][j].sequenceNum() != 0) {
					for (Place pl : _board[i][j].successors()) {
						if (get(pl).sequenceNum() ==
									_board[i][j].sequenceNum() + 1) {
							_board[i][j].connect(get(pl));
							change = true;
						}
					}
				}
			}
		}
		return change;
	}

	/**
	 * Sets the numbers in this board's squares to the solution from which
	 * this board was last initialized by the constructor.
	 */
	void solve() {
		for (int i = 0; i < width(); i++) {
			for (int j = 0; j < height(); j++) {
				_board[i][j]._sequenceNum = _solution[i][j];
			}
		}
		autoConnect();
		_unconnected = 0;
	}

	/**
	 * Return the direction from cell (X, Y) in the solution to its successor,
	 * or 0 if it has none.
	 */
	private int arrowDirection(int x, int y) {
		int seq0 = _solution[x][y];
		Place pl0 = solnNumToPlace(seq0);
		if (seq0 != size()) {
			Place pl1 = solnNumToPlace(seq0 + 1);
			return pl0.dirOf(pl1);
		} else {
			return 0;
		}
	}

	/**
	 * Return a new, currently unused group number > 0. Selects the lowest not
	 * currently in use.
	 */
	private int newGroup() {
		for (int i = 1; true; i += 1) {
			if (_usedGroups.add(i)) {
				return i;
			}
		}
	}

	/**
	 * Indicate that group number GROUP is no longer in use.
	 */
	private void releaseGroup(int group) {
		_usedGroups.remove(group);
	}

	/**
	 * Combine the groups G1 and G2. Return the resulting group. Assumes
	 * G1 != 0 and G2!= 0 and G1 != G2.
	 */
	private int joinGroups(int g1, int g2) {
		/* -1组是未连接未分配值的组 */
		assert (g1 != 0 && g2 != 0);
		if (g1 == -1 && g2 == -1) {
			return newGroup();
		} else if (g1 == -1) {
			return g2;
		} else if (g2 == -1) {
			return g1;
		} else if (g1 < g2) {
			releaseGroup(g2);
			return g1;
		} else {
			releaseGroup(g1);
			return g2;
		}
	}

	@Override
	public Iterator<Sq> iterator() {
		return _allSquares.iterator();
	}

	@Override
	public String toString() {
		String hline;
		hline = "+";
		for (int x = 0; x < _width; x += 1) {
			hline += "------+";
		}

		Formatter out = new Formatter();
		for (int y = _height - 1; y >= 0; y -= 1) {
			out.format("%s%n", hline);
			out.format("|");
			for (int x = 0; x < _width; x += 1) {
				Sq sq = get(x, y);
				if (sq.hasFixedNum()) {
					out.format("+%-5s|", sq.seqText());
				} else {
					out.format("%-6s|", sq.seqText());
				}
			}
			out.format("%n|");
			for (int x = 0; x < _width; x += 1) {
				Sq sq = get(x, y);
				if (sq.predecessor() == null && sq.sequenceNum() != 1) {
					out.format(".");
				} else {
					out.format(" ");
				}
				if (sq.successor() == null && sq.sequenceNum() != size()) {
					out.format("o ");
				} else {
					out.format("  ");
				}
				out.format("%s |", ARROWS[sq.direction()]);
			}
			out.format("%n");
		}
		out.format(hline);
		return out.toString();
	}

	@Override
	public boolean equals(Object obj) {
		Model model = (Model) obj;
		return (_unconnected == model._unconnected
						&& _width == model._width
						&& _height == model._height
						&& Arrays.deepEquals(_solution, model._solution)
						&& Arrays.deepEquals(_board, model._board));
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(_solution) * Arrays.deepHashCode(_board);
	}

	/**
	 * Sq类：Model中的嵌套类 每个已经连接起来的的Sq组都是一个双向链表，每个Sq连
	 * 接其 predecessor和successor；连接起来的Sq链表中的每个Sq都用_head变量来存
	 * 储链首，用_group 来存储group number； 每个Sq对象代表棋盘上的一个小方格
	 * Represents a square on the board.
	 */
	final class Sq {
		/**
		 * A square at (X0, Y0) with arrow in direction DIR (0 if not set),
		 * group number GROUP, sequence number SEQUENCENUM (0 if none initially
		 * assigned), which is fixed iff FIXED.
		 */
		Sq(int x0, int y0, int sequenceNum, boolean fixed, int dir, int group) {
			/* _head是Sq链表中的链首
			 * _group储存组号 */
			x = x0;
			y = y0;
			pl = pl(x, y);
			_hasFixedNum = fixed;
			_sequenceNum = sequenceNum;
			_dir = dir;
			_head = this;
			_group = group;
		}

		/**
		 * A copy of OTHER, excluding head, successor, and predecessor.
		 */
		Sq(Sq other) {
			this(other.x, other.y, other._sequenceNum,
					other._hasFixedNum, other._dir, other._group);
			_successor = _predecessor = null;
			_head = this;
			_successors = other._successors;
			_predecessors = other._predecessors;
		}

		/**
		 * Return this square's current sequence number, or 0 if none
		 * assigned.
		 */
		int sequenceNum() {
			return _sequenceNum;
		}

		/**
		 * Fix this square's current sequence number at N>0. It is an error if
		 * this square's number is
		 * not initially 0 or N.
		 */
		void setFixedNum(int n) {
			/* 如果sequenceNum=0就setNum, 同时设定_hasFixedNum为true
			 * 如果sequenceNum=n说明已经set过num了，什么都不用做 */
			if (n == 0 || (_sequenceNum != 0 && _sequenceNum != n)) {
				throw badArgs("sequence number may not be fixed");
			}
			_hasFixedNum = true;
			if (_sequenceNum == n) {
				return;
			} else {
				/* 如果不等于n的话，则一定等于0，该sq未分配值
				 * 则在分配值之后该sq一定属于group 0. */
				releaseGroup(_head._group);
			}
			_sequenceNum = n;
			for (Sq sq = this; sq._successor != null; sq = sq._successor) {
				sq._successor._sequenceNum = sq._sequenceNum + 1;
			}
			for (Sq sq = this; sq._predecessor != null; sq = sq._predecessor) {
				sq._predecessor._sequenceNum = sq._sequenceNum - 1;
			}
		}

		/**
		 * Unfix this square's sequence number if it is currently fixed;
		 * otherwise do nothing.
		 */
		void unfixNum() {
			Sq next = _successor, pred = _predecessor;
			_hasFixedNum = false;
			disconnect();
			if (pred != null) {
				pred.disconnect();
			}
			_sequenceNum = 0;
			if (next != null) {
				connect(next);
			}
			if (pred != null) {
				pred.connect(this);
			}
		}

		/**
		 * Return true iff this square's sequence number is fixed.
		 */
		boolean hasFixedNum() {
			return _hasFixedNum;
		}

		/**
		 * Returns direction of this square's arrow (0 if no arrow).
		 */
		int direction() {
			return _dir;
		}

		/**
		 * Return this square's current predecessor.
		 */
		Sq predecessor() {
			return _predecessor;
		}

		/**
		 * Return this square's current successor.
		 */
		Sq successor() {
			return _successor;
		}

		/**
		 * Return the head of the connected sequence this square is
		 * currently in.
		 */
		Sq head() {
			return _head;
		}

		/**
		 * Return the group number of this square's group. It is 0 if this
		 * square is numbered, and -1 if
		 * it is alone in its group.
		 */
		int group() {
			if (_sequenceNum != 0) {
				return 0;
				/* 如果seqnum不是0则已分配值，一定在第0组 */
			} else {
				return _head._group;
				/* 返回链表首结点的组号 */
			}
		}

		/**
		 * Size of alphabet.
		 */
		static final int ALPHA_SIZE = 26;

		/**
		 * Return a textual representation of this square's sequence number
		 * or group position.
		 */
		String seqText() {
			if (_sequenceNum != 0) {
				return String.format("%d", _sequenceNum);
			}
			int g = group() - 1;
			if (g < 0) {
				return "";
			}

			String groupName =
					String.format(
							"%s%s",
							g < ALPHA_SIZE ? "" : Character.toString((char) (
									g / ALPHA_SIZE + 'a')),
							Character.toString((char) (g % ALPHA_SIZE + 'a')));
			if (this == _head) {
				return groupName;
			}
			int n;
			n = 0;
			for (Sq p = this; p != _head; p = p._predecessor) {
				n += 1;
			}
			return String.format("%s%+d", groupName, n);
		}

		/**
		 * Return locations of this square's potential successors.
		 */
		PlaceList successors() {
			return _successors;
		}

		/**
		 * Return locations of this square's potential predecessors.
		 */
		PlaceList predecessors() {
			return _predecessors;
		}

		/**
		 * Returns true iff this square may be connected to square S1, that is:
		 * + S1 is in the correct direction from this square. + S1 does not
		 * have a current predecessor, this square does not have a current
		 * successor, S1 is not the first cell in sequence, and this square is
		 * not the last. + If S1 and this square both have sequence numbers,
		 * then this square's is sequenceNum() == S1.sequenceNum() - 1. + If
		 * neither S1 nor this square have sequence numbers, then they are
		 * not part of the same connected sequence.
		 */
		boolean connectable(Sq s1) {
			if (pl.dirOf(s1.pl) == direction()
						& s1.predecessor() == null
						& successor() == null
						& s1.sequenceNum() != 1
						& sequenceNum() != size()) {
				if (sequenceNum() != 0 & s1.sequenceNum() != 0) {
					return sequenceNum() == s1.sequenceNum() - 1;
				} else if (sequenceNum() == 0 & s1.sequenceNum() == 0) {
					if (group() == -1 & s1.group() == -1) {
						return true;
					} else {
						return group() != s1.group();
					}
				} else {
					return true;
				}
			}
			return false;
		}

		/**
		 * Connect this square to S1, if both are connectable; otherwise do
		 * nothing. Returns true iff this square and S1 were connectable.
		 * Assumes S1 is in the proper arrow direction from this
		 * square.
		 */
		boolean connect(Sq s1) {
			if (!connectable(s1)) {
				return false;
			}
			_unconnected -= 1;
			/* FIXME:
			 * Connect this square to its successor:
			 * + Set this square's _successor field and S1's
			 * and S1's _predecessor field. */
			_successor = s1;
			s1._predecessor = this;
			/* + If this square has a number, number all
			 *   its successors accordingly (if needed). */
			if (sequenceNum() != 0) {
				releaseGroup(s1._head._group);
				for (Sq sq = this; sq.successor() != null; sq = sq._successor) {
					sq._successor._sequenceNum = sq.sequenceNum() + 1;
				}
			}
			/* + If S1 is numbered, number this square and its
			 *   predecessors accordingly (if needed). */
			if (s1.sequenceNum() != 0) {
				releaseGroup(this._head._group);
				for (Sq sq = s1; sq.predecessor() !=
										 null; sq = sq._predecessor) {
					sq._predecessor._sequenceNum = sq.sequenceNum() - 1;
				}
			}
			/* + Set the _head fields of this square's
			 *   successors this square's _head. */
			for (Sq sq = this; sq != null; sq = sq._successor) {
				sq._head = this._head;
			}
			/* + If either of this square or S1 used to be
			 *   unnumbered and is now numbered, release its
			 *   group of whichever was unnumbered, so that
			 *   it can be reused. */
			/*   已在上述代码中完成 */

			/* + If both this square and S1 are unnumbered,
			 *   set the group of this square's head to the
			 *   result of joining the two groups. */
			if (sequenceNum() == 0 & s1.sequenceNum() == 0) {
				_head._group = joinGroups(this._head._group, s1._head._group);
			}
			return true;
		}

		/**
		 * Disconnect this square from its current successor, if any.
		 */
		void disconnect() {
			Sq next = _successor;
			if (next == null) {
				return;
			}
			_unconnected += 1;
			next._predecessor = _successor = null;
			if (_sequenceNum == 0) {
				next._head = next;
				if (predecessor() == null & next.successor() == null) {
					releaseGroup(_head._group);
					_head = this;
					_group = -1;
					next._group = -1;
				} else if (predecessor() != null & next.successor() != null) {
					next._group = newGroup();
				} else {
					if (predecessor() == null) {
						_group = -1;
					} else {
						next._group = -1;
					}
				}
			} else {
				next._head = next;

				/* disconnect后，前半截的处理 */
				boolean havefixednum = false;
				for (Sq sq = this; sq != null; sq = sq._predecessor) {
					if (sq.hasFixedNum()) {
						havefixednum = true;
						break;
					}
				}
				if (!havefixednum) {
					if (predecessor() != null) {
						_head._group = newGroup();
					} else {
						_head._group = -1;
					}
					for (Sq sq = this; sq != null; sq = sq._predecessor) {
						sq._sequenceNum = 0;
					}
				}

				/* disconnect后，后半截的处理 */
				/* FIXME: If neither next nor any square in its group that
				 *        follows it has a fixed sequence number, set all
				 *        their sequence numbers to 0 and create a new
				 *        group for them if next has a current successor
				 *        (otherwise set next's group to -1.) */
				havefixednum = false;
				for (Sq sq = next; sq != null; sq = sq._successor) {
					if (sq.hasFixedNum()) {
						havefixednum = true;
						break;
					}
				}
				if (!havefixednum) {
					if (next.successor() != null) {
						next._head._group = newGroup();
					} else {
						next._head._group = -1;
					}
					for (Sq sq = next; sq != null; sq = sq._successor) {
						sq._sequenceNum = 0;
					}
				}

				/* FIXME: Set the _head of next and all squares in its group to
				 *        next. */
				for (Sq sq = next; sq != null; sq = sq._successor) {
					sq._head = next;
					sq._group = next._group;
				}
			}
		}

		@Override
		public boolean equals(Object obj) {
			Sq sq = (Sq) obj;
			return sq != null
						   && pl == sq.pl
						   && _hasFixedNum == sq._hasFixedNum
						   && _sequenceNum == sq._sequenceNum
						   && _dir == sq._dir
						   && (_predecessor == null) == (sq._predecessor == null)
						   && (_predecessor == null || _predecessor.pl ==
															   sq._predecessor.pl)
						   && (_successor == null || _successor.pl == sq._successor.pl);
		}

		@Override
		public int hashCode() {
			return (x + 1) * (y + 1) * (_dir + 1) * (_hasFixedNum ? 3 : 1)
						   * (_sequenceNum + 1);
		}

		@Override
		public String toString() {
			return String.format("<Sq@%s, dir: %d>", pl, direction());
		}

		/**
		 * The coordinates of this square in the board.
		 */
		protected final int x, y;

		/**
		 * The coordinates of this square as a Place.
		 */
		protected final Place pl;

		/**
		 * The first in the currently connected sequence of cells ("group")
		 * that includes this one.
		 */
		private Sq _head;

		/**
		 * The group number of the group of which this is a member, if
		 * head == this. Numbered sequences have a group number of 0, regardless
		 * of the value of _group. Unnumbered one-member groups have a group
		 * number of -1. If _head != this and the square is unnumbered,
		 * then _group is undefined and the square's group number is maintained
		 * in _head._group.
		 */
		private int _group;

		/**
		 * True iff assigned a fixed sequence number.
		 */
		private boolean _hasFixedNum;

		/**
		 * The current imputed or fixed sequence number, numbering from 1, or
		 * 0 if there currently is none.
		 */
		private int _sequenceNum;

		/**
		 * The arrow direction. The possible values are 0 (for unset), 1 for
		 * northeast, 2 for east, 3 for southeast, 4 for south, 5 for
		 * southwest, 6 for west, 7 for northwest, and 8 for north.
		 */
		private final int _dir;

		/**
		 * The current predecessor of this square, or null if there is
		 * currently no predecessor.
		 */
		private Sq _predecessor;

		/**
		 * The current successor of this square, or null if there is
		 * currently no successor.
		 */
		private Sq _successor;

		/**
		 * Locations of the possible predecessors of this square.
		 */
		private PlaceList _predecessors;

		/**
		 * Locations of the possible successors of this square.
		 */
		private PlaceList _successors;
	}

	/**
	 * ASCII denotations of arrows, indexed by direction.
	 */
	private static final String[] ARROWS = {" *", "NE", "E ", "SE", "S ",
			"SW", "W ", "NW", "N "
	};

	/**
	 * Number of squares that haven't been connected.
	 */
	private int _unconnected;

	/**
	 * Dimensions of board.
	 */
	private int _width, _height;

	/**
	 * Contents of board, indexed by position.
	 * Sq[][]类型的_board按position作为索引存储中的所有square.
	 */
	private Sq[][] _board;

	/**
	 * Contents of board as a sequence of squares for convenient iteration.
	 */
	private final ArrayList<Sq> _allSquares = new ArrayList<>();

	/**
	 * _allSuccessors[x][y][dir] is a sequence of all queen moves possible on
	 * the board of in direction dir from (x, y). If dir == 0, this is all
	 * places that are a queen move from (x, y) in any direction.
	 */
	private PlaceList[][][] _allSuccessors;

	/**
	 * The solution from which this Model was built.
	 */
	private int[][] _solution;

	/**
	 * Inverse mapping from sequence numbers to board positions.
	 */
	private Place[] _solnNumToPlace;

	/**
	 * The set of positive group numbers currently in use.
	 */
	private final HashSet<Integer> _usedGroups = new HashSet<>();
}
