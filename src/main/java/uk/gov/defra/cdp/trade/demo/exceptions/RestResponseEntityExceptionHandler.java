package uk.gov.defra.cdp.trade.demo.exceptions;

import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger EXCEPTION_LOGGER = LoggerFactory
      .getLogger(RestResponseEntityExceptionHandler.class);
  
  @ExceptionHandler(value = {NotFoundException.class, NoSuchElementException.class})
  protected ResponseEntity<Object> handleNotFound(Exception ex, WebRequest request) {
    EXCEPTION_LOGGER.info("{} : {}", PAGE_NOT_FOUND_LOG_CATEGORY, request.getDescription(false));
    return handleExceptionInternal(ex, "", new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(value = {FilterTooShortException.class})
  protected ResponseEntity<Object> handleFilterTooShort(Exception ex, WebRequest request) {
    EXCEPTION_LOGGER.info("Filter too short", ex);
    return handleExceptionInternal(
        ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }
}
