package variants.game;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import models.IntPair;
import models.Marker;
import models.Player;
import models.exceptions.InvalidMoveException;
import variants.board.Board;

public abstract class BaseTicTacToe implements TicTacToe {
  private Board board;
  private Player playerOne;
  private Player playerTwo;
  private Player curPlayer;

  public BaseTicTacToe(Board board, Player playerOne, Player playerTwo) {
    this.board = board;
    this.playerOne = playerOne;
    this.playerTwo = playerTwo;
  }

  private void initGame() {
    board.initialize();
    this.curPlayer = playerOne;
  }

  public Optional<Marker> play() {
    initGame();
    while (true) {
      IntPair nextRequestedMove = curPlayer.getNextMove(board);
      System.out.println(String.format(
          "Player %s requested move %s\n",
          curPlayer.getMarker(),
          IntPair.getDisplayCoordinatesForPair(nextRequestedMove)
      ));
      try {
        board = getNextBoardForMove(nextRequestedMove);
      } catch (InvalidMoveException e) {
        System.out.println(e.getMessage());
        continue;
      }
      Optional<Marker> maybeWinner = board.getWinner();
      if (maybeWinner.isPresent()) {
        System.out.println(board.getDisplayAsString());
        System.out.println(String.format("%s won!\n", maybeWinner.get().toString()));
        onGameEndCallbacks(maybeWinner);
        return maybeWinner;
      } else if (board.isGameOver()) {
        System.out.println(board.getDisplayAsString());
        System.out.println("Draw!\n");
        onGameEndCallbacks(maybeWinner);
        return Optional.empty();
      }
      curPlayer = curPlayer.equals(playerOne) ? playerTwo : playerOne;
    }
  }

  protected void onGameEndCallbacks(Optional<Marker> maybeWinner) {
    playerOne.onGameEnd(maybeWinner);
    playerTwo.onGameEnd(maybeWinner);
  }

  protected Board getNextBoardForMove(IntPair requestedMove) {
    Map<IntPair, Map<Double, List<Board>>> nextStates = board.getNextStates();
    double rand = Math.random();

    Map<Double, List<Board>> probsToBoards = nextStates.get(requestedMove);
    if (probsToBoards == null) {
      throw new InvalidMoveException("Invalid move, try again.");
    }
    List<Double> probabilities = probsToBoards.keySet().stream()
        .sorted()
        .collect(Collectors.toList());

    for (Double dub : probabilities) {
      rand -= dub;
      if (rand < 0) {
        List<Board> boardsToChooseFrom = probsToBoards.get(dub);
        int selectedIndex = (int) (Math.random() * boardsToChooseFrom.size());
        return boardsToChooseFrom.get(selectedIndex);
      }
    }
    throw new RuntimeException("Error while selecting random move - sum of probabilities for states not equal to 1.0");
  }

  public void reset() {
    board.reset();
    curPlayer = playerOne;
  }

}
