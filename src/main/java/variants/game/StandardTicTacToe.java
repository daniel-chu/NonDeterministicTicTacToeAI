package variants.game;

import models.Player;
import variants.board.StandardBoard;

public class StandardTicTacToe extends BaseTicTacToe {
  public StandardTicTacToe(Player playerOne, Player playerTwo) {
    super(new StandardBoard(), playerOne, playerTwo);
  }
}
