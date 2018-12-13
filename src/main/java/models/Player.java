package models;

import org.immutables.value.Value;

import strategy.Strategy;
import variants.board.Board;

@Value.Immutable
public interface Player {
  Marker getMarker();
  Strategy getStrategy();

  default Pair getNextMove(Board board) {
    return getStrategy().getNextMove(board, getMarker());
  }
}
