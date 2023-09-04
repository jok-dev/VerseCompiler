package dev.jok.verse.ast;

import dev.jok.verse.VerseLang;
import dev.jok.verse.lexer.Token;
import dev.jok.verse.util.AstPrinter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

public abstract class Expr {

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {

        R visitAssignExpr(Assign assign);
        R visitBinaryExpr(Binary binary);
        R visitGroupingExpr(Grouping grouping);
        R visitLiteralExpr(Literal literal);
        R visitUnaryExpr(Unary unary);
        R visitVariableExpr(Variable variable);
        R visitCallExpr(Call call);

    }

    @RequiredArgsConstructor
    public static class Assign extends Expr {

        public final Token name;
        public final Expr value;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

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

        @Override
        public String toString() {
            if (value instanceof String str) {
                return "\"" + str + "\"";
            }

            return Objects.toString(value);
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

    @RequiredArgsConstructor
    public static class Variable extends Expr {

        public final Token name;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

    }

    @RequiredArgsConstructor
    public static class Call extends Expr {

        public final Expr callee;
        public final List<Expr> arguments;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }

    }

    @Override
    public String toString() {
        return VerseLang.PRINTER.print(this);
    }

}
