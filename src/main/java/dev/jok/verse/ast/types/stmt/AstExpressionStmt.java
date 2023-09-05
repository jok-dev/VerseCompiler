package dev.jok.verse.ast.types.stmt;

import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.types.AstExpr;
import dev.jok.verse.ast.types.AstStmt;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AstExpressionStmt extends AstStmt {

    public final AstExpr expression;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitExpressionStmt(this);
    }

}
