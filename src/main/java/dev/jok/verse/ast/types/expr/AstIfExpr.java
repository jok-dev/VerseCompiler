package dev.jok.verse.ast.types.expr;

import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.types.AstExpr;
import dev.jok.verse.ast.types.AstStmt;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AstIfExpr extends AstExpr {

    public final AstExpr condition;
    public final List<AstStmt> thenBranch;
    public final List<AstStmt> elseBranch;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitIf(this);
    }
}
