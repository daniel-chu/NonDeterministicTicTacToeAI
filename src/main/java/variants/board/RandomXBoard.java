package variants.board;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import models.ImmutablePair;
import models.Line;
import models.Marker;
import models.Pair;

public class RandomXBoard extends NonDeterministicBoard {

  public RandomXBoard() {
    super();
  }

  public RandomXBoard(double mainMoveProbability) {
    super(mainMoveProbability);
  }

  public RandomXBoard(Marker[][] board,
                      double mainMoveProbability,
                      Map<Line, Pair> winmap,
                      Marker curTurn,
                      Optional<Marker> winner) {
    super(board, mainMoveProbability, winmap, curTurn, winner);
  }

  protected Map<Double, List<Board>> createPotentialBoardsForMove(int col,
                                                                  int row) {
    List<Board> randBoards = getRandomCellsFromMove(Pair.create(col, row)).stream()
        .map(offsetPair -> {
          Board copy = copy();
          copy.makeMove(offsetPair.getX(), offsetPair.getY());
          return copy;
        })
        .collect(Collectors.toList());

    Map<Double, List<Board>> result = new HashMap<>();
    Board copyForMainMove = copy();
    copyForMainMove.makeMove(col, row);
    result.put(mainMoveProbability, Collections.singletonList(copyForMainMove));
    result.put(1.0 - mainMoveProbability, randBoards);

    return result;
  }

  @Override
  protected Set<Pair> getRandomCellsFromMove(Pair pair) {
    return Stream.of(-1, 1)
        .flatMap(xOffset -> Stream.of(-1, 1)
            .map(yOffset -> Pair.create(pair.getX() + xOffset, pair.getY() + yOffset)))
        .filter(finalMove -> isMoveValid(finalMove.getX(), finalMove.getY()))
        .collect(Collectors.toSet());
  }

  @Override
  protected Board copy() {
    Marker[][] copyBoard = new Marker[3][3];
    for (int i = 0; i < 3; i++) {
      copyBoard[i] = Arrays.copyOf(board[i], 3);
    }
    Map<Line, Pair> copyWinmap = winmap.keySet().stream()
        .collect(Collectors.toMap(
            Function.identity(),
            line -> ImmutablePair.builder().from(winmap.get(line)).build()
        ));
    return new RandomXBoard(copyBoard, mainMoveProbability, copyWinmap, curTurn, winner);
  }
}
