package dev.jok.verse.ast;

import dev.jok.verse.VerseLang;
import dev.jok.verse.lexer.Token;
import lombok.RequiredArgsConstructor;

import java.util.List;

public abstract class Stmt {

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {

        R visitExpressionStmt(Expression expression);
        R visitBlockStmt(Block block);
        R visitFunctionStmt(Function function);
        R visitParameterStmt(Parameter parameter);
        R visitVariableDeclarationStmt(VariableDeclaration variableDeclaration);
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
    public static class Block extends Stmt {

        public final List<Stmt> statements;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

    }

    @RequiredArgsConstructor
    public static class Function extends Stmt {

        public final Token name;
        public final Token returnType;
        public final List<Parameter> parameters;
        public final List<Stmt> body;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }

    }

    @RequiredArgsConstructor
    public static class Parameter extends Stmt {

        public final Token name;
        public final Token type;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitParameterStmt(this);
        }

    }

    @RequiredArgsConstructor
    public static class VariableDeclaration extends Stmt {

        public final Token name;
        public final Token type;
        public final Expr initializer;
        public final boolean mutable;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableDeclarationStmt(this);
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

    @Override
    public String toString() {
        return VerseLang.PRINTER.print(this);
    }

}
