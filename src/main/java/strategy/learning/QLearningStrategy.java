package strategy.learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.Marker;
import models.Pair;
import strategy.Strategy;
import variants.board.Board;

public class QLearningStrategy implements Strategy {

  private Map<Marker[][], double[][]> stateToValueMatrix;
  List<Marker[][]> stateList;
  private double exploreRate;
  private boolean isLearningDone;
  private int numIterations;

  public QLearningStrategy(double exploreRate) {
    this.stateToValueMatrix = new HashMap<>();
    this.stateList = new ArrayList<>();
    this.exploreRate = exploreRate;
    this.isLearningDone = false;
  }

  @Override
  public Pair getNextMove(Board board, Marker ownMarker) {
    System.out.println(board.getDisplayAsString());
    double[][] valueMatrixForState = stateToValueMatrix.getOrDefault(board.getBoard(), createEmptyValueMatrix());
    if (Math.random() < exploreRate) {
      return getExploreMove(board);
    }

    return getExploitMove(board, valueMatrixForState);
  }

  private Pair getExploreMove(Board board) {
    List<Pair> possibleActions = board.getNextActions().stream()
        .collect(Collectors.toList());
    int index = (int) (Math.random() * possibleActions.size());
    return possibleActions.get(index);
  }

  private Pair getExploitMove(Board board, double[][] valueMatrixForState) {
    return Pair.create(0, 0);
  }

  private double[][] createEmptyValueMatrix() {
    double[][] matrix = new double[3][3];
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        matrix[i][j] = 0;
      }
    }
    return matrix;
  }
}
