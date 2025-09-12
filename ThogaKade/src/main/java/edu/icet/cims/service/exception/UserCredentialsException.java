package edu.icet.cims.service.exception;

public class UserCredentialsException extends RuntimeException {

  public UserCredentialsException(String message, Throwable cause) {
    super(message, cause);
  }

    public UserCredentialsException(String message) {
      super(message);
    }
}
