package variants;

import models.Player;
import variants.game.RandomXTicTacToe;
import variants.game.StandardTicTacToe;
import variants.game.TicTacToe;

public class GameModeFactory {
  public static TicTacToe createGameForMode(String gamemode, Player p1, Player p2, double moveSuccessChance) {
    switch (gamemode) {
      case "standard":
        return new StandardTicTacToe(p1, p2);
      case "randomX":
        return new RandomXTicTacToe(p1, p2, moveSuccessChance);
      default:
        throw new IllegalArgumentException(String.format("Invalid gamemode name: %s, must be one of [standard, randomX]", gamemode));
    }
  }
}
