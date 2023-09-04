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
import java.util.Set;

import static dev.jok.verse.lexer.TokenType.*;

@RequiredArgsConstructor
public class VerseParser {

    private static final Set<TokenType> VALID_STATEMENT_ENDS = Set.of(RIGHT_BRACE, SEMICOLON, NEW_LINE, EOF);

    private final boolean debug;
    private final List<Token> tokens;
    private int current = 0;

    public @NotNull List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();

        while (!eatBlankLines()) {
            statements.add(declaration());
            advanceExpressionEnd();
        }

        return statements;
    }

    private List<Stmt> anonymousBlock() {
        List<Stmt> statements = new ArrayList<>();

        while (!eatBlankLines()) {
            if (peekIs(RIGHT_BRACE)) {
                break;
            }

            statements.add(declaration());
            advanceExpressionEnd();
        }

        advanceExpectToken(RIGHT_BRACE, "after block");
        return statements;
    }

    private Stmt declaration() {
        try {
            if (advanceIfAny(VAR)) {
                return mutableDeclaration();
            }

            // function declaration
            if (peekIsFunctionDeclaration()) {
                return functionDeclaration();
            }

            return statement();
        } catch (SyntaxError error) {
            // @Todo(Jok): BAD!
            if (debug) {
                error.printStackTrace();
            }

            synchronize();
            return null;
        }
    }

    private Stmt functionDeclaration() {
        Token name = advanceExpectToken(IDENTIFIER, "in function declaration");
        advanceExpectToken(LEFT_PAREN, "in function declaration");

        List<Stmt.Parameter> parameters = new ArrayList<>();
        if (!peekIs(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    throw error("Cannot have more than 255 parameters");
                }

                Token parameterName = advanceExpectToken(IDENTIFIER, "in function declaration");
                advanceExpectToken(COLON, "in function parameter declaration");
                Token parameterType = advanceExpectToken(IDENTIFIER, "in function parameter declaration");

                parameters.add(new Stmt.Parameter(parameterName, parameterType));
            } while (advanceIfAny(COMMA));
        }

        advanceExpectToken(RIGHT_PAREN, "in function declaration");
        advanceExpectToken(COLON, "in function declaration");
        Token returnType = advanceExpectToken("return type", IDENTIFIER, "in function declaration");
        advanceExpectToken(EQUALS, "in function declaration");

        // @Todo(Jok): don't enforce braces
        advanceExpectToken(LEFT_BRACE, "in function declaration");
        List<Stmt> body = anonymousBlock();

        return new Stmt.Function(name, returnType, parameters, body);
    }

    private boolean peekIsFunctionDeclaration() {
        if (!peekIsConsecutive(IDENTIFIER, LEFT_PAREN)) {
            return false;
        }

        int advance = 2;
        while (true) {
            Token ahead = peek(advance);
            if (ahead == null) {
                return false;
            }

            if (ahead.type == RIGHT_PAREN) {
                Token after = peek(advance + 1);
                return after != null && after.type == COLON;
            }

            // if the statement ends, it's not a function declaration
            if (VALID_STATEMENT_ENDS.contains(ahead.type)) {
                return false;
            }

            advance++;
        }
    }

    private Stmt mutableDeclaration() {
        Token identifier = advanceExpectToken(IDENTIFIER);

        // mutables don't support inferred types currently
        errorIfPeekIs("Missing type for `^` or `var` definition", INFERRED_DECLARATION_TYPE);

        advanceExpectToken(COLON, "in var definition");

        Token type = advanceExpectToken("type identifier", IDENTIFIER, "after ':' in var definition");

        advanceExpectToken(EQUALS, "in var definition");

        Expr initializer = expression();
        return new Stmt.VariableDeclaration(identifier, type, initializer, true);
    }

    private Stmt statement() {
        if (advanceIfAny(PRINT)) return printStatement();

        // block statements
        if (advanceThroughIfConsecutive(BLOCK, LEFT_BRACE)) return new Stmt.Block(anonymousBlock());



        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr value = expression();
        return new Stmt.Expression(value);
    }

    private void advanceExpressionEnd() {
        // } ends the expression
        if (peekIs(RIGHT_BRACE)) {
            return;
        }

        advanceIfAnyOrError("Unexpected {peek} following expression", SEMICOLON, NEW_LINE, EOF);
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = equality();

        if (advanceIfAny(EQUALS)) {
            Expr value = assignment();

            // @Todo(Jok) @Feat: add support for other assignment targets
            if (expr instanceof Expr.Variable variable) {
                Token name = variable.name;
                return new Expr.Assign(name, value);
            }

            // @Todo(Jok): need a better error here
            throw error("Invalid assignment target");
        }

        return expr;
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

        return call();
    }

    private Expr call() {
        Expr expr = primary();

        while (true) {
            if (advanceIfAny(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else {
                break;
            }
        }

        return expr;
    }

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();

        if (!peekIs(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 255) {
                    throw error("Cannot have more than 255 arguments");
                }

                arguments.add(expression());
            } while (advanceIfAny(COMMA));
        }

        advanceExpectToken(RIGHT_PAREN, "after function arguments");

        return new Expr.Call(callee, arguments);
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

        if (advanceIfAny(IDENTIFIER)) {
            return new Expr.Variable(peekPrevious());
        }

        if (advanceIfAny(LEFT_PAREN)) {
            Expr expr = expression();
            advanceExpectToken(RIGHT_PAREN, "after expression");
            return new Expr.Grouping(expr);
        }

        throw error("Expected expression, instead got {peek}");
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (VALID_STATEMENT_ENDS.contains(peek().type)) {
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

    private boolean peekIs(TokenType type) {
        if (isAtEnd()) {
            return type == EOF;
        }

        return peek().type == type;
    }

    private boolean errorIfPeekIs(String message, TokenType type) {
        if (peekIs(type)) {
            throw error(message);
        }

        return true;
    }

    private @Nullable Token peekIsAny(TokenType... anyOfTypes) {
        for (TokenType type : anyOfTypes) {
            if (peekIs(type)) {
                return advance();
            }
        }

        return null;
    }

    private boolean peekIsConsecutive(TokenType... types) {
        int advance = 0;
        for (TokenType type : types) {
            Token ahead = peek(advance);
            if (ahead == null || ahead.type != type) {
                return false;
            }

            advance++;
        }

        return true;
    }

    private boolean advanceThroughIfConsecutive(TokenType... types) {
        int advance = 0;
        for (TokenType type : types) {
            Token ahead = peek(advance);
            if (ahead == null || ahead.type != type) {
                return false;
            }

            advance++;
        }

        for (int i = 0; i < advance; i++) {
            advance();
        }

        return true;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }

        return peekPrevious();
    }

    private boolean advanceIfAny(TokenType... anyOfTypes) {
        return peekIsAny(anyOfTypes) != null;
    }

    private boolean eatBlankLines() {
        while (advanceIfAny(NEW_LINE)) { }
        return isAtEnd();
    }

    private Token advanceIfAnyOrError(String message, TokenType... anyOfTypes) {
        Token token = peekIsAny(anyOfTypes);
        if (token != null) {
            return token;
        }

        throw error(message);
    }

    private Token advanceExpectToken(TokenType tokenType) {
        return advanceExpectToken(tokenType, null);
    }

    private Token advanceExpectToken(TokenType tokenType, @Nullable String context) {
        return advanceExpectToken(null, tokenType, context);
    }

    private Token advanceExpectToken(@Nullable String expectedName, TokenType tokenType, @Nullable String context) {
        if (peekIs(tokenType)) {
            return advance();
        }

        throw error("Expected " + (expectedName != null ? expectedName : tokenType) + (context != null ? " " + context : "") + ", instead got {peek}");
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private @Nullable Token peek(int advance) {
        if (current + advance >= tokens.size()) {
            return null;
        }

        return tokens.get(current + advance);
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
