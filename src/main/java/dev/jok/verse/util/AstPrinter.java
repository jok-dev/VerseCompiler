package dev.jok.verse.util;

import dev.jok.verse.ast.AstNode;
import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.Expr;
import dev.jok.verse.ast.types.*;
import dev.jok.verse.ast.types.decl.AstFunctionDecl;
import dev.jok.verse.ast.types.decl.AstVariableDecl;

import java.util.List;
import java.util.Objects;

public class AstPrinter implements Expr.Visitor<String>, AstVisitor<String> {

    public String print(Expr expr) {
        return expr.accept(this);
    }

    public String print(AstStmt stmt) {
        return stmt.accept(this);
    }

    @Override
    public String visitExpressionStmt(AstExpressionStmt expression) {
        return expression.expression.accept(this);
    }

    @Override
    public String visitBlock(AstBlock block) {
        StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        appendStatements(block.statements, builder);
        builder.append("}");

        return builder.toString();
    }

    @Override
    public String visitTypeExpr(AstType type) {
        return type.toString();
    }

    @Override
    public String visitFunctionDecl(AstFunctionDecl function) {
        StringBuilder builder = new StringBuilder();

        builder.append(function.name.lexeme).append("(");
        appendCommaSeperatedStatements(function.parameters, builder);

        builder.append(") : ").append(function.type).append(" {\n");
        appendStatements(function.body, builder);
        builder.append("}");

        return builder.toString();
    }

    @Override
    public String visitParameter(AstParameter parameter) {
        return parameter.name.lexeme + " : " + parameter.type;
    }

    @Override
    public String visitVariableDecl(AstVariableDecl variableDeclaration) {
        return (variableDeclaration.mutable ? "var " : "") + variableDeclaration.name.lexeme + " : "
                + variableDeclaration.type + " = " + variableDeclaration.initializer.accept(this);
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

    @Override
    public String visitTypeExpr(Expr.Type type) {
        return type.toString();
    }

    private void appendCommaSeperatedStatements(List<? extends AstNode> statements, StringBuilder builder) {
        for (int i = 0; i < statements.size(); i++) {
            AstNode node = statements.get(i);
            builder.append(node.accept(this));
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

    private void appendStatements(List<? extends AstStmt> statements, StringBuilder builder) {
        for (AstStmt stmt : statements) {
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
