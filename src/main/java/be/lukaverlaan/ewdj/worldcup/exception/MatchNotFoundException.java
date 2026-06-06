package be.lukaverlaan.ewdj.worldcup.exception;

public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(String message) {
        super(message);
    }
}
