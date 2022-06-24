package tools;

public class BadIdentifierException extends RuntimeException {

    public BadIdentifierException(String identifier) {
        super(String.format("Can't parse identifier '%s'", identifier));
    }


}
