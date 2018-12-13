import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.util.Optional;

import models.ImmutablePlayer;
import models.Marker;
import models.Player;
import strategy.StrategyFactory;
import variants.GameModeFactory;
import variants.game.TicTacToe;

public class Runner {
  @Parameter(names = {"-g", "--gamemode"}, description = "game mode to play")
  private String gamemode = "standard";

  @Parameter(names = {"-p1", "--playerOneStrategy"}, description = "Strategy for player one")
  private String p1Strategy = "human";

  @Parameter(names = {"-p2", "--playerTwoStrategy"}, description = "Strategy for player two")
  private String p2Strategy = "simplesearch";

  @Parameter(names = {"-c", "--moveSuccessChance"}, description = "Chance your move will go where you want it to")
  private double moveSuccessChance = 0.7;

  @Parameter(names = {"-i", "--iterations"}, description = "How many games to play")
  private int iterations = 1;

  @Parameter(names = {"-t", "--trainingIterations"}, description = "How many games a Q learning agent will play before they start getting counted towards score")
  private int trainingIterations = 1;

  @Parameter(names = {"-e", "--exploreRate"}, description = "Exploration rate for Q learning agent")
  private double exploreRate = -1;

  public void run() {
    System.out.println(String.format(
        "Starting TicTacToe in %s mode for %d iterations.\nPlayer 1: %s\nPlayer 2: %s\n",
        gamemode,
        iterations,
        p1Strategy,
        p2Strategy
    ));

    Optional<Double> maybeExplorationRate = exploreRate < 0 ? Optional.empty() : Optional.of(exploreRate);
    Player p1 = ImmutablePlayer.builder()
        .marker(Marker.X)
        .strategy(StrategyFactory.createStrategy(p1Strategy, maybeExplorationRate))
        .build();
    Player p2 = ImmutablePlayer.builder()
        .marker(Marker.O)
        .strategy(StrategyFactory.createStrategy(p2Strategy,maybeExplorationRate))
        .build();

    int xWins = 0;
    int oWins = 0;
    int draws = 0;
    int iterationNum = 0;
    while (iterationNum < iterations) {
      System.out.println(String.format("Starting iteration %d...", iterationNum));
      TicTacToe game = GameModeFactory.createGameForMode(gamemode, p1, p2, moveSuccessChance);
      Optional<Marker> winner = game.play();
      iterationNum++;

      if (winner.isPresent()) {
        if (winner.get().equals(Marker.X)) {
          xWins++;
        } else {
          oWins++;
        }
      } else {
        draws++;
      }
    }

    System.out.println(String.format("\n\nStats for mode %s with %.2f move success chance:", gamemode, moveSuccessChance));
    System.out.println(String.format("X wins: %d", xWins));
    System.out.println(String.format("O wins: %d", oWins));
    System.out.println(String.format("Draws: %d", draws));
  }

  public static void main(String[] args) {
    Runner runner = new Runner();
    JCommander.newBuilder()
        .addObject(runner)
        .build()
        .parse(args);
    runner.run();
  }
}
