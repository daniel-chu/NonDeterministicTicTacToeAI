package variants.game;

import java.util.Optional;

import models.Marker;

public interface TicTacToe {
  Optional<Marker> play();
  void reset();
}
