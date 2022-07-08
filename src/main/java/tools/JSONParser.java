package tools;


import java.io.*;
import java.nio.charset.Charset;

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

        TokenizerResult nextToken() throws BadTokenException;

        boolean isFrontBracket(char c);

        boolean isBackBracket(char c);

        boolean bracketMatches(char c1, char c2);

        boolean hasNextToken();
    }

    private abstract static class AbstractJSONTokenizer implements JSONTokenizer {
        protected TokenizerResult nextTokenBuf = new TokenizerResult();


        private boolean isFormatCharacter(char c) {
            return c == ' ' || c == '\t' || c == '\n' || c == '\r';
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

        abstract char nextChar();

        abstract char nextCharNoConsume();

        abstract boolean hasNextChar();


        @Override
        public boolean hasNextToken() {
            return nextTokenBuf != null;
        }

        @Override
        public TokenizerResult nextToken() throws BadTokenException {

            TokenizerResult token = nextTokenBuf != null ? nextTokenBuf.clone() : null;
            TokenType type = TokenType.UNKNOWN;
            StringBuilder stringBuilder = new StringBuilder();
            char c;
            while (hasNextChar()) {
                c = nextChar();
                if (isFormatCharacter(c)) {
                } else if (isBracket(c)) {
                    stringBuilder.append(c);
                    type = TokenType.BRACKET;
                    break;
                } else if (c == '"') {
                    type = TokenType.IDENTIFIER;
                    stringBuilder.append(c);
                    while (hasNextChar()) {
                        c = nextChar();
                        if (c == '\\') {
                            c = nextChar();
                            stringBuilder.append(unescape(c));
                        } else if (c == '"') {
                            stringBuilder.append(c);
                            break;
                        } else {
                            stringBuilder.append(c);
                        }
                    }
                    break;
                } else if (isNumber(c)) {
                    stringBuilder.append(c);
                    while (hasNextChar()) {
                        c = nextCharNoConsume();
                        if (isNumber(c)) {
                            stringBuilder.append(c);
                        } else if (c == '.') {
                            stringBuilder.append(c);
                        } else if (c == 'e' || c == 'E') {
                            stringBuilder.append(c);
                        } else {
                            break;
                        }
                        nextChar();
                    }
                    type = TokenType.IDENTIFIER;
                    break;
                } else if (isAlpha(c)) {
                    stringBuilder.append(c);
                    while (hasNextChar()) {
                        c = nextCharNoConsume();
                        if (isAlpha(c)) {
                            stringBuilder.append(c);
                        } else {
                            break;
                        }
                        nextChar();
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
                    throw new BadTokenException(c);
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
    }


    private static class PlainTextJSONTokenizer extends AbstractJSONTokenizer {

        private String text;
        private int index = 0;

        public PlainTextJSONTokenizer() {
        }

        public void reset(String text, int startIndex) throws BadTokenException {
            this.text = text;
            this.index = startIndex;
            nextToken();
        }

        @Override
        char nextChar() {
            return text.charAt(index++);
        }

        @Override
        char nextCharNoConsume() {
            return text.charAt(index);
        }

        @Override
        boolean hasNextChar() {
            return index < text.length();
        }

    }

    private static class StreamJSONTokenizer extends AbstractJSONTokenizer {
        private BufferedReader reader;
        private char nextCharBuffer;
        private boolean nextCharBufferValid = false;

        public void reset(InputStream inputStream) throws BadTokenException {

            this.reader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));

            nextToken();
        }

        @Override
        char nextChar() {
            if (nextCharBufferValid) {
                nextCharBufferValid = false;
                return nextCharBuffer;
            } else {
                try {
                    int c = reader.read();
                    assert c != -1;
                    return (char) c;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        char nextCharNoConsume() {
            if (nextCharBufferValid) {
                return nextCharBuffer;
            } else {
                try {
                    int c = reader.read();
                    assert c != -1;
                    nextCharBuffer = (char) c;
                    nextCharBufferValid = true;
                    return nextCharBuffer;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        boolean hasNextChar() {
            if (nextCharBufferValid) {
                return true;
            } else {
                try {
                    int c = reader.read();
                    if (c == -1) {
                        return false;
                    } else {
                        nextCharBuffer = (char) c;
                        nextCharBufferValid = true;
                        return true;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
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

    public JSONObject deserialize(String jsonString, boolean sleep) throws BadTokenException {
        try {
            Thread.sleep(sleep ? 5000 : 0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return deserialize(jsonString);
    }

    public JSONObject deserialize(String jsonString) throws BadTokenException {
        InputStream inputStream = new ByteArrayInputStream(jsonString.getBytes());
        return deserialize(inputStream);
    }

    public JSONObject deserialize(InputStream inputStream) throws BadTokenException {
        StreamJSONTokenizer tokenizer = new StreamJSONTokenizer();
        tokenizer.reset(inputStream);
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
