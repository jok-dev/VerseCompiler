package dev.jok.verse.ast.types.expr;

import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.types.AstExpr;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AstGroupingExpr extends AstExpr {

    public final AstExpr expression;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitGroupingExpr(this);
    }
}
