package dev.jok.verse.ast;

import dev.jok.verse.ast.types.AstBlock;
import dev.jok.verse.ast.types.AstExpressionStmt;
import dev.jok.verse.ast.types.AstParameter;
import dev.jok.verse.ast.types.AstType;
import dev.jok.verse.ast.types.decl.AstFunctionDecl;
import dev.jok.verse.ast.types.decl.AstVariableDecl;

public interface AstVisitor<R> {

    R visitTypeExpr(AstType type);
    R visitFunctionDecl(AstFunctionDecl astFunctionDecl);
    R visitVariableDecl(AstVariableDecl astVariableDecl);
    R visitExpressionStmt(AstExpressionStmt astExpressionStmt);
    R visitBlock(AstBlock astBlock);
    R visitParameter(AstParameter astParameter);

}
