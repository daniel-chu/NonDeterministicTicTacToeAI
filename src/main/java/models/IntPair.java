package models;

import org.immutables.value.Value;

@Value.Immutable
public interface IntPair {
  int getX();
  int getY();

  static IntPair create(int x, int y) {
    return ImmutableIntPair.builder().x(x).y(y).build();
  }

  static String getDisplayCoordinatesForPair(IntPair pair) {
    char colSymbol = pair.getX() == 0
        ? 'A'
        : (pair.getX() == 1 ? 'B' : 'C');
    return String.format("%s%d", colSymbol, pair.getY() + 1);
  }
}
