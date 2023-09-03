package dev.jok.verse.ast;

import dev.jok.verse.lexer.Token;
import lombok.RequiredArgsConstructor;

public abstract class Expr {

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {

        R visitBinaryExpr(Binary binary);
        R visitGroupingExpr(Grouping grouping);
        R visitLiteralExpr(Literal literal);
        R visitUnaryExpr(Unary unary);

    }

    @RequiredArgsConstructor
    public static class Binary extends Expr {

        public final Expr left;
        public final Token operator;
        public final Expr right;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

    }

    @RequiredArgsConstructor
    public static class Grouping extends Expr {

        public final Expr expression;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

    }

    @RequiredArgsConstructor
    public static class Literal extends Expr {

        public final Object value;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

    }

    @RequiredArgsConstructor
    public static class Unary extends Expr {

        public final Token operator;
        public final Expr right;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

    }

}
