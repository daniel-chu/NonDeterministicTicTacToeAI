package variants.board;

import java.util.Arrays;
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

public abstract class BaseBoard implements Board {

  protected Marker[][] board;
  protected Map<Line, IntPair> winmap;
  protected Marker curTurn;
  protected Optional<Marker> winner;

  public BaseBoard() {
    initialize();
  }

  public BaseBoard(Marker[][] board,
                   Map<Line, IntPair> winmap,
                   Marker curTurn,
                   Optional<Marker> winner) {
    this.board = board;
    this.winmap = winmap;
    this.curTurn = curTurn;
    this.winner = winner;
  }

  public void initialize() {
    this.curTurn = Marker.X;
    this.board = new Marker[3][3];
    this.winmap = Arrays.stream(Line.values()).collect(Collectors.toMap(
        Function.identity(),
        line -> IntPair.create(0, 0)
    ));
    this.winner = Optional.empty();
  }

  public Optional<Marker> makeMove(int col, int row) {
    if (isMoveValid(col, row) && board[col][row] == null) {
      board[col][row] = curTurn;
      List<Line> linesToUpdate = Line.getLinesToUpdate(col, row);

      for (Line line : linesToUpdate) {
        IntPair cur = winmap.get(line);
        IntPair newScore;
        if (curTurn.equals(Marker.X)) {
          newScore = ImmutableIntPair.builder().from(cur).x(cur.getX() + 1).build();
        } else {
          newScore = ImmutableIntPair.builder().from(cur).y(cur.getY() + 1).build();
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

  public abstract Set<IntPair> getNextActions();

  public abstract Map<IntPair, Map<Double, List<Board>>> getNextStates();

  public Map<Line, IntPair> getWinmap() {
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

  public void reset() {
    initialize();
  }
}
