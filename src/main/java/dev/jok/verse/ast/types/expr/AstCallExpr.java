package dev.jok.verse.ast.types.expr;

import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.types.AstExpr;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AstCallExpr extends AstExpr {

    public final AstExpr callee;
    public final List<AstExpr> arguments;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitCallExpr(this);
    }

}
