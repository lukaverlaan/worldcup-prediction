package be.lukaverlaan.ewdj.worldcup.exception;

public record ErrorResponse(int status, String message, String timestamp) {
}
