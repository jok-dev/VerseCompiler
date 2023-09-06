package dev.jok.verse.ast.types.expr;

import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.types.AstExpr;
import dev.jok.verse.lexer.Token;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AstGetExpr extends AstExpr {

    public final AstExpr expr;
    public final Token name;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitGetExpr(this);
    }
}
