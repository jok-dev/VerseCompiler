package dev.jok.verse.lexer;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public enum TokenType {

    // Punctuation
    LEFT_PAREN("("), RIGHT_PAREN(")"), LEFT_BRACE("{"), RIGHT_BRACE("}"), LEFT_BRACKET("["), RIGHT_BRACKET("]"),
    COMMA(","), DOT("."), SEMICOLON(";"), COLON(":"), NEW_LINE("\\n"),

    // Math
    EQUALS("="), PLUS("+"), MINUS("-"), STAR("*"), SLASH("/"), INFERRED_DECLARATION_TYPE(":="),
    GREATER(">"), GREATER_EQUAL(">="), LESS("<"), LESS_EQUAL("<="),

    // Literals
    IDENTIFIER, STRING, NUMBER_INT, NUMBER_FLOAT,

    // Keywords
    AND, OR, NOT, TRUE, FALSE,
    VAR, RETURN, SELF,
    IF, WHILE, FOR,

    // Block statements
    BLOCK, SPAWN,

    // @Todo(Jok): remove
    // Temporary
    PRINT,

    EOF;

    private final @NotNull String pretty;
    private @Getter boolean punctuation = false;

    TokenType() {
        this.pretty = name().toLowerCase();
    }

    TokenType(@NotNull String pretty) {
        this.pretty = pretty;
        this.punctuation = true;
    }

    @Override
    public String toString() {
        return punctuation ? "`" + pretty + "`" : pretty;
    }

}
