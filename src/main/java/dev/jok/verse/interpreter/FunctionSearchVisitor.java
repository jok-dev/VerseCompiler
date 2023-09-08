package dev.jok.verse.interpreter;

import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.types.AstParameter;
import dev.jok.verse.ast.types.AstType;
import dev.jok.verse.ast.types.decl.AstFunctionDecl;
import dev.jok.verse.ast.types.decl.AstVariableDecl;
import dev.jok.verse.ast.types.expr.*;
import dev.jok.verse.ast.types.stmt.AstBlock;
import dev.jok.verse.ast.types.stmt.AstExpressionStmt;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FunctionSearchVisitor implements AstVisitor<AstFunctionDecl> {

    private final String name;

    @Override
    public AstFunctionDecl visitTypeExpr(AstType type) {
        return null;
    }

    @Override
    public AstFunctionDecl visitFunctionDecl(AstFunctionDecl astFunctionDecl) {
        if (astFunctionDecl.name.lexeme.equals(name)) {
            return astFunctionDecl;
        }

        return null;
    }

    @Override
    public AstFunctionDecl visitVariableDecl(AstVariableDecl astVariableDecl) {
        return null;
    }

    @Override
    public AstFunctionDecl visitExpressionStmt(AstExpressionStmt astExpressionStmt) {
        return null;
    }

    @Override
    public AstFunctionDecl visitBlock(AstBlock astBlock) {
        return null;
    }

    @Override
    public AstFunctionDecl visitParameter(AstParameter astParameter) {
        return null;
    }

    @Override
    public AstFunctionDecl visitIf(AstIfExpr astIf) {
        return null;
    }

    @Override
    public AstFunctionDecl visitAssignExpr(AstAssignExpr astAssign) {
        return null;
    }

    @Override
    public AstFunctionDecl visitBinaryExpr(AstBinaryExpr astBinary) {
        return null;
    }

    @Override
    public AstFunctionDecl visitGroupingExpr(AstGroupingExpr astGrouping) {
        return null;
    }

    @Override
    public AstFunctionDecl visitLiteralExpr(AstLiteralExpr astLiteral) {
        return null;
    }

    @Override
    public AstFunctionDecl visitUnaryExpr(AstUnaryExpr astUnary) {
        return null;
    }

    @Override
    public AstFunctionDecl visitVariableExpr(AstVariableExpr astVariable) {
        return null;
    }

    @Override
    public AstFunctionDecl visitCallExpr(AstCallExpr astCall) {
        return null;
    }

    @Override
    public AstFunctionDecl visitGetExpr(AstGetExpr astGet) {
        return null;
    }
}
