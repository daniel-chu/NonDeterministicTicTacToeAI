package models;

public enum Marker {
  X, O;

  public static Marker getNext(Marker cur) {
    return cur == X ? O : X;
  }
}
