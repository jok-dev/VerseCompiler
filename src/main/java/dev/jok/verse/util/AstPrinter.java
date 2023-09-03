package dev.jok.verse.util;

import dev.jok.verse.ast.Expr;
import dev.jok.verse.ast.Stmt;

import java.util.Objects;

public class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {

    public String print(Expr expr) {
        return expr.accept(this);
    }

    public String print(Stmt stmt) {
        return stmt.accept(this);
    }

    @Override
    public String visitBlockStmt(Stmt.Block block) {
        StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        for (Stmt stmt : block.statements) {
            builder.append(stmt.accept(this)).append("\n");
        }
        builder.append("}");

        return builder.toString();
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression expression) {
        return expression.expression.accept(this);
    }

    @Override
    public String visitPrintStmt(Stmt.Print print) {
        return "print " + print.expression.accept(this);
    }

    @Override
    public String visitVariableDeclarationStmt(Stmt.VariableDeclaration variableDeclaration) {
        return (variableDeclaration.mutable ? "var " : "") + variableDeclaration.name.lexeme + " : "
                + variableDeclaration.type.lexeme + " = " + variableDeclaration.initializer.accept(this);
    }

    @Override
    public String visitAssignExpr(Expr.Assign assign) {
        return assign.name.lexeme + " = " + assign.value.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary binary) {
        return "(" +
                binary.left.accept(this) +
                " " + binary.operator.lexeme + " " +
                binary.right.accept(this) +
                ")";
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping grouping) {
        return parenthesize("group", grouping.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal literal) {
        return Objects.toString(literal);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary unary) {
        return unary.operator.lexeme + unary.right.accept(this);
    }

    @Override
    public String visitVariableExpr(Expr.Variable variable) {
        return variable.name.lexeme;
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

}
