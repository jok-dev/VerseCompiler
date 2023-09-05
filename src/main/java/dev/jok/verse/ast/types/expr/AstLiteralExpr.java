package dev.jok.verse.ast.types.expr;

import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.types.AstExpr;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public class AstLiteralExpr extends AstExpr {

    public final Object value;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitLiteralExpr(this);
    }

    @Override
    public String toString() {
        if (value instanceof String str) {
            return "\"" + str + "\"";
        }

        return Objects.toString(value);
    }

}
