package variants.board;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import models.Line;
import models.Marker;
import models.Pair;

public interface Board {
  void initialize();
  Optional<Marker> makeMove(int col, int row);
  Set<Pair> getNextActions();
  Map<Pair, Map<Double, List<Board>>> getNextStates();
  Map<Line, Pair> getWinmap();
  Marker[][] getBoard();
  Marker getCurTurn();
  boolean isGameOver();
  Optional<Marker> getWinner();
  String getDisplayAsString();
}
