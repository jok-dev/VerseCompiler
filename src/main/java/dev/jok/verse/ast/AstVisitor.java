package dev.jok.verse.ast;

import dev.jok.verse.ast.types.*;
import dev.jok.verse.ast.types.decl.AstFunctionDecl;
import dev.jok.verse.ast.types.decl.AstVariableDecl;
import dev.jok.verse.ast.types.expr.*;
import dev.jok.verse.ast.types.stmt.AstBlock;
import dev.jok.verse.ast.types.stmt.AstExpressionStmt;

public interface AstVisitor<R> {

    R visitTypeExpr(AstType type);
    R visitFunctionDecl(AstFunctionDecl astFunctionDecl);
    R visitVariableDecl(AstVariableDecl astVariableDecl);
    R visitExpressionStmt(AstExpressionStmt astExpressionStmt);
    R visitBlock(AstBlock astBlock);
    R visitParameter(AstParameter astParameter);
    R visitIf(AstIfExpr astIf);
    R visitAssignExpr(AstAssignExpr astAssign);

    R visitBinaryExpr(AstBinaryExpr astBinary);

    R visitGroupingExpr(AstGroupingExpr astGrouping);

    R visitLiteralExpr(AstLiteralExpr astLiteral);

    R visitUnaryExpr(AstUnaryExpr astUnary);

    R visitVariableExpr(AstVariableExpr astVariable);

    R visitCallExpr(AstCallExpr astCall);
    R visitGetExpr(AstGetExpr astGet);

}
