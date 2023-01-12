/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;

import static loa.Piece.*;
import java.util.List;

/** An automated Player.
 *  @author shaw ma
 */

class MachinePlayer extends Player {

    /** A position-score magnitude indicating a win (for white if
     *  positive, black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;

    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** The DEPTH limit for MachinePlayer to compute on Gametree. */
    private static final int DEPTH = 5;

    /** A new MachinePlayer with no piece or controller (intended
     *  to produce a template). */
    MachinePlayer() {
        this(null, null);
    }

    /** A MachinePlayer that plays the SIDE pieces in GAME. */
    MachinePlayer(Piece side, Game game) {
        super(side, game);
    }

    @Override
    String getMove() {
        Move choice;
        assert side() == getGame().getBoard().turn();
        choice = searchForMove();
        getGame().reportMove(choice);
        return choice.toString();
    }

    @Override
    Player create(Piece piece, Game game) {
        return new MachinePlayer(piece, game);
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move after searching the game tree to DEPTH>0 moves
     *  from the current position. Assumes the game is not over. */
    private Move searchForMove() {
        /* Alpha initialization: -infinity.
         * Beta initialization: infinity.
         * Initially, automated player is White. */
        Board work = new Board(getBoard());
        assert side() == work.turn();
        _foundMove = null;
        if (side() == WP) {
            findMove(work, chooseDepth(), true, 1, -INFTY, INFTY);
        } else {
            findMove(work, chooseDepth(), true, -1, -INFTY, INFTY);
        }
        return _foundMove;
    }

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        /* SENSE=1 implies this MachinePlayer is White, otherwise is Black.
         * We set White to maximize the score and Black to minimize the score.
         * According to SENSE, we know what this MachinePlayer should do to
         * maximize its utility. */
        if (board.gameOver() | depth == 0) {
            return heuristicFunction(board);
        }
        int bestScore;
        Move bestMove = Move.mv("a0-a0", false);
        if (sense == 1) {
            bestScore = -INFTY;
        } else {
            bestScore = INFTY;
        }
        List<Move> moves = board.legalMoves();
        for (Move move : moves) {
            board.makeMove(move);
            int score = findMove(board, depth - 1, false, -sense, alpha, beta);
            board.retract();
            if (sense == 1) {
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
                alpha = Math.max(bestScore, alpha);
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
                beta = Math.min(bestScore, beta);
            }
            if (alpha >= beta) {
                return bestScore;
            }
        }
        if (saveMove) {
            _foundMove = bestMove;
        }
        return bestScore;
    }

    /** Return the heuristic value of BOARD. */
    private int heuristicFunction(Board board) {
        /* What to be considered:
         * The number of pieces left;
         * The sizes of the biggest contiguous regions for each piece;
         * The number of contiguous regions for each piece;
         * The distance between the contiguous region of each piece. */
        if (board.gameOver()) {
            if (board.winner() == WP) {
                return WINNING_VALUE;
            } else if (board.winner() == BP) {
                return -WINNING_VALUE;
            } else {
                return 0;
            }
        } else {
            double whiteScore, blackScore;
            whiteScore = -2 * board.numContRegion(WP)
                         + board.maxContNum(WP)
                         - 0.5 * board.numPieces(WP);
            blackScore = -2 * board.numContRegion(BP)
                         + board.maxContNum(BP)
                         - 0.5 * board.numPieces(BP);
            return (int) (whiteScore - blackScore);
        }
    }

    /** Return a search depth for the current position. */
    private int chooseDepth() {
        return DEPTH;
    }

    /** Used to convey moves discovered by findMove. */
    private Move _foundMove;
}
