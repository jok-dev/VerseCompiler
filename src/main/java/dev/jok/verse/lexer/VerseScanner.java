package dev.jok.verse.lexer;

import dev.jok.verse.VerseLang;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class VerseScanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }


        addToken(TokenType.EOF);
        return tokens;
    }

    private void scanToken() {
        char c = advance();

        switch (c) {
            case '(' -> addToken(TokenType.LEFT_PAREN);
            case ')' -> addToken(TokenType.RIGHT_PAREN);
            case '{' -> addToken(TokenType.LEFT_BRACE);
            case '}' -> addToken(TokenType.RIGHT_BRACE);
            case '[' -> addToken(TokenType.LEFT_BRACKET);
            case ']' -> addToken(TokenType.RIGHT_BRACKET);

            case ',' -> addToken(TokenType.COMMA);
            case '.' -> addToken(TokenType.DOT);
            case ';' -> addToken(TokenType.SEMICOLON);
            case ':' -> addToken(TokenType.COLON);

            case '=' -> addToken(TokenType.EQUALS);
            case '+' -> addToken(TokenType.PLUS);
            case '-' -> addToken(TokenType.MINUS);
            case '*' -> addToken(TokenType.STAR);
            case '/' -> addToken(TokenType.SLASH);

            case '>' -> addToken(advanceIf('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
            case '<' -> addToken(advanceIf('=') ? TokenType.LESS_EQUAL : TokenType.LESS);

            case '"' -> string();

            // # is comments
            case '#' -> advanceTillEol();

            case ' ', '\r', '\t', '\n' -> {
                // Ignore whitespace.
            }

            default -> {
                if (Character.isDigit(c)) {
                    number();
                } else if (isIdentifierStart(c)) {
                    identifier();
                } else {
                    error("Unexpected character: '" + c + "'");
                }
            }
        }
    }

    private void number() {
        boolean isFloat = false;
        while (Character.isDigit(peek())) {
            advance();
        }

        // Look for a decimal point
        if (peek() == '.' && Character.isDigit(peekNext())) {
            isFloat = true;

            // Consume the "."
            advance();

            while (Character.isDigit(peek())) {
                advance();
            }
        }

        String text = source.substring(start, current);
        if (isFloat) {
            addToken(TokenType.NUMBER_FLOAT, Float.parseFloat(text));
            return;
        }

        addToken(TokenType.NUMBER_INT, Integer.parseInt(text));
    }

    private void identifier() {
        while (isIdentifierPart(peek())) {
            advance();
        }

        String text = source.substring(start, current);
        TokenType type = TokenType.IDENTIFIER;
        if (text.equals("and")) type = TokenType.AND;
        if (text.equals("or")) type = TokenType.OR;
        if (text.equals("not")) type = TokenType.NOT;
        if (text.equals("true")) type = TokenType.TRUE;
        if (text.equals("false")) type = TokenType.FALSE;
        if (text.equals("var")) type = TokenType.VAR;
        if (text.equals("return")) type = TokenType.RETURN;
        if (text.equals("self")) type = TokenType.SELF;
        if (text.equals("if")) type = TokenType.IF;
        if (text.equals("while")) type = TokenType.WHILE;
        if (text.equals("for")) type = TokenType.FOR;

        addToken(type);
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }

            advance();
        }

        if (isAtEnd()) {
            error("Unterminated string");
            return;
        }

        // The closing "
        advance();

        // Trim the surrounding quotes
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private boolean isIdentifierStart(char c) {
        return Character.isAlphabetic(c) || c == '_';
    }

    private boolean isIdentifierPart(char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c) || c == '_';
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }

        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }

        return source.charAt(current + 1);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void advanceTillEol() {
        while (peek() != '\n' && !isAtEnd()) {
            advance();
        }
    }

    private boolean advanceIf(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, @Nullable Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void error(String message) {
        VerseLang.syntaxError(line, message);
    }


}
