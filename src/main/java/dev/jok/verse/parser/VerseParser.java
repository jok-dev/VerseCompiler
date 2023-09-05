package dev.jok.verse.parser;

import dev.jok.verse.VerseLang;
import dev.jok.verse.ast.types.*;
import dev.jok.verse.ast.types.decl.AstFunctionDecl;
import dev.jok.verse.ast.types.decl.AstVariableDecl;
import dev.jok.verse.ast.types.expr.*;
import dev.jok.verse.ast.types.stmt.AstBlock;
import dev.jok.verse.ast.types.stmt.AstExpressionStmt;
import dev.jok.verse.lexer.Token;
import dev.jok.verse.lexer.TokenType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static dev.jok.verse.lexer.TokenType.*;

@RequiredArgsConstructor
public class VerseParser {

    private static final TokenType[] ALLOWED_AFTER_DECL_IDENTIFIER = { COLON, LESS, LEFT_PAREN, INFERRED_DECL };

    private static final Set<TokenType> VALID_STATEMENT_ENDS = Set.of(RIGHT_BRACE, SEMICOLON, NEW_LINE, EOF);

    private final boolean debug;
    private final List<Token> tokens;
    private int current = 0;

    public @NotNull List<AstStmt> parse() {
        List<AstStmt> statements = new ArrayList<>();

        while (!eatBlankLines()) {
            statements.add(declaration());
            advanceExpressionEnd();
        }

        return statements;
    }

    private List<AstStmt> anonymousBlock() {
        List<AstStmt> statements = new ArrayList<>();

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

    private AstStmt declaration() {
        try {
            boolean mutableVar = advanceIfAny(VAR);
            if (peekExpectConditional(mutableVar, IDENTIFIER) && peekNextIsAny(ALLOWED_AFTER_DECL_IDENTIFIER)) {
                Token name = advanceExpectToken(IDENTIFIER, "in declaration");
                List<AstType> specifiers = maybeSpecifiers("in declaration");

                if (mutableVar || peekIsAny(COLON, INFERRED_DECL)) {
                    return variableDecl(mutableVar, name, specifiers);
                }

                if (!peekIs(LEFT_PAREN)) {
                    throw error("Expected definition but found {peek}");
                }

                // must be a function
                return functionDecl(name, specifiers);
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

    private AstFunctionDecl functionDecl(Token name, List<AstType> specifiers) {
        advanceExpectToken(LEFT_PAREN, "in function declaration");

        // parse parameters
        List<AstParameter> parameters = new ArrayList<>();
        if (!peekIs(RIGHT_PAREN)) {
            do {
                Token parameterName = advanceExpectToken(IDENTIFIER, "in parameter declaration");
                advanceExpectToken(COLON, "in parameter declaration");
                AstType parameterType = type("in parameter declaration");

                parameters.add(new AstParameter(parameterName, parameterType));
            } while (advanceIfAny(COMMA));
        }

        advanceExpectToken(RIGHT_PAREN, "in function declaration");

        List<AstType> effects = maybeSpecifiers("in function declaration");

        advanceExpectToken(COLON, "in function declaration");
        AstType type = type("in function declaration");
        advanceExpectToken(EQUALS, "in function declaration");

        // @Todo(Jok): don't require braces {  }
        advanceExpectToken(LEFT_BRACE);
        List<AstStmt> body = anonymousBlock();

        return new AstFunctionDecl(name, specifiers, effects, parameters, type, body);
    }

    private AstVariableDecl variableDecl(boolean mutableVar, Token identifier, List<AstType> specifiers) {
        // mutables don't support inferred types currently
        if (mutableVar && peekIs(INFERRED_DECL)) {
            throw error("Missing type for `^` or `var` definition");
        }

        AstType type = null;

        // parse type if not inferred
        if (!advanceIfAny(INFERRED_DECL)) {
            advanceExpectToken(COLON, "in variable definition");
            type = type("in variable definition");
            advanceExpectToken(EQUALS, "in variable definition");
        }

        AstExpr initializer = expression();
        return new AstVariableDecl(identifier, specifiers, type, initializer, mutableVar);
    }

    private List<AstType> maybeSpecifiers(String context) {
        List<AstType> specifiers = new ArrayList<>();
        if (advanceIfAny(LESS)) {
            do {
                specifiers.add(type("specifier type", ""));
                advanceExpectToken(GREATER, "after specifier type");
            } while (advanceIfAny(LESS));
        }

        return specifiers;
    }

    private AstType type(String context) {
        return type(null, context);
    }

    private AstType type(@Nullable String tokenName, String context) {
        if (tokenName == null) {
            tokenName = "type";
        }

        boolean array = false;
        boolean map = false;
        AstType keyType = null;

        if (advanceIfAny(LEFT_BRACKET)) {
            if (!peekIs(RIGHT_BRACKET)) {
                map = true;
                keyType = type("in map declaration");
            } else {
                array = true;
            }

            advanceExpectToken(RIGHT_BRACKET, "in type declaration");
        }

        boolean optional = advanceIfAny(QUESTION_MARK);
        Token name = advanceExpectToken(tokenName, IDENTIFIER, context);
        return new AstType(name, array, map, keyType, optional);
    }

    private AstStmt statement() {
        // block statements
        if (advanceThroughIfConsecutive(BLOCK, LEFT_BRACE)) return new AstBlock(anonymousBlock());

        return expressionStmt();
    }

    private AstStmt expressionStmt() {
        AstExpr value = expression();
        return new AstExpressionStmt(value);
    }

    private void advanceExpressionEnd() {
        // } ends the expression
        if (peekIs(RIGHT_BRACE)) {
            return;
        }

        advanceExpectAny("Unexpected {peek} following expression", SEMICOLON, NEW_LINE, EOF);
    }

    private AstExpr expression() {
        return assignment();
    }

    private AstExpr assignment() {
        AstExpr expr = ifExpr();

        if (advanceIfAny(EQUALS)) {
            AstExpr value = assignment();

            // @Todo(Jok) @Feat: add support for other assignment targets
            if (expr instanceof AstVariableExpr variable) {
                Token name = variable.name;
                return new AstAssignExpr(name, value);
            }

            // @Todo(Jok): need a better error here
            throw error("Invalid assignment target");
        }

        return expr;
    }

    private AstExpr ifExpr() {
        AstExpr expr = equality();

        if (advanceIfAny(IF)) {
            // @Todo(Jok) @Feat: in verse this weird syntax needs to be supported "if { maybe }"
            // @Todo(Jok) @Feat: ifs can also be in expressions, not just statements!

            advanceExpectToken(LEFT_PAREN, "in if statement");
            AstExpr condition = expression();
            advanceExpectToken(RIGHT_PAREN, "in if statement");

            // @Todo(Jok) @Feat: don't require braces {  }
            advanceExpectToken(LEFT_BRACE, "in if statement");

            // @Todo(Jok) @Feat: support then: block
            List<AstStmt> thenBranch = anonymousBlock();

            List<AstStmt> elseBranch;
            if (advanceIfAny(ELSE)) {
                // @Todo(Jok) @Feat: don't require braces {  }
                advanceExpectToken(LEFT_BRACE, "in else statement");
                elseBranch = anonymousBlock();
            } else {
                elseBranch = Collections.emptyList();
            }

            return new AstIfExpr(condition, thenBranch, elseBranch);
        }

        return expr;
    }

    private AstExpr equality() {
        AstExpr expr = comparison();

        while (advanceIfAny(EQUALS)) {
            Token operator = peekPrevious();
            AstExpr right = comparison();
            expr = new AstBinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private AstExpr comparison() {
        AstExpr expr = addition();

        while (advanceIfAny(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = peekPrevious();
            AstExpr right = addition();
            expr = new AstBinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private AstExpr addition() {
        AstExpr expr = multiplication();

        while (advanceIfAny(PLUS, MINUS)) {
            Token operator = peekPrevious();
            AstExpr right = multiplication();
            expr = new AstBinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private AstExpr multiplication() {
        AstExpr expr = unary();

        while (advanceIfAny(STAR, SLASH)) {
            Token operator = peekPrevious();
            AstExpr right = unary();
            expr = new AstBinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private AstExpr unary() {
        if (advanceIfAny(MINUS, NOT)) {
            Token operator = peekPrevious();
            AstExpr right = unary();
            return new AstUnaryExpr(operator, right);
        }

        return call();
    }

    private AstExpr call() {
        AstExpr expr = primary();

        while (true) {
            if (advanceIfAny(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else {
                break;
            }
        }

        return expr;
    }

    private AstExpr finishCall(AstExpr callee) {
        List<AstExpr> arguments = new ArrayList<>();

        if (!peekIs(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 255) {
                    throw error("Cannot have more than 255 arguments");
                }

                arguments.add(expression());
            } while (advanceIfAny(COMMA));
        }

        advanceExpectToken(RIGHT_PAREN, "after function arguments");

        return new AstCallExpr(callee, arguments);
    }

    private AstExpr primary() {
        if (advanceIfAny(FALSE)) {
            return new AstLiteralExpr(false);
        }

        if (advanceIfAny(TRUE)) {
            return new AstLiteralExpr(true);
        }

        if (advanceIfAny(NUMBER_INT, NUMBER_FLOAT, STRING)) {
            return new AstLiteralExpr(peekPrevious().literal);
        }

        if (advanceIfAny(IDENTIFIER)) {
            return new AstVariableExpr(peekPrevious());
        }

        if (advanceIfAny(LEFT_PAREN)) {
            AstExpr expr = expression();
            advanceExpectToken(RIGHT_PAREN, "after expression");
            return new AstGroupingExpr(expr);
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

    private boolean peekIs(int offset, TokenType type) {
        if (isAtEnd()) {
            return type == EOF;
        }

        return peek().type == type;
    }

    private boolean peekIs(TokenType type) {
        if (isAtEnd()) {
            return type == EOF;
        }

        return peek().type == type;
    }

    private boolean peekNextIs(TokenType tokenType) {
        if (isAtEnd()) {
            return tokenType == EOF;
        }

        return peekNext().type == tokenType;
    }

    private void errorIfPeekIs(String message, TokenType type) {
        if (peekIs(type)) {
            throw error(message);
        }
    }

    private boolean peekExpectConditional(boolean shouldError, TokenType type) {
        return peekExpectConditional(shouldError, null, type);
    }

    private boolean peekExpectConditional(boolean shouldError, @Nullable String context, TokenType type) {
        if (shouldError) {
            peekExpect(context, type);
        }

        return peekIs(type);
    }

    private void peekExpect(@Nullable String context, TokenType type) {
        if (!peekIs(type)) {
            throw expectError(null, type, context);
        }
    }

    private boolean peekIsAny(TokenType... anyOfTypes) {
        for (TokenType type : anyOfTypes) {
            if (peekIs(type)) {
                return true;
            }
        }

        return false;
    }

    private boolean peekNextIsAny(TokenType... anyOfTypes) {
        for (TokenType type : anyOfTypes) {
            if (peekNextIs(type)) {
                return true;
            }
        }

        return false;
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
        if (peekIsAny(anyOfTypes)) {
            advance();
            return true;
        }

        return false;
    }

    private boolean eatBlankLines() {
        while (advanceIfAny(NEW_LINE)) { }
        return isAtEnd();
    }

    private void advanceExpectAny(String message, TokenType... anyOfTypes) {
        if (!advanceIfAny(anyOfTypes)) {
            throw error(message);
        }
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

        throw expectError(expectedName, tokenType, context);
    }

    private SyntaxError expectError(@Nullable String expectedName, TokenType tokenType, @Nullable String context) {
        throw error("Expected " + (expectedName != null ? expectedName : tokenType) + (context != null ? " " + context : "") + " after {peekPrev}, instead got {peek}");
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private @NotNull Token peek() {
        Token peek = peek(0);
        if (peek == null) {
            throw new IllegalStateException("Advanced over EOF");
        }

        return peek;
    }

    private @Nullable Token peekNext() {
        return peek(1);
    }

    private @Nullable Token peekPrevious() {
        return peek(-1);
    }

    private @Nullable Token peek(int advance) {
        int pos = current + advance;
        if (pos >= tokens.size() || pos < 0) {
            return null;
        }

        return tokens.get(pos);
    }

    private SyntaxError error(String message) {
        VerseLang.syntaxError(peekPrevious(), peek(), peekNext(), message);
        return new SyntaxError();
    }

    private static class SyntaxError extends RuntimeException { }

}
