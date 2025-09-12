package edu.icet.cims.service.exception;

public class ItemServiceException extends RuntimeException {

  public ItemServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public ItemServiceException(String message) {
    super(message);
  }
}
