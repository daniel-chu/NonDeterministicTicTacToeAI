package strategy;

import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.IntPair;
import models.Marker;
import variants.board.Board;

public class HumanStrategy implements Strategy {
  private static final Pattern MOVE_MATCHER = Pattern.compile("^([ABCabc])([123])$");

  private final Scanner scanner;

  public HumanStrategy() {
    this.scanner = new Scanner(new InputStreamReader(System.in));
  }

  public IntPair getNextMove(Board board, Marker ownMarker) {
    System.out.println(board.getDisplayAsString());
    System.out.println("Input your next move: ");
    while (scanner.hasNext()) {
      String nextMove = scanner.next();
      Matcher moveMatcher = MOVE_MATCHER.matcher(nextMove);

      if (moveMatcher.find()) {
        char col = moveMatcher.group(1).toLowerCase().charAt(0);
        int colInt = Character.getNumericValue(col) - 10;
        int row = Integer.parseInt(moveMatcher.group(2)) - 1;
        return IntPair.create(colInt, row);
      } else {
        System.out.println("Invalid move (format must match something like A1), try again:");
      }
    }
    throw new RuntimeException("wtf?");
  }

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void prep() {
    // nothing needs to be done for human player
  }
  @Override
  public void onGameEnd(Optional<Boolean> didWin) {
    // nothing needs to be done for human player
  }



}
