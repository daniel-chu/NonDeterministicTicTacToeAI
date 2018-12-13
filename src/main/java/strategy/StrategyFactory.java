package strategy;

import java.util.Optional;

import strategy.learning.QLearningStrategy;
import strategy.search.SearchStrategy;

public class StrategyFactory {
  public static Strategy createStrategy(String strategy,
                                        int depth,
                                        Optional<Double> learningRate,
                                        Optional<Double> exploreRate,
                                        double discount) {
    switch (strategy) {
      case "human":
        return new HumanStrategy();
      case "search":
        return new SearchStrategy(depth, discount);
      case "qlearning":
        return new QLearningStrategy(learningRate.orElse(0.3), exploreRate.orElse(0.2), discount);
      case "random":
        return new RandomStrategy();
      default:
        throw new IllegalArgumentException(String.format("Invalid strategy name: %s, must be one of [human, search, qlearning]", strategy));
    }
  }
}
