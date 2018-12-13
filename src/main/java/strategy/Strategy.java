package strategy;

import models.Marker;
import models.Pair;
import variants.board.Board;

public interface Strategy {
  Pair getNextMove(Board board, Marker ownMarker);
}
