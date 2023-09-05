package dev.jok.verse.ast.types;

import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.Expr;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AstExpressionStmt extends AstStmt {

    public final Expr expression;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitExpressionStmt(this);
    }

}
