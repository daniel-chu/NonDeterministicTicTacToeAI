package variants.board;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import models.ImmutablePair;
import models.Line;
import models.Marker;
import models.Pair;

public abstract class BaseBoard implements Board {

  protected Marker[][] board;
  protected Map<Line, Pair> winmap;
  protected Marker curTurn;
  protected Optional<Marker> winner;

  public BaseBoard() {
    this.curTurn = Marker.X;
    this.winner = Optional.empty();
    initialize();
  }

  public BaseBoard(Marker[][] board,
                   Map<Line, Pair> winmap,
                   Marker curTurn,
                   Optional<Marker> winner) {
    this.board = board;
    this.winmap = winmap;
    this.curTurn = curTurn;
    this.winner = winner;
  }

  public void initialize() {
    board = new Marker[3][3];
    winmap = Arrays.stream(Line.values()).collect(Collectors.toMap(
        Function.identity(),
        line -> Pair.create(0, 0)
    ));
  }

  public Optional<Marker> makeMove(int col, int row) {
    if (isMoveValid(col, row) && board[col][row] == null) {
      board[col][row] = curTurn;
      List<Line> linesToUpdate = Line.getLinesToUpdate(col, row);

      for (Line line : linesToUpdate) {
        Pair cur = winmap.get(line);
        Pair newScore;
        if (curTurn.equals(Marker.X)) {
          newScore = ImmutablePair.builder().from(cur).x(cur.getX() + 1).build();
        } else {
          newScore = ImmutablePair.builder().from(cur).y(cur.getY() + 1).build();
        }
        if (newScore.getX() == 3 || newScore.getY() == 3) {
          winner = Optional.of(curTurn);
        } else {
          winmap.put(line, newScore);
        }
      }
    }
    curTurn = Marker.getNext(curTurn);
    return winner;
  }

  protected boolean isMoveValid(int col, int row) {
    return (col >= 0 && col < 3) && (row >= 0 && row < 3);
  }

  public abstract Set<Pair> getNextActions();

  public abstract Map<Pair, Map<Double, List<Board>>> getNextStates();

  public Map<Line, Pair> getWinmap() {
    return winmap;
  }

  public Marker[][] getBoard() {
    return board;
  }

  public Marker getCurTurn() {
    return curTurn;
  }

  public boolean isGameOver() {
    return winner.isPresent() || Arrays.stream(board)
        .flatMap(Arrays::stream)
        .allMatch(cell -> cell != null);
  }

  public Optional<Marker> getWinner() {
    return winner;
  }

  protected abstract Board copy();

  public String getDisplayAsString() {
    StringBuilder display = new StringBuilder("    A   B   C  \n");
    display.append("  +---+---+---+\n");
    for (int y = 0; y < 3; y++) {
      display.append(y + 1);
      display.append(" |");
      for (int x = 0; x < 3; x++) {
        String cell = "   |";
        if (board[x][y] != null) {
          cell = board[x][y] == Marker.X ? " X |" : " O |";
        }
        display.append(cell);
      }
      display.append("\n  +---+---+---+\n");
    }
    if (isGameOver()) {
      display.append("Game Over!");
    } else {
      display.append(curTurn.toString()).append("'s turn.\n");
    }
    return display.toString();
  }
}
