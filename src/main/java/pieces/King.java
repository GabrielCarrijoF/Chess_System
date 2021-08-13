package pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;


public class King extends ChessPiece {

  private ChessMatch chessMatch;

  public King(Board board, Color color, ChessMatch chessMatch) {
    super(board, color);
    this.chessMatch = chessMatch;
  }

  private boolean testRookCastling(Position position) {
    ChessPiece p = (ChessPiece) getBoard().piece(position);
    return p != null && p instanceof Rook && p.getColor() == getColor() && p.getMoveCount() == 0;
  }

  @Override
  public String toString() {
    return "R";
  }

  private boolean canMove(Position position) {
    ChessPiece p = (ChessPiece) getBoard().piece(position);
    return p == null || p.getColor() != getColor();
  }

  @Override
  public boolean[][] possibleMoves() {
    boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

    Position p = new Position(0, 0);

    //Above
    p.setValues(position.getRow() - 1, position.getColumn());
    if (getBoard().positionExists(p) && canMove(p)) {
      mat[p.getRow()][p.getColumn()] = true;
    }

    //Below
    p.setValues(position.getRow() + 1, position.getColumn());
    if (getBoard().positionExists(p) && canMove(p)) {
      mat[p.getRow()][p.getColumn()] = true;
    }

    //Left
    p.setValues(position.getRow(), position.getColumn() - 1);
    if (getBoard().positionExists(p) && canMove(p)) {
      mat[p.getRow()][p.getColumn()] = true;
    }

    //Right
    p.setValues(position.getRow(), position.getColumn() + 1);
    if (getBoard().positionExists(p) && canMove(p)) {
      mat[p.getRow()][p.getColumn()] = true;
    }

    //Nw
    p.setValues(position.getRow() - 1, position.getColumn() - 1);
    if (getBoard().positionExists(p) && canMove(p)) {
      mat[p.getRow()][p.getColumn()] = true;
    }
    //Ne
    p.setValues(position.getRow() - 1, position.getColumn() + 1);
    if (getBoard().positionExists(p) && canMove(p)) {
      mat[p.getRow()][p.getColumn()] = true;
    }

    //Sw
    p.setValues(position.getRow() + 1, position.getColumn() - 1);
    if (getBoard().positionExists(p) && canMove(p)) {
      mat[p.getRow()][p.getColumn()] = true;
    }

    //
    p.setValues(position.getRow() + 1, position.getColumn() + 1);
    if (getBoard().positionExists(p) && canMove(p)) {
      mat[p.getRow()][p.getColumn()] = true;
    }

    //#specialMove Castling
    if (getMoveCount() == 0 && !chessMatch.getCheck()) {
      //Rook King
      Position positionRookR = new Position(position.getRow(), position.getColumn() + 3);
      if (testRookCastling(positionRookR)) {
        Position firstPosition = new Position(position.getRow(), position.getColumn() + 1);
        Position secondPosition = new Position(position.getRow(), position.getColumn() + 2);
        if (getBoard().piece(firstPosition) == null && getBoard().piece(secondPosition) == null) {
          mat[position.getRow()][position.getColumn() + 2] = true;
        }
      }
      //Rook Quenn
      Position positionRookQ = new Position(position.getRow(), position.getColumn() - 4);
      if (testRookCastling(positionRookQ)) {
        Position firstPosition = new Position(position.getRow(), position.getColumn() - 1);
        Position secondPosition = new Position(position.getRow(), position.getColumn() - 2);
        Position thirdPosition = new Position(position.getRow(), position.getColumn() - 3);
        if (getBoard().piece(firstPosition) == null && getBoard().piece(secondPosition) == null && getBoard().piece(thirdPosition) == null) {
          mat[position.getRow()][position.getColumn() - 2] = true;
        }

      }
    }
    return mat;
  }
}
