package variants.board;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import models.ImmutableIntPair;
import models.IntPair;
import models.Line;
import models.Marker;

public class StandardBoard extends BaseBoard {

  public StandardBoard() {
    super();
  }

  public StandardBoard(Marker[][] board,
                       Map<Line, IntPair> winmap,
                       Marker curTurn,
                       Optional<Marker> winner) {
    super(board, winmap, curTurn, winner);
  }

  @Override
  public Set<IntPair> getNextActions() {
    Set<IntPair> result = new HashSet<>();
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (board[i][j] == null) {
          result.add(ImmutableIntPair.builder().x(i).y(j).build());
        }
      }
    }
    return result;
  }

  @Override
  public Map<IntPair, Map<Double, List<Board>>> getNextStates() {
    Set<IntPair> nextActions = getNextActions();
    return nextActions.stream().collect(Collectors.toMap(
        Function.identity(),
        pair -> {
          Map<Double, List<Board>> probabilityToBoards = new HashMap<>();
          Board newBoard = copy();
          newBoard.makeMove(pair.getX(), pair.getY());
          probabilityToBoards.put(1.0, Collections.singletonList(newBoard));
          return probabilityToBoards;
        }));
  }

  @Override
  protected Board copy() {
    Marker[][] copyBoard = new Marker[3][3];
    for (int i = 0; i < 3; i++) {
      copyBoard[i] = Arrays.copyOf(board[i], 3);
    }
    Map<Line, IntPair> copyWinmap = winmap.keySet().stream()
        .collect(Collectors.toMap(
            Function.identity(),
            line -> ImmutableIntPair.builder().from(winmap.get(line)).build()
        ));
    return new StandardBoard(copyBoard, copyWinmap, curTurn, winner);
  }
}
