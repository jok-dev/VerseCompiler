package dev.jok.verse.util;

import dev.jok.verse.ast.Expr;
import dev.jok.verse.ast.Stmt;

import java.util.List;
import java.util.Objects;

public class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {

    public String print(Expr expr) {
        return expr.accept(this);
    }

    public String print(Stmt stmt) {
        return stmt.accept(this);
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression expression) {
        return expression.expression.accept(this);
    }

    @Override
    public String visitBlockStmt(Stmt.Block block) {
        StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        appendStatements(block.statements, builder);
        builder.append("}");

        return builder.toString();
    }

    @Override
    public String visitFunctionStmt(Stmt.Function function) {
        StringBuilder builder = new StringBuilder();

        builder.append(function.name.lexeme).append("(");
        appendCommaSeperatedStatements(function.parameters, builder);

        builder.append(") : ").append(function.returnType.lexeme).append(" {\n");
        appendStatements(function.body, builder);
        builder.append("}");

        return builder.toString();
    }

    @Override
    public String visitParameterStmt(Stmt.Parameter parameter) {
        return parameter.name.lexeme + " : " + parameter.type.lexeme;
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

    @Override
    public String visitCallExpr(Expr.Call call) {
        StringBuilder builder = new StringBuilder();

        builder.append(call.callee.accept(this)).append("(");
        appendCommaSeperatedExpressions(call.arguments, builder);
        builder.append(")");

        return builder.toString();
    }

    private void appendCommaSeperatedStatements(List<? extends Stmt> statements, StringBuilder builder) {
        for (int i = 0; i < statements.size(); i++) {
            Stmt stmt = statements.get(i);
            builder.append(stmt.accept(this));
            if (i != statements.size() - 1) {
                builder.append(", ");
            }
        }
    }

    private void appendCommaSeperatedExpressions(List<? extends Expr> expressions, StringBuilder builder) {
        for (int i = 0; i < expressions.size(); i++) {
            Expr expr = expressions.get(i);
            builder.append(expr.accept(this));
            if (i != expressions.size() - 1) {
                builder.append(", ");
            }
        }
    }

    private void appendStatements(List<Stmt> statements, StringBuilder builder) {
        for (Stmt stmt : statements) {
            builder.append(stmt.accept(this)).append("\n");
        }
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
