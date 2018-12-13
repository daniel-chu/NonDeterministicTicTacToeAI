package models;

import org.immutables.value.Value;

@Value.Immutable
public interface Pair {
  int getX();
  int getY();

  static Pair create(int x, int y) {
    return ImmutablePair.builder().x(x).y(y).build();
  }

  static String getDisplayCoordinatesForPair(Pair pair) {
    char colSymbol = pair.getX() == 0
        ? 'A'
        : (pair.getX() == 1 ? 'B' : 'C');
    return String.format("%s%d", colSymbol, pair.getY() + 1);
  }
}
