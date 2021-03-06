package variants.board;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import models.IntPair;
import models.Line;
import models.Marker;

public abstract class NonDeterministicBoard extends BaseBoard {

  protected double mainMoveProbability;

  public NonDeterministicBoard() {
    super();
    mainMoveProbability = 0.7;
  }

  public NonDeterministicBoard(double mainMoveProbability) {
    super();
    this.mainMoveProbability = mainMoveProbability;
  }

  public NonDeterministicBoard(Marker[][] board,
                               double mainMoveProbability,
                               Map<Line, IntPair> winmap,
                               Marker curTurn,
                               Optional<Marker> winner) {
    super(board, winmap, curTurn, winner);
    this.mainMoveProbability = mainMoveProbability;
  }

  @Override
  public Set<IntPair> getNextActions() {
    return IntStream.range(0, 3)
        .mapToObj(i -> i)
        .flatMap(x -> IntStream.range(0, 3)
            .mapToObj(y -> IntPair.create(x, y)))
        .filter(pair -> board[pair.getX()][pair.getY()] == null || getRandomCellsFromMove(pair).stream()
            .anyMatch(finalMove -> board[finalMove.getX()][finalMove.getY()] == null))
        .collect(Collectors.toSet());
  }

  protected abstract Set<IntPair> getRandomCellsFromMove(IntPair pair);

  @Override
  public Map<IntPair, Map<Double, List<Board>>> getNextStates() {
    return getNextActions().stream()
        .collect(Collectors.toMap(
            Function.identity(),
            pair -> createPotentialBoardsForMove(pair.getX(), pair.getY())
        ));
  }

  protected abstract Map<Double, List<Board>> createPotentialBoardsForMove(int col,
                                                                           int row);

}
