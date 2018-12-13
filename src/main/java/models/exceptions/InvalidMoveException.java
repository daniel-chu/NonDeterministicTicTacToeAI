package models.exceptions;

/**
 * Created by danielchu on 12/9/18.
 */
public class InvalidMoveException extends RuntimeException {
  public InvalidMoveException(String message) {
    super(message);
  }

  public InvalidMoveException(String message, Throwable e) {
    super(message, e);
  }
}
