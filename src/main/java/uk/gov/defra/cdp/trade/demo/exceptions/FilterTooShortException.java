package uk.gov.defra.cdp.trade.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FilterTooShortException extends RuntimeException {
  public FilterTooShortException(String message) {
    super(message);
  }
}
