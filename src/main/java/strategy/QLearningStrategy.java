package strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.util.Pair;
import models.BoardArrWrapper;
import models.IntPair;
import models.Marker;
import strategy.Strategy;
import variants.board.Board;

public class QLearningStrategy implements Strategy {

  private Map<BoardArrWrapper, double[][]> stateToValueMatrix;
  List<Pair<BoardArrWrapper, IntPair>> stateToActionHistory;
  private final double learningRate;
  private final double exploreRate;
  private boolean isLearningDone;
  private double discount;

  public QLearningStrategy(double learningRate, double exploreRate, double discount) {
    this.stateToValueMatrix = new HashMap<>();
    this.stateToActionHistory = new ArrayList<>();
    this.learningRate = learningRate;
    this.exploreRate = exploreRate;
    this.isLearningDone = false;
    this.discount = discount;
  }

  @Override
  public IntPair getNextMove(Board board, Marker ownMarker) {
    System.out.println(board.getDisplayAsString());
    double[][] valueMatrixForState = stateToValueMatrix.getOrDefault(new BoardArrWrapper(board.getBoard()), createEmptyValueMatrix());
    IntPair selectedMove;
    if (!isLearningDone && Math.random() < exploreRate) {
      selectedMove = getExploreMove(board);
    } else {
      selectedMove = getExploitMove(board, valueMatrixForState);
    }
    stateToActionHistory.add(new Pair<>(new BoardArrWrapper(board.getBoard()), selectedMove));
    return selectedMove;
  }

  @Override
  public void onGameEnd(Optional<Boolean> didWin) {
    double reward = !didWin.isPresent() ? 5.0 : didWin.get() ? 100 : -100;
    // value update propagates backwards
    Collections.reverse(stateToActionHistory);

    Pair<BoardArrWrapper, IntPair> lastStateToAction = stateToActionHistory.get(0);
    BoardArrWrapper lastState = lastStateToAction.getKey();
    if (!stateToValueMatrix.containsKey(lastState)) {
      stateToValueMatrix.put(lastState, createEmptyValueMatrix());
    }
    reward *= discount;
    double[][] valMatrixForLastState = stateToValueMatrix.get(lastState);
    valMatrixForLastState[lastStateToAction.getValue().getX()][lastStateToAction.getValue().getY()] = reward;

    for(int i = 1; i < stateToActionHistory.size(); i++) {
      Pair<BoardArrWrapper, IntPair> curStateToAction = stateToActionHistory.get(i);
      Pair<BoardArrWrapper, IntPair> nextStateToAction = stateToActionHistory.get(i - 1);
      BoardArrWrapper curState = curStateToAction.getKey();
      BoardArrWrapper nextState = nextStateToAction.getKey();
      if (!stateToValueMatrix.containsKey(curState)) {
        stateToValueMatrix.put(curState, createEmptyValueMatrix());
      }
      updateMatrixForStateWithReward(stateToValueMatrix.get(curState), curStateToAction.getValue(), stateToValueMatrix.get(nextState));
    }
    stateToActionHistory = new ArrayList<>();
  }

  private void dumpStatesToQVals() {
    StringBuilder display = new StringBuilder();
    stateToValueMatrix.forEach((state, values) -> {
      display.append("+---+---+---+\n");
      for (int y = 0; y < 3; y++) {
        display.append("|");
        for (int x = 0; x < 3; x++) {
          String cell = "   |";
          if (state.board[x][y] != null) {
            cell = state.board[x][y] == Marker.X ? " X |" : " O |";
          }
          display.append(cell);
        }
        display.append("    ");
        for (int x = 0; x < 3; x++) {
          display.append(String.format("%.2f  ", values[x][y]));
        }
        display.append("\n+---+---+---+\n");
      }
    });
    System.out.println(display.toString());
  }

  private void updateMatrixForStateWithReward(double[][] curStateQVals,
                                              IntPair action,
                                              double[][] nextStateQVals) {
    double maxQValueFromNext = -Double.MAX_VALUE;
    for (int i = 0; i < 3; i++) {
      for(int j = 0; j < 3; j++) {
        double qVal = nextStateQVals[i][j];
        if(qVal > maxQValueFromNext) {
          maxQValueFromNext = qVal;
        }
      }
    }
    double curQValForAction = curStateQVals[action.getX()][action.getY()];
    double increment = learningRate * ((discount * maxQValueFromNext) - curQValForAction);
    double newQValForActionFromCurState = curQValForAction + increment;
    curStateQVals[action.getX()][action.getY()] = newQValForActionFromCurState;
  }

  private IntPair getExploreMove(Board board) {
    List<IntPair> possibleActions = board.getNextActions().stream()
        .collect(Collectors.toList());
    int index = (int) (Math.random() * possibleActions.size());
    return possibleActions.get(index);
  }

  private IntPair getExploitMove(Board board, double[][] valueMatrixForState) {
    List<IntPair> bestActions = new ArrayList<>();
    double bestScore = -Double.MAX_VALUE;
    for (IntPair move : board.getNextActions()) {
      double scoreForMove = valueMatrixForState[move.getX()][move.getY()];
      if (scoreForMove > bestScore) {
        bestActions = new ArrayList<>();
        bestActions.add(move);
        bestScore = scoreForMove;
      } else if (Math.abs(scoreForMove - bestScore) < 0.00001) {
        bestActions.add(move);
      }
    }
    return bestActions.get((int) (Math.random() * bestActions.size()));
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

  @Override
  public boolean isReady() {
    return isLearningDone;
  }

  @Override
  public void prep() {
    dumpStatesToQVals();
    this.isLearningDone = true;
  }
}
