package dev.jok.verse.lexer;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public enum TokenType {

    // Punctuation
    LEFT_PAREN("("), RIGHT_PAREN(")"), LEFT_BRACE("{"), RIGHT_BRACE("}"), LEFT_BRACKET("["), RIGHT_BRACKET("]"),
    COMMA(","), DOT("."), SEMICOLON(";"), COLON(":"), QUESTION_MARK("?"), NEW_LINE("\\n"),

    // Math
    EQUALS("="), PLUS("+"), MINUS("-"), STAR("*"), SLASH("/"), INFERRED_DECL(":="),
    GREATER(">"), GREATER_EQUAL(">="), LESS("<"), LESS_EQUAL("<="),

    // Literals
    IDENTIFIER, STRING, NUMBER_INT, NUMBER_FLOAT,

    // Keywords
    AND(true), OR(true), NOT(true), TRUE(true), FALSE(true),
    VAR(true), RETURN(true), SELF(true),
    IF(true), ELSE(true), FOR(true), BREAK(true),

    CLASS(true), MODULE(true),

    // Effects
    TRANSACTS, VARIES, COMPUTES, CONVERGES,

    // Effect specifiers
    SUSPENDS, DECIDES,

    // Access specifiers
    PUBLIC, INTERNAL, PROTECTED, PRIVATE,

    // Attributes
    OVERRIDE, ABSTRACT, FINAL, UNIQUE,

    // Reserved keywords
    CONTINUE, YIELD,

    // Block statements
    BLOCK, SPAWN,

    EOF;

    private final @NotNull String pretty;
    private @Getter boolean punctuation = false;
    private @Getter boolean keyword = false;

    TokenType() {
        this.pretty = name().toLowerCase();
    }

    TokenType(boolean keyword) {
        this();
        this.keyword = keyword;
    }

    TokenType(@NotNull String pretty) {
        this.pretty = pretty;
        this.punctuation = true;
    }

    public boolean isSelfDescribing() {
        return punctuation || keyword;
    }

    @Override
    public String toString() {
        if (keyword) {
            return "keyword `" + pretty + "`";
        }

        return punctuation ? "`" + pretty + "`" : pretty;
    }
}
