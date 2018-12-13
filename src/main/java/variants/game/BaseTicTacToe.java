package variants.game;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import models.Marker;
import models.Pair;
import models.Player;
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
      Pair nextRequestedMove = curPlayer.getNextMove(board);
      System.out.println(String.format(
          "Player %s requested move %s\n",
          curPlayer.getMarker(),
          Pair.getDisplayCoordinatesForPair(nextRequestedMove)
      ));
      board = getNextBoardForMove(nextRequestedMove);
      Optional<Marker> winner = board.getWinner();
      if (winner.isPresent()) {
        System.out.println(board.getDisplayAsString());
        System.out.println(String.format("%s won!\n", winner.get().toString()));
        return winner;
      } else if (board.isGameOver()) {
        System.out.println(board.getDisplayAsString());
        System.out.println("Draw!\n");
        return Optional.empty();
      }
      curPlayer = curPlayer.equals(playerOne) ? playerTwo : playerOne;
    }
  }

  protected Board getNextBoardForMove(Pair requestedMove) {
    Map<Pair, Map<Double, List<Board>>> nextStates = board.getNextStates();
    double rand = Math.random();

    Map<Double, List<Board>> probsToBoards = nextStates.get(requestedMove);
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

}
