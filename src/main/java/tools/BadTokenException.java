package tools;

public class BadTokenException extends Exception {
    public String token;

    public BadTokenException(char c, int index) {
        super(String.format("Bad character '%c' at index %d", c, index));
        this.token = "" + c;
    }

    public BadTokenException(char c) {
        super(String.format("Bad character '%c'", c));
        this.token = "" + c;
    }

    public BadTokenException(String token) {
        super(String.format("Unexpected token: '%s'", token));
        this.token = token;
    }


}
