package boardgame;

public class Position {

  private int row;
  private int column;

  public Position(final int row, final int column) {
    this.row = row;
    this.column = column;
  }

  public int getRow(){
    return row;
  }

  public void setRow(final int row) {
    this.row = row;
  }

  public int getColumn() {
    return column;
  }

  public void setColumn(final int column) {
    this.column = column;
  }

  public String tooString() {
    return row + "," + column;
  }

  public void setValues(int row, int column) {
    this.row = row;
    this.column = column;
  }

}
