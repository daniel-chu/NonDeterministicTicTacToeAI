import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.util.Arrays;
import java.util.List;
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
  private String p1Strategy = "random";

  @Parameter(names = {"-p2", "--playerTwoStrategy"}, description = "Strategy for player two")
  private String p2Strategy = "qlearning";

  @Parameter(names = {"-c", "--moveSuccessChance"}, description = "Chance your move will go where you want it to")
  private double moveSuccessChance = 0.7;

  @Parameter(names = {"-i", "--iterations"}, description = "How many games to play")
  private int iterations = 100;

  @Parameter(names = {"-t", "--trainingIterations"}, description = "How many games a Q learning agent will play before they start getting counted towards score")
  private int trainingIterations = 10000;

  @Parameter(names = {"--depth"}, description = "Learning rate for Q learning agent")
  private int depth = 2;

  @Parameter(names = {"-l", "--learningRate"}, description = "Learning rate for Q learning agent")
  private double learningRate = 0.3;

  @Parameter(names = {"-e", "--exploreRate"}, description = "Exploration rate for Q learning agent")
  private double exploreRate = 0.3;

  @Parameter(names = {"-d", "--discount"}, description = "Discount rate")
  private double discount = 0.95;

  @Parameter(names = {"--trainingAgentStrategy"}, description = "Agent to train against (warning search will be very slow)")
  private String trainingAgentStrategy = "random";

  public void run() {
    System.out.println(String.format(
        "Starting TicTacToe in %s mode for %d iterations.\nPlayer 1: %s\nPlayer 2: %s\n",
        gamemode,
        iterations,
        p1Strategy,
        p2Strategy
    ));

    Player p1 = ImmutablePlayer.builder()
        .marker(Marker.X)
        .strategy(StrategyFactory.createStrategy(p1Strategy, depth, learningRate, exploreRate, discount))
        .build();
    Player p2 = ImmutablePlayer.builder()
        .marker(Marker.O)
        .strategy(StrategyFactory.createStrategy(p2Strategy, depth, learningRate, exploreRate, discount))
        .build();

    List<Player> players = Arrays.asList(p1, p2);
    for (Player player : players) {
      if (player.isReady()) {
        continue;
      }
      Player trainingPlayer = ImmutablePlayer.builder()
          .marker(player.getMarker().equals(Marker.X) ? Marker.O : Marker.X)
          .strategy(StrategyFactory.createStrategy(trainingAgentStrategy, depth, learningRate, exploreRate, discount))
          .build();

      TicTacToe game;
      if (trainingPlayer.getMarker().equals(Marker.X)) {
        game = GameModeFactory.createGameForMode(gamemode, trainingPlayer, p2, moveSuccessChance);
      } else {
        game = GameModeFactory.createGameForMode(gamemode, p1, trainingPlayer, moveSuccessChance);
      }

      int trainingIterationNum = 0;
      while (trainingIterationNum < trainingIterations) {
        System.out.println(String.format("Starting training iteration %d for %s...", trainingIterationNum + 1, player.getMarker()));
        game.reset();
        game.play();
        trainingIterationNum++;
      }
      player.setReady();
    }

    int xWins = 0;
    int oWins = 0;
    int draws = 0;
    int iterationNum = 0;
    while (iterationNum < iterations) {
      System.out.println(String.format("Starting iteration %d...", iterationNum + 1));
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

    System.out.println(String.format("\n\nStats for mode %s:", gamemode, moveSuccessChance));
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
