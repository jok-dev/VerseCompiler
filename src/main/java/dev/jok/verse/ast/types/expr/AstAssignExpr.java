package dev.jok.verse.ast.types.expr;

import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.types.AstExpr;
import dev.jok.verse.lexer.Token;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AstAssignExpr extends AstExpr {

    public final Token name;
    public final AstExpr value;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitAssignExpr(this);
    }
}
