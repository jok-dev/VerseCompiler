package dev.jok.verse.lexer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Token {

    public final TokenType type;
    public final String lexeme;
    public final Object literal;
    public final int line;
    public final int col;

    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }

    public CharSequence errorString() {
        if (type == TokenType.EOF) {
            return "end of file";
        }

        if (type.isSelfDescribing()) {
            return type.toString();
        }

        return type + " `" + lexeme + "`";
    }
}
