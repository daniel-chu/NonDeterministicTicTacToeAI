package strategy;

public class StrategyFactory {
  public static Strategy createStrategy(String strategy,
                                        int depth,
                                        double learningRate,
                                        double exploreRate,
                                        double discount) {
    switch (strategy) {
      case "human":
        return new HumanStrategy();
      case "search":
        return new SearchStrategy(depth, discount);
      case "qlearning":
        return new QLearningStrategy(learningRate, exploreRate, discount);
      case "random":
        return new RandomStrategy();
      default:
        throw new IllegalArgumentException(String.format("Invalid strategy name: %s, must be one of [human, search, qlearning, random]", strategy));
    }
  }
}
