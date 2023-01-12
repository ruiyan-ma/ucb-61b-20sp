/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import java.util.regex.Pattern;

import static loa.Piece.*;
import static loa.Square.*;

/** Represents the state of a game of Lines of Action.
 *  @author Shaw Ma
 */
class Board {

    /** Default number of moves for each side that results in a draw. */
    static final int DEFAULT_MOVE_LIMIT = 60;

    /** Pattern describing a valid square designator (cr). */
    static final Pattern ROW_COL = Pattern.compile("^[a-h][1-8]$");

    /** A Board whose initial contents are taken from INITIALCONTENTS
     *  and in which the player playing TURN is to move. The resulting
     *  Board has
     *        get(col, row) == INITIALCONTENTS[row][col]
     *  Assumes that PLAYER is not null and INITIALCONTENTS is 8x8.
     *
     *  CAUTION: The natural written notation for arrays initializers puts
     *  the BOTTOM row of INITIALCONTENTS at the top.
     */
    Board(Piece[][] initialContents, Piece turn) {
        initialize(initialContents, turn);
    }

    /** A new board in the standard initial position. */
    Board() {
        this(INITIAL_PIECES, BP);
    }

    /** A Board whose initial contents and state are copied from
     *  BOARD. */
    Board(Board board) {
        this();
        copyFrom(board);
    }

    /** Set my state to CONTENTS with SIDE to move. */
    void initialize(Piece[][] contents, Piece side) {
        assert contents.length * contents[0].length == _board.length;
        int index = 0;
        for (int i = 0; i < contents.length; i++) {
            for (int j = 0; j < contents[0].length; j++) {
                _board[index] = contents[i][j];
                index++;
            }
        }
        _moves.clear();
        _turn = side;
        _moveLimit = DEFAULT_MOVE_LIMIT;
        _subsetsInitialized = false;
        computeRegions();
        _winnerKnown = false;
        winner();
    }

    /** Set me to the initial configuration. */
    void clear() {
        initialize(INITIAL_PIECES, BP);
    }

    /** Set my state to a copy of BOARD. */
    void copyFrom(Board board) {
        if (board == this) {
            return;
        }
        for (int index = 0; index < board._board.length; index++) {
            _board[index] = board._board[index];
        }
        _moves.clear();
        for (int i = 0; i < board._moves.size(); i++) {
            _moves.add(board._moves.get(i));
        }
        _turn = board._turn;
        _moveLimit = board._moveLimit;
        _winnerKnown = board._winnerKnown;
        _winner = board._winner;
        _whiteRegionSizes.clear();
        for (int i = 0; i < board._whiteRegionSizes.size(); i++) {
            _whiteRegionSizes.add(board._whiteRegionSizes.get(i));
        }
        _blackRegionSizes.clear();
        for (int i = 0; i < board._blackRegionSizes.size(); i++) {
            _blackRegionSizes.add(board._blackRegionSizes.get(i));
        }
    }

    /** Return the contents of the square at SQ. */
    Piece get(Square sq) {
        return _board[sq.index()];
    }

    /** Set the square at SQ to V and set the side that is to move next
     *  to NEXT, if NEXT is not null. */
    void set(Square sq, Piece v, Piece next) {
        _board[sq.index()] = v;
        if (next != null) {
            _turn = next;
        }
    }

    /** Set the square at SQ to V, without modifying the side that
     *  moves next. */
    void set(Square sq, Piece v) {
        set(sq, v, null);
    }

    /** Set limit on number of moves (before tie results) to LIMIT. */
    void setMoveLimit(int limit) {
        _moveLimit = limit;
        _winnerKnown = false;
    }

    /** Assuming isLegal(MOVE), make MOVE. Assumes MOVE.isCapture()
     *  is false. */
    void makeMove(Move move) {
        /* Every time after making move:
         * Adjust _board and change _turn to opposite;
         * Put move into _moves, subtract 1 from _moveLimit;
         * Set _subsetsInitialized to false, run computeRegions() again
         * to update _whiteRegionSizes and _blackRegionSizes;
         * Run winner() again to update win or not. */
        assert isLegal(move);
        assert _turn == _board[move.getFrom().index()];
        if (_board[move.getTo().index()] != Piece.EMP) {
            move = move.captureMove();
        }
        set(move.getFrom(), Piece.EMP);
        set(move.getTo(), _turn, _turn.opposite());
        _moves.add(move);
        _moveLimit -= 1;
        _subsetsInitialized = false;
        computeRegions();
        winner();
    }

    /** Retract (unmake) one move, returning to the state immediately before
    *  that move.  Requires that movesMade () > 0. */
    void retract() {
        assert movesMade() > 0;
        _moveLimit += 1;
        Move move = _moves.remove(_moves.size() - 1);
        assert _board[move.getTo().index()] == _turn.opposite();
        if (move.isCapture()) {
            set(move.getTo(), _turn, _turn.opposite());
        } else {
            set(move.getTo(), Piece.EMP, _turn.opposite());
        }
        set(move.getFrom(), _turn);
        _subsetsInitialized = false;
        computeRegions();
        _winnerKnown = false;
        winner();
    }

    /** Return the Piece representing who is next to move. */
    Piece turn() {
        return _turn;
    }

    /** Return true iff FROM - TO is a legal move for the player currently on
     *  move. */
    boolean isLegal(Square from, Square to) {
        Piece me = _board[from.index()];
        if (me != _turn) {
            return false;
        }
        if (!from.isValidMove(to)) {
            return false;
        }
        if (blocked(from, to)) {
            return false;
        }
        int direction = from.direction(to);
        int numPiece = 1;
        for (Square sqDir = from.moveDest(direction, 1);
                sqDir != null;
                sqDir = sqDir.moveDest(direction, 1)) {
            Piece p = _board[sqDir.index()];
            if (p != Piece.EMP) {
                numPiece += 1;
            }
        }
        int revDirection = to.direction(from);
        for (Square sqRevDir = from.moveDest(revDirection, 1);
                sqRevDir != null;
                sqRevDir = sqRevDir.moveDest(revDirection, 1)) {
            Piece p = _board[sqRevDir.index()];
            if (p != Piece.EMP) {
                numPiece += 1;
            }
        }
        if (from.distance(to) != numPiece) {
            return false;
        }
        return true;
    }

    /** Return true iff MOVE is legal for the player currently on move.
     *  The isCapture() property is ignored. */
    boolean isLegal(Move move) {
        return isLegal(move.getFrom(), move.getTo());
    }

    /** Return a sequence of all legal moves from this position. */
    List<Move> legalMoves() {
        ArrayList<Move> legalMoves = new ArrayList<>();
        for (int i = 0; i < ALL_SQUARES.length; i++) {
            Square from = ALL_SQUARES[i];
            for (int j = 0; j < ALL_SQUARES.length; j++) {
                Square to = ALL_SQUARES[j];
                if (isLegal(from, to)) {
                    if (_board[to.index()] == Piece.EMP) {
                        legalMoves.add(Move.mv(from, to));
                    } else {
                        legalMoves.add(Move.mv(from, to, true));
                    }
                }
            }
        }
        return legalMoves;
    }

    /** Return true iff the game is over (either player has all his
     *  pieces continguous or there is a tie). */
    boolean gameOver() {
        return winner() != null;
    }

    /** Return true iff SIDE's pieces are continguous. */
    boolean piecesContiguous(Piece side) {
        /* ArrayList<Integer> _whiteRegionSizes and _blackRegionSizes
         * are supposed to store the size of contiguous regions of white
         * pieces and black pieces. _whiteRegionSizes.size()=1 or
         * _blackRegionSizes.size()=1 implies white or black has  only one
         * contiguous region and won the game. */
        return getRegionSizes(side).size() == 1;
    }

    /** Return the winning side, if any.  If the game is not over, result is
     *  null.  If the game has ended in a tie, returns EMP. */
    Piece winner() {
        if (!_winnerKnown) {
            boolean whiteCont = piecesContiguous(Piece.WP);
            boolean blackCont = piecesContiguous(Piece.BP);
            if (whiteCont & !blackCont) {
                _winnerKnown = true;
                _winner = Piece.WP;
            } else if (!whiteCont & blackCont) {
                _winnerKnown = true;
                _winner = Piece.BP;
            } else if (whiteCont & blackCont) {
                _winnerKnown = true;
                _winner = _turn.opposite();
            } else if (_moveLimit == 0) {
                _winnerKnown = true;
                _winner = Piece.EMP;
            } else {
                _winnerKnown = false;
                _winner = null;
            }
        }
        return _winner;
    }

    /** Return the total number of moves that have been made (and not
     *  retracted).  Each valid call to makeMove with a normal move increases
     *  this number by 1. */
    int movesMade() {
        return _moves.size();
    }

    @Override
    public boolean equals(Object obj) {
        Board b = (Board) obj;
        return Arrays.deepEquals(_board, b._board) && _turn == b._turn
               && _moves.equals(b._moves) && _moveLimit == b._moveLimit
               && _winnerKnown == b._winnerKnown && _winner == b._winner
               && _subsetsInitialized == b._subsetsInitialized
               && _whiteRegionSizes.equals(b._whiteRegionSizes)
               && _blackRegionSizes.equals(b._blackRegionSizes);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(_board) * 2 + _turn.hashCode();
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===%n");
        for (int r = BOARD_SIZE - 1; r >= 0; r -= 1) {
            out.format("    ");
            for (int c = 0; c < BOARD_SIZE; c += 1) {
                out.format("%s ", get(sq(c, r)).abbrev());
            }
            out.format("%n");
        }
        out.format("Next move: %s%n===", turn().fullName());
        return out.toString();
    }

    /** Return true if a move from FROM to TO is blocked by an opposing
     *  piece or by a friendly piece on the target square. */
    private boolean blocked(Square from, Square to) {
        if (_board[from.index()] == _board[to.index()]) {
            return true;
        }
        int direction = from.direction(to);
        for (Square sqDir = from.moveDest(direction, 1);
                from.distance(sqDir) < from.distance(to);
                sqDir = sqDir.moveDest(direction, 1)) {
            Piece p = _board[sqDir.index()];
            if (p == _board[from.index()].opposite()) {
                return true;
            }
        }
        return false;
    }

    /** Return the size of the as-yet unvisited cluster of squares
     *  containing P at and adjacent to SQ.  VISITED indicates squares that
     *  have already been processed or are in different clusters.  Update
     *  VISITED to reflect squares counted. */
    private int numContig(Square sq, boolean[][] visited, Piece p) {
        /* Use tree structure to call numContig iteratively, return the size
         * of the biggest contiguous region containing SQ. Use boolean[][]
         * VISITED to record squares have been visited. Use P to show which
         * side of Piece to count. */
        if (_board[sq.index()] != p) {
            return 0;
        }
        if (visited[sq.row()][sq.col()]) {
            return 0;
        }
        visited[sq.row()][sq.col()] = true;
        int contigNum = 1;
        Square[] adjacent = sq.adjacent();
        for (int i = 0; i < adjacent.length; i++) {
            contigNum += numContig(adjacent[i], visited, p);
        }
        return contigNum;
    }

    /** Set the values of _whiteRegionSizes and _blackRegionSizes. */
    private void computeRegions() {
        if (_subsetsInitialized) {
            return;
        }
        _whiteRegionSizes.clear();
        _blackRegionSizes.clear();
        boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < ALL_SQUARES.length; i++) {
            Square sq = ALL_SQUARES[i];
            Piece p = _board[sq.index()];
            if (p != Piece.EMP & !visited[sq.row()][sq.col()]) {
                int numContig = numContig(sq, visited, p);
                if (p == Piece.BP) {
                    _blackRegionSizes.add(numContig);
                } else {
                    _whiteRegionSizes.add(numContig);
                }
            }
        }
        Collections.sort(_whiteRegionSizes, Collections.reverseOrder());
        Collections.sort(_blackRegionSizes, Collections.reverseOrder());
        _subsetsInitialized = true;
    }

    /** Return the sizes of all the regions in the current union-find
     *  structure for side S. */
    List<Integer> getRegionSizes(Piece s) {
        computeRegions();
        if (s == WP) {
            return _whiteRegionSizes;
        } else {
            return _blackRegionSizes;
        }
    }

    /** Return how many P pieces left on this board. */
    int numPieces(Piece p) {
        int numberOfPieces = 0;
        if (p == Piece.WP) {
            for (Integer i : _whiteRegionSizes) {
                numberOfPieces += i;
            }
        } else {
            for (Integer i : _blackRegionSizes) {
                numberOfPieces += i;
            }
        }
        return numberOfPieces;
    }

    /** Return the size of the biggest contiguous regions for piece P. */
    int maxContNum(Piece p) {
        if (p == Piece.WP) {
            return _whiteRegionSizes.get(0);
        } else {
            return _blackRegionSizes.get(0);
        }
    }

    /** Return the number of contiguous regions for piece P. */
    int numContRegion(Piece p) {
        if (p == Piece.WP) {
            return _whiteRegionSizes.size();
        } else {
            return _blackRegionSizes.size();
        }
    }

    /** Return the distance between two contiguous regions for P. */
    int distCont(Piece p) {
        return 0;
    }

    /** Return String to print the board. */
    public String print() {
        Formatter out = new Formatter();
        for (int r = BOARD_SIZE - 1, col = 8; r >= 0; r -= 1, col -= 1) {
            out.format("%s ", col);
            for (int c = 0; c < BOARD_SIZE; c += 1) {
                out.format("%s ", get(sq(c, r)).abbrev());
            }
            out.format("%n");
        }
        out.format("  a b c d e f g h %n");
        out.format("Next move: %s", turn().fullName());
        return out.toString();
    }


    /** The standard initial configuration for Lines of Action (bottom row
     *  first). */
    static final Piece[][] INITIAL_PIECES = {
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
    };

    /** Current contents of the board.  Square S is at _board[S.index()]. */
    private final Piece[] _board = new Piece[BOARD_SIZE  * BOARD_SIZE];

    /** List of all unretracted moves on this board, in order. */
    private final ArrayList<Move> _moves = new ArrayList<>();

    /** Current side on move. */
    private Piece _turn;

    /** Limit on number of moves before tie is declared.  */
    private int _moveLimit;

    /** True iff the value of _winner is known to be valid. */
    private boolean _winnerKnown;

    /** Cached value of the winner (BP, WP, EMP (for tie), or null (game still
     *  in progress).  Use only if _winnerKnown. */
    private Piece _winner;

    /** True iff subsets computation is up-to-date. */
    private boolean _subsetsInitialized;

    /** List of the sizes of continguous clusters of pieces, by color. */
    private final ArrayList<Integer> _whiteRegionSizes = new ArrayList<>();

    /** List of the sizes of continguous clusters of pieces, by color. */
    private final ArrayList<Integer> _blackRegionSizes = new ArrayList<>();
}
