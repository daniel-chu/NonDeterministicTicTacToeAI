package strategy;

import java.util.Optional;

import strategy.learning.QLearningStrategy;
import strategy.search.StandardGameSimpleSearch;

public class StrategyFactory {
  public static Strategy createStrategy(String strategy, Optional<Double> exploreRate) {
    switch (strategy) {
      case "human":
        return new HumanStrategy();
      case "search":
        return new StandardGameSimpleSearch();
      case "qlearning":
        return new QLearningStrategy(1.0);
      default:
        throw new IllegalArgumentException(String.format("Invalid strategy name: %s, must be one of [human, search, qlearning]", strategy));
    }
  }
}
