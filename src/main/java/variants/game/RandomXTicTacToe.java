package variants.game;

import models.Player;
import variants.board.RandomXBoard;

public class RandomXTicTacToe extends BaseTicTacToe {
  public RandomXTicTacToe(Player playerOne, Player playerTwo, double moveSuccessChance) {
    super(new RandomXBoard(moveSuccessChance), playerOne, playerTwo);
  }
}
