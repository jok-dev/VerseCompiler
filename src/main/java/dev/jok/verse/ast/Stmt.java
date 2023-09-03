package dev.jok.verse.ast;

import lombok.RequiredArgsConstructor;

public abstract class Stmt {

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {

        R visitExpressionStmt(Expression expression);
        R visitPrintStmt(Print print);

    }

    @RequiredArgsConstructor
    public static class Expression extends Stmt {

        public final Expr expression;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

    }

    @RequiredArgsConstructor
    public static class Print extends Stmt {

        public final Expr expression;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

    }

}
