package models;

import java.util.Arrays;

public class BoardArrWrapper {
  public Marker[][] board;

  public BoardArrWrapper(Marker[][] board) {
    this.board = board;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BoardArrWrapper that = (BoardArrWrapper) o;

    return Arrays.deepEquals(board, that.board);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(board);
  }
}
