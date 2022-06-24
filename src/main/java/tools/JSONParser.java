package tools;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class JSONParser {
    private enum TokenType {
        BRACKET, IDENTIFIER, COLON, COMMA, UNKNOWN
    }

    private static class TokenizerResult implements Cloneable {
        public String content;
        public TokenType type;

        @Override
        public TokenizerResult clone() {
            try {
                return (TokenizerResult) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    private interface JSONTokenizer {
        TokenizerResult nextToken();

        boolean isFrontBracket(char c);

        boolean isBackBracket(char c);

        boolean bracketMatches(char c1, char c2);

        boolean hasNextToken();
    }


    private static class PlainTextJSONTokenizer implements JSONTokenizer {

        private String text;
        private TokenizerResult nextTokenBuf = null;
        private int index = 0;

        private boolean isWhiteSpace(char c) {
            return c == ' ' || c == '\t' || c == '\n';
        }

        private boolean isComma(char c) {
            return c == ',';
        }

        private boolean isColon(char c) {
            return c == ':';
        }

        private boolean isBracket(char c) {
            return isFrontBracket(c) || isBackBracket(c);
        }


        @Override
        public boolean isFrontBracket(char c) {
            return c == '{' || c == '[';
        }

        @Override
        public boolean isBackBracket(char c) {
            return c == '}' || c == ']';
        }

        @Override
        public boolean bracketMatches(char c1, char c2) {
            return (c1 == '{' && c2 == '}') || (c1 == '[' && c2 == ']');
        }

        private char unescape(char c) {
            switch (c) {
                case '\\':
                    return '\\';
                case '\"':
                    return '\"';
                case 'n':
                    return '\n';
                case 't':
                    return '\t';
                case 'r':
                    return '\r';
                case 'b':
                    return '\b';
                case 'f':
                    return '\f';
                default:
                    return c;
            }
        }

        private boolean isNumber(char c) {
            return (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '+' || c == 'e' || c == 'E';
        }

        private boolean isAlpha(char c) {
            return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
        }

        public PlainTextJSONTokenizer() {
        }

        public void reset(String text, int startIndex) {
            this.text = text;
            this.index = startIndex;
            nextToken();
        }

        @Override
        public TokenizerResult nextToken() {
            TokenizerResult token = nextTokenBuf != null ? nextTokenBuf.clone() : null;
            TokenType type = TokenType.UNKNOWN;
            StringBuilder stringBuilder = new StringBuilder();
            char c;
            while (index < text.length()) {
                c = text.charAt(index);
                index++;
                if (isWhiteSpace(c)) {
                } else if (isBracket(c)) {
                    stringBuilder.append(c);
                    type = TokenType.BRACKET;
                    break;
                } else if (c == '"') {
                    type = TokenType.IDENTIFIER;
                    stringBuilder.append(c);
                    while (index < text.length()) {
                        c = text.charAt(index);
                        if (c == '\\') {
                            c = text.charAt(index);
                            stringBuilder.append(unescape(c));
                        } else if (c == '"') {
                            stringBuilder.append(c);
                            index++;
                            break;
                        } else {
                            stringBuilder.append(c);
                        }
                        index++;
                    }
                    break;
                } else if (isNumber(c)) {
                    stringBuilder.append(c);
                    while (index < text.length()) {
                        c = text.charAt(index);
                        if (isNumber(c)) {
                            stringBuilder.append(c);
                        } else if (c == '.') {
                            stringBuilder.append(c);
                        } else if (c == 'e' || c == 'E') {
                            stringBuilder.append(c);
                        } else {
                            break;
                        }
                        index++;
                    }
                    type = TokenType.IDENTIFIER;
                    break;
                } else if (isAlpha(c)) {
                    stringBuilder.append(c);
                    while (index < text.length()) {
                        c = text.charAt(index);
                        if (isAlpha(c)) {
                            stringBuilder.append(c);
                        } else {
                            break;
                        }
                        index++;
                    }
                    type = TokenType.IDENTIFIER;
                    break;
                } else if (isComma(c)) {
                    stringBuilder.append(c);
                    type = TokenType.COMMA;
                    break;
                } else if (isColon(c)) {
                    stringBuilder.append(c);
                    type = TokenType.COLON;
                    break;
                } else {
                    throw new BadTokenException(c, index);
                }
            }

            if (stringBuilder.length() <= 0) {
                nextTokenBuf = null;
            } else {
                if (nextTokenBuf == null) {
                    nextTokenBuf = new TokenizerResult();
                }
                nextTokenBuf.content = stringBuilder.toString();
                nextTokenBuf.type = type;
            }
            return token;
        }

        @Override
        public boolean hasNextToken() {
            return nextTokenBuf != null;
        }
    }


    public JSONParser() {
    }

    private JSONObject parseIdentifier(String identifier) {
        if (identifier == null) {
            return null;
        }
        if (identifier.equals("null")) {
            return JSONFactory.getInstance().buildNull();
        } else if (identifier.equals("true") || identifier.equals("false")) {
            return JSONFactory.getInstance().buildBool(identifier);
        } else if (identifier.charAt(0) == '"') {
            return JSONFactory.getInstance().buildString(identifier);
        } else if (identifier.contains(".")) {
            return JSONFactory.getInstance().buildFloat(identifier);
        } else {
            try {
                return JSONFactory.getInstance().buildInteger(identifier);
            } catch (NumberFormatException e) {
                throw new BadIdentifierException(identifier);
            }
        }
    }

    private JSONObject parse(JSONTokenizer tokenizer, char lastBracket) throws BadTokenException {
        JSONFactory.JSONProxyObject proxy = JSONFactory.getInstance().buildCascadeJSON(lastBracket);
        while (tokenizer.hasNextToken()) {
            TokenizerResult token = tokenizer.nextToken();
            switch (token.type) {
                case BRACKET:
                    char bracket = token.content.charAt(0);
                    if (tokenizer.bracketMatches(lastBracket, bracket)) {
                        return proxy.build();
                    } else {
                        if (tokenizer.isBackBracket(bracket)) {
                            throw new BadTokenException(bracket);
                        } else if (tokenizer.isFrontBracket(bracket)) {
                            proxy.next(parse(tokenizer, bracket));
                        }
                    }
                    break;
                case IDENTIFIER:
                    proxy.next(parseIdentifier(token.content));
                    break;
                case COLON:
                    // fall through
                case COMMA:
                    break;
                case UNKNOWN:
                    throw new BadTokenException(token.content);
            }
        }
        return proxy.build();
    }

    public JSONObject deserialize(String jsonString, boolean sleep) {
        try {
            Thread.sleep(sleep ? 5000 : 0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return deserialize(jsonString);
    }

    public JSONObject deserialize(String jsonString) {
        PlainTextJSONTokenizer tokenizer = new PlainTextJSONTokenizer();
        tokenizer.reset(jsonString, 0);
        TokenizerResult token = tokenizer.nextToken();
        if (token == null) {
            return null;
        }
        if (!tokenizer.hasNextToken()) {
            return new JSONObject();
        } else if (token.type != TokenType.BRACKET ||
                !(token.content.equals("{") || token.content.equals("["))) {
            throw new BadTokenException(token.content);
        }
        return parse(tokenizer, token.content.charAt(0));
    }

}
