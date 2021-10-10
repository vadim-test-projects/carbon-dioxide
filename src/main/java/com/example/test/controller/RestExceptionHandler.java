package com.example.test.controller;

import com.example.test.exception.IllegalDatabaseState;
import com.example.test.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    public static final String REQUEST_NOT_CORRECT = "The request is not correct";

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(final WebRequest request, final HttpMessageNotReadableException e) {
        return logAndGetErrorResponseEntity(
                request, HttpStatus.BAD_REQUEST, e, REQUEST_NOT_CORRECT);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleHttpRequestMethodNotSupportedException(final WebRequest request,
                                                                                 final HttpRequestMethodNotSupportedException e) {
        StringBuilder message = new StringBuilder();
        message.append(e.getMethod());
        message.append(" method is not supported for this request. Supported methods are ");
        val httpMethods = Optional.ofNullable(e.getSupportedHttpMethods());

        httpMethods.ifPresent(methods -> methods.forEach(t -> message.append(t).append(" ")));

        return logAndGetErrorResponseEntity(request, HttpStatus.METHOD_NOT_ALLOWED, e, message.toString());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> checkHttpMediaTypeNotSupportedException(final WebRequest request,
                                                                          final HttpMediaTypeNotSupportedException e) {
        StringBuilder message = new StringBuilder();
        message.append(e.getContentType());
        message.append(" media type is not supported. Supported media types are ");

        e.getSupportedMediaTypes().forEach(t -> message.append(t).append(" "));

        return logAndGetErrorResponseEntity(request, HttpStatus.UNSUPPORTED_MEDIA_TYPE, e, message.toString());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNoHandlerFoundException(final WebRequest request, final NoHandlerFoundException e) {
        return logAndGetErrorResponseEntity(request, HttpStatus.BAD_REQUEST, e, e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(final WebRequest request, final ResourceNotFoundException e) {
        return logAndGetErrorResponseEntity(request, HttpStatus.BAD_REQUEST, e, e.getMessage());
    }

    @ExceptionHandler(IllegalDatabaseState.class)
    public ResponseEntity<String> handleIllegalDataBaseState(final WebRequest request, final IllegalDatabaseState e) {
        return logAndGetErrorResponseEntity(request, HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(final WebRequest request, final Exception e) {
        return logAndGetErrorResponseEntity(request, HttpStatus.INTERNAL_SERVER_ERROR, e, "Unknown error");
    }

    private ResponseEntity<String> logAndGetErrorResponseEntity(final WebRequest request, final HttpStatus httpStatus,
                                                                  final Exception e, final String message) {
        val requestDescription = request.getDescription(true);

        log.error(String.format("Error at request: %s", requestDescription), ExceptionUtils.getRootCause(e));

        val headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.status(httpStatus).headers(headers).body(message);
    }
}
