package be.lukaverlaan.ewdj.worldcup.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TeamNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleTeamNotFound(TeamNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "error.team.notfound.title");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(MatchNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleMatchNotFound(MatchNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "error.match.notfound.title");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResource(Model model) {
        model.addAttribute("errorTitle", "error.404.title");
        model.addAttribute("errorMessage", "error.404.message");
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("errorTitle", "error.generic.title");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }
}
