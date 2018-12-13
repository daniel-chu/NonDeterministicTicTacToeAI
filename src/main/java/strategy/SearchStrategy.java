package strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import models.IntPair;
import models.Marker;
import variants.board.Board;

public class SearchStrategy implements Strategy {

  private int maxDepth;
  private double discount;

  public SearchStrategy(int maxDepth, double discount) {
    this.maxDepth = maxDepth;
    this.discount = discount;
  }

  public IntPair getNextMove(Board board, Marker ownMarker) {
    System.out.println(board.getDisplayAsString());
    Map<IntPair, Map<Double, List<Board>>> selActionToProbsToStates = board.getNextStates();
    List<IntPair> bestActions = new ArrayList<>();
    double bestScore = -Double.MAX_VALUE;
    for (IntPair requestedAction : selActionToProbsToStates.keySet()) {
      Map<Double, List<Board>> probsToBoards = selActionToProbsToStates.get(requestedAction);
      double valForRequestedAction = getValueForPotentialBoardDistribution(probsToBoards, maxDepth, ownMarker);
      if (valForRequestedAction > bestScore) {
        bestActions = new ArrayList<>();
        bestActions.add(requestedAction);
        bestScore = valForRequestedAction;
      } else if (Math.abs(valForRequestedAction - bestScore) < 0.0001) {
        bestActions.add(requestedAction);
      }
    }
    return bestActions.get((int) (Math.random() * bestActions.size()));
  }

  @Override
  public void onGameEnd(Optional<Boolean> didWin) {
    // nothing needs to be done for search strategies
  }

  private double getValueOfBoard(Board board, Marker ownMarker, int depth) {
    Optional<Marker> maybeWinner = board.getWinner();
    if (maybeWinner.isPresent()) {
      return maybeWinner.get().equals(ownMarker) ? 10 : -10;
    } else if (board.isGameOver()) {
      return 1;
    } else if (depth == 0) {
      return discount * evaluateBoardWithHeuristic(board, ownMarker);
    } else {
      boolean isOwnTurn = board.getCurTurn().equals(ownMarker);
      return discount * (isOwnTurn ? getMaxVal(board, ownMarker, depth) : getMinVal(board, ownMarker, depth));
    }
  }

  private double evaluateBoardWithHeuristic(Board board, Marker ownMarker) {
    IntPair totalsPair = board.getWinmap().keySet()
        .stream()
        .map(board.getWinmap()::get)
        .filter(pair -> pair.getX() == 0 || pair.getY() == 0)
        .reduce(IntPair.create(0, 0), (pair1, pair2) ->
            IntPair.create(pair1.getX() + pair2.getX(), pair1.getY() + pair2.getY())
        );

    int multiplier = ownMarker == Marker.X ? 1 : -1;
    return Math.abs(totalsPair.getX() - totalsPair.getY()) * multiplier;
  }

  private double getMaxVal(Board board, Marker ownMarker, int depth) {
    Comparator<Double> maxComparator = Double::compareTo;
    return findVal(board, ownMarker, depth, maxComparator, -Double.MAX_VALUE);
  }

  private double getMinVal(Board board, Marker ownMarker, int depth) {
    Comparator<Double> minComparator = (a, b) -> -1 * a.compareTo(b);
    return findVal(board, ownMarker, depth, minComparator, Double.MAX_VALUE);
  }

  private double findVal(Board board, Marker ownMarker, int depth, Comparator<Double> comparator, double initValue) {
    Map<IntPair, Map<Double, List<Board>>> selActionToProbsToStates = board.getNextStates();
    double score = initValue;
    for (IntPair requestedAction : selActionToProbsToStates.keySet()) {
      Map<Double, List<Board>> probsToBoards = selActionToProbsToStates.get(requestedAction);
      double valForRequestedAction = getValueForPotentialBoardDistribution(probsToBoards, depth, ownMarker);
      if (comparator.compare(valForRequestedAction, score) > 0) {
        score = valForRequestedAction;
      }
    }
    return score;
  }

  private double getValueForPotentialBoardDistribution(Map<Double, List<Board>> probsToBoards,
                                                       int depth,
                                                       Marker ownMarker) {
    double valForRequestedAction = 0.0;
    for (Double prob : probsToBoards.keySet()) {
      List<Board> possibleBoards = probsToBoards.get(prob);
      for (Board selectedBoard : possibleBoards) {
        int nextDepth = selectedBoard.getCurTurn().equals(Marker.O)
            ? depth - 1
            : depth;
        double val = getValueOfBoard(selectedBoard, ownMarker, nextDepth);
        valForRequestedAction += val * prob;
      }
    }
    return valForRequestedAction;
  }

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void prep() {
    // nothing needs to be done for search agent
  }
}
