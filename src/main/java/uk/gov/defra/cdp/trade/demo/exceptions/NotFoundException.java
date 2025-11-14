package uk.gov.defra.cdp.trade.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found. Will be mapped to 404 Not Found by
 * GlobalExceptionHandler.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

  public NotFoundException(){
    super();
  };

  public NotFoundException(String message) {
    super(message);
  }
}
