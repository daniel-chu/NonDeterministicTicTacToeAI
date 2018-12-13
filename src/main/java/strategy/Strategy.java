package strategy;

import java.util.Optional;

import models.IntPair;
import models.Marker;
import variants.board.Board;

public interface Strategy {
  IntPair getNextMove(Board board, Marker ownMarker);
  void onGameEnd(Optional<Boolean> didWin);
  boolean isReady();
  void prep();
}
