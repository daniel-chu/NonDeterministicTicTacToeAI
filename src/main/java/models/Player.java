package models;

import org.immutables.value.Value;

import java.util.Optional;

import strategy.Strategy;
import variants.board.Board;

@Value.Immutable
public interface Player {
  Marker getMarker();
  Strategy getStrategy();

  default void onGameEnd(Optional<Marker> maybeWinner) {
    Optional<Boolean> didWin = maybeWinner.isPresent()
        ? Optional.of(getMarker().equals(maybeWinner.get()))
        : Optional.empty();
    getStrategy().onGameEnd(didWin);
  }
  default IntPair getNextMove(Board board) {
    return getStrategy().getNextMove(board, getMarker());
  }
  default boolean isReady() {
    return getStrategy().isReady();
  }
  default void setReady() {
    getStrategy().prep();
  }
}
