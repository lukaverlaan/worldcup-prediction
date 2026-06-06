package be.lukaverlaan.ewdj.worldcup.exception;

import be.lukaverlaan.ewdj.worldcup.controller.rest.MatchRestController;
import be.lukaverlaan.ewdj.worldcup.controller.rest.StadiumRestController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = {MatchRestController.class, StadiumRestController.class})
public class RestExceptionHandler {

    @ExceptionHandler(MatchNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse matchNotFoundHandler(MatchNotFoundException ex) {
        return new ErrorResponse(404, ex.getMessage(), LocalDateTime.now().toString());
    }

    @ExceptionHandler(TeamNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse teamNotFoundHandler(TeamNotFoundException ex) {
        return new ErrorResponse(404, ex.getMessage(), LocalDateTime.now().toString());
    }
}
