package dev.jok.verse.parser;

import dev.jok.verse.ast.Expr;
import dev.jok.verse.VerseLang;
import dev.jok.verse.ast.Stmt;
import dev.jok.verse.lexer.Token;
import dev.jok.verse.lexer.TokenType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static dev.jok.verse.lexer.TokenType.*;

@RequiredArgsConstructor
public class VerseParser {

    private final List<Token> tokens;
    private int current = 0;

    public @NotNull List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();

        while (!isAtEnd()) {
            statements.add(statement());
        }

        return statements;
    }

    private Stmt statement() {
        if (advanceIfAny(PRINT)) {
            return printStatement();
        }

        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consumeOrError(SEMICOLON, "Expected ';' after value");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr value = expression();
        consumeOrError(SEMICOLON, "Expected ';' after value");
        return new Stmt.Expression(value);
    }

    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        Expr expr = comparison();

        while (advanceIfAny(EQUALS)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = addition();

        while (advanceIfAny(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = addition();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr addition() {
        Expr expr = multiplication();

        while (advanceIfAny(PLUS, MINUS)) {
            Token operator = previous();
            Expr right = multiplication();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr multiplication() {
        Expr expr = unary();

        while (advanceIfAny(STAR, SLASH)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        // @Todo(Jok): implement "not" keyword
        if (advanceIfAny(MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() {
        if (advanceIfAny(FALSE)) {
            return new Expr.Literal(false);
        }

        if (advanceIfAny(TRUE)) {
            return new Expr.Literal(true);
        }

        if (advanceIfAny(NUMBER_INT, NUMBER_FLOAT, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (advanceIfAny(LEFT_PAREN)) {
            Expr expr = expression();
            consumeOrError(RIGHT_PAREN, "Expected ')' after expression");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expected expression, instead got " + peek().type);
    }

    // @Todo(Jok) @Important: since semicolons are optional, this synchronization doesn't actually work for Verse
    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) {
                return;
            }

            // @Todo(Jok) @Important: add more keywords
            switch (peek().type) {
                case VAR, FOR, IF, WHILE, RETURN -> {
                    return;
                }
            }

            advance();
        }
    }

    private boolean advanceIfAny(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }

        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }

        return previous();
    }

    private Token consumeOrError(TokenType type, String message) {
        if (peek().type == type) {
            return advance();
        }

        throw error(peek(), message);
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        VerseLang.syntaxError(token.line, message);
        return new ParseError();
    }

    private static class ParseError extends RuntimeException { }

}
