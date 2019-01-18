package org.webtrekk.mailsender.controller.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    private String invalid_uri_syntax = "Invalid URI Syntax";


    @ExceptionHandler({
            InvalidURISyntaxException.class,
            org.springframework.web.HttpRequestMethodNotSupportedException.class,
            org.springframework.web.bind.MissingServletRequestParameterException.class
    })
    @Nullable
    public final ResponseEntity<String> handleException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();

        LOGGER.error("Handling " + ex.getClass().getSimpleName() + " due to " + ex.getMessage());

        if (ex instanceof InvalidURISyntaxException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            InvalidURISyntaxException unfe = (InvalidURISyntaxException) ex;

            return handleInvalidURISyntax(unfe, status);
        } else if (ex instanceof MissingServletRequestParameterException)  {
            HttpStatus status = HttpStatus.BAD_REQUEST;

            return handleExceptionInternal("Missing parameter. At least 'to' , 'subject' , 'text' are required", status);

        }

            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleExceptionInternal("Internal server error.", status);
        }



    protected ResponseEntity handleInvalidURISyntax(InvalidURISyntaxException ex,
                                                    HttpStatus status
                                                                    ) {
        return handleExceptionInternal(invalid_uri_syntax,  status);
    }


    protected ResponseEntity<String> handleExceptionInternal(String description,
                                                     HttpStatus status
                                                     ) {

        return  ResponseEntity.status(status).body(description);
    }
}
