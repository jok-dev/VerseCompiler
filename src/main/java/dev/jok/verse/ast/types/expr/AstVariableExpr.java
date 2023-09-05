package dev.jok.verse.ast.types.expr;

import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.types.AstExpr;
import dev.jok.verse.lexer.Token;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AstVariableExpr extends AstExpr {

    public final Token name;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitVariableExpr(this);
    }

}
