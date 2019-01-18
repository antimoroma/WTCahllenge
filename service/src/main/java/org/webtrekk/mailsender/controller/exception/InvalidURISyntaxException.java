package org.webtrekk.mailsender.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason="URI Sintax exception")
public class InvalidURISyntaxException  extends RuntimeException{
}
