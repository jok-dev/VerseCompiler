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
            // eat new lines
            while (advanceIfAny(NEW_LINE)) { }

            if (isAtEnd()) {
                break;
            }

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
        advanceIfAnyOrError("Unexpected {peekNext} following expression", SEMICOLON, NEW_LINE, EOF);
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr value = expression();
        advanceIfAnyOrError("Unexpected {peekNext} following expression", SEMICOLON, NEW_LINE, EOF);
        return new Stmt.Expression(value);
    }

    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        Expr expr = comparison();

        while (advanceIfAny(EQUALS)) {
            Token operator = peekPrevious();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = addition();

        while (advanceIfAny(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = peekPrevious();
            Expr right = addition();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr addition() {
        Expr expr = multiplication();

        while (advanceIfAny(PLUS, MINUS)) {
            Token operator = peekPrevious();
            Expr right = multiplication();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr multiplication() {
        Expr expr = unary();

        while (advanceIfAny(STAR, SLASH)) {
            Token operator = peekPrevious();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (advanceIfAny(MINUS, NOT)) {
            Token operator = peekPrevious();
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
            return new Expr.Literal(peekPrevious().literal);
        }

        if (advanceIfAny(LEFT_PAREN)) {
            Expr expr = expression();
            advanceIfAnyOrError("Expected ')' after expression", RIGHT_PAREN);
            return new Expr.Grouping(expr);
        }

        throw error("Expected expression, instead got {peek}");
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (peekPrevious().type == SEMICOLON || peekPrevious().type == NEW_LINE) {
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

    private boolean check(TokenType type) {
        if (type != EOF && isAtEnd()) {
            return false;
        }

        return peek().type == type;
    }

    private @Nullable Token checkIfAny(TokenType... anyOfTypes) {
        for (TokenType type : anyOfTypes) {
            if (check(type)) {
                return advance();
            }
        }

        return null;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }

        return peekPrevious();
    }

    private boolean advanceIfAny(TokenType... anyOfTypes) {
        return checkIfAny(anyOfTypes) != null;
    }

    private Token advanceIfAnyOrError(String message, TokenType... anyOfTypes) {
        Token token = checkIfAny(anyOfTypes);
        if (token != null) {
            return token;
        }

        throw error(message);
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private @Nullable Token peekNext() {
        return current + 1 >= tokens.size() ? null : tokens.get(current + 1);
    }

    private @NotNull Token peekPrevious() {
        return tokens.get(current - 1);
    }

    private SyntaxError error(String message) {
        VerseLang.syntaxError(peek(), peekNext(), message);
        return new SyntaxError();
    }

    private static class SyntaxError extends RuntimeException { }

}
