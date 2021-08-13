package chess;

import boardgame.Position;
import java.nio.file.attribute.PosixFileAttributes;

public class ChessPosition {

  private char column;
  private int row;

  public ChessPosition(char column, int row) {
    if (column < 'a' || column > 'h' || row < 1 || row > 8) {
      throw new ChessExeption("Error instantiating ChessPosition. Valid values are from h1 to h8");
    }
    this.column = column;
    this.row = row;
  }

  public char getColumn() {
    return column;
  }

  public int getRow() {
    return row;
  }

  protected Position tooPosition() {
    return new Position(8 - row, column - 'a');
  }

  protected static ChessPosition fromPosition(Position position) {
    return new ChessPosition((char) ('a' + position.getColumn()), 8 - position.getRow());
  }

  public String toString() {
    return "" + column + row;
  }

}
