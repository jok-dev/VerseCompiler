package dev.jok.verse.util;

import dev.jok.verse.ast.AstNode;
import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.types.*;
import dev.jok.verse.ast.types.decl.AstFunctionDecl;
import dev.jok.verse.ast.types.decl.AstVariableDecl;
import dev.jok.verse.ast.types.expr.*;
import dev.jok.verse.ast.types.stmt.AstBlock;
import dev.jok.verse.ast.types.stmt.AstExpressionStmt;

import java.util.List;
import java.util.Objects;

public class AstPrinter implements AstVisitor<String> {

    public String print(AstExpr expr) {
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
    public String visitIf(AstIfExpr astIf) {
        StringBuilder builder = new StringBuilder();

        builder.append("if (").append(astIf.condition.accept(this)).append(") {\n");
        appendStatements(astIf.thenBranch, builder);
        builder.append("}");

        if (!astIf.elseBranch.isEmpty()) {
            builder.append(" else {\n");
            appendStatements(astIf.elseBranch, builder);
            builder.append("}");
        }

        return builder.toString();
    }

    @Override
    public String visitVariableDecl(AstVariableDecl variableDeclaration) {
        String type;
        if (variableDeclaration.type != null) {
            type = " : " + variableDeclaration.type.accept(this) + " = ";
        } else {
            type = " := ";
        }

        return (variableDeclaration.mutable ? "var " : "") + variableDeclaration.name.lexeme + type + variableDeclaration.initializer.accept(this);
    }

    @Override
    public String visitAssignExpr(AstAssignExpr assign) {
        return assign.name.lexeme + " = " + assign.value.accept(this);
    }

    @Override
    public String visitBinaryExpr(AstBinaryExpr binary) {
        return "(" +
                binary.left.accept(this) +
                " " + binary.operator.lexeme + " " +
                binary.right.accept(this) +
                ")";
    }

    @Override
    public String visitGroupingExpr(AstGroupingExpr grouping) {
        return parenthesize("group", grouping.expression);
    }

    @Override
    public String visitLiteralExpr(AstLiteralExpr literal) {
        return Objects.toString(literal);
    }

    @Override
    public String visitUnaryExpr(AstUnaryExpr unary) {
        return unary.operator.lexeme + unary.right.accept(this);
    }

    @Override
    public String visitVariableExpr(AstVariableExpr variable) {
        return variable.name.lexeme;
    }

    @Override
    public String visitCallExpr(AstCallExpr call) {
        StringBuilder builder = new StringBuilder();

        builder.append(call.callee.accept(this)).append("(");
        appendCommaSeperatedExpressions(call.arguments, builder);
        builder.append(")");

        return builder.toString();
    }

    @Override
    public String visitGetExpr(AstGetExpr astGet) {
        return astGet.toString();
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

    private void appendCommaSeperatedExpressions(List<? extends AstExpr> expressions, StringBuilder builder) {
        for (int i = 0; i < expressions.size(); i++) {
            AstExpr expr = expressions.get(i);
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

    private String parenthesize(String name, AstExpr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (AstExpr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

}
