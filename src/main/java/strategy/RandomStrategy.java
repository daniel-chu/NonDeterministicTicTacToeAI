package strategy;

import java.util.Optional;
import java.util.stream.Collectors;

import models.IntPair;
import models.Marker;
import variants.board.Board;

public class RandomStrategy implements Strategy {
  @Override
  public IntPair getNextMove(Board board, Marker ownMarker) {
    return board.getNextActions().stream()
        .collect(Collectors.toList())
        .get((int) (Math.random() * board.getNextActions().size()));
  }

  @Override
  public void onGameEnd(Optional<Boolean> didWin) {
    // nothing needs to be done for random agent
  }

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void prep() {
    // nothing needs to be done for random agent
  }
}