package uk.gov.defra.cdp.trade.demo.exceptions;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(MockitoExtension.class)
class RestResponseEntityExceptionHandlerTest {

  private static final String FILTER_TOO_SHORT_MESSAGE = "Filter must be a minimum of 3 characters";

  @Mock
  private Appender mockAppender;

  @Captor
  private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

  @Mock
  WebRequest mockWebRequest;

  @BeforeEach
  void setUp() {
    final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    logger.addAppender(mockAppender);
  }

  @AfterEach
  void teardown() {
    final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    logger.detachAppender(mockAppender);
  }

  @Test
  void logsNotFoundException() {
    //Given
    RestResponseEntityExceptionHandler exceptionHandler = new RestResponseEntityExceptionHandler();
    Exception ex = new Exception();
    when(mockWebRequest.getDescription(false)).thenReturn("some description");

    //When
    exceptionHandler.handleNotFound(ex, mockWebRequest);

    //Then
    verify(mockAppender).doAppend(captorLoggingEvent.capture());
    final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(),
            is("org.springframework.web.servlet.PageNotFound : some description"));
  }

  @Test
  void notFoundExceptionReturnsCorrectHttpResponse() {
    //Given
    RestResponseEntityExceptionHandler exceptionHandler = new RestResponseEntityExceptionHandler();
    Exception ex = new Exception();

    //When
    ResponseEntity<Object> response = exceptionHandler.handleNotFound(ex, mockWebRequest);

    //Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("", response.getBody());
  }

  @Test
  void filterTooShortException_ReturnsCorrectHttpResponse() {
    // Given
    RestResponseEntityExceptionHandler exceptionHandler = new RestResponseEntityExceptionHandler();
    Exception ex = new Exception(FILTER_TOO_SHORT_MESSAGE);

    // When
    var response = exceptionHandler.handleFilterTooShort(ex, mockWebRequest);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(FILTER_TOO_SHORT_MESSAGE, response.getBody());
  }
}
