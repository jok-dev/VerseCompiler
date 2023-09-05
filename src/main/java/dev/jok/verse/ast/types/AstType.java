package dev.jok.verse.ast.types;

import dev.jok.verse.ast.AstNode;
import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.lexer.Token;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AstType extends AstNode {

    public final Token name;
    public final boolean array;
    public final boolean map;
    public final AstType keyType;
    public final boolean optional;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitTypeExpr(this);
    }

    @Override
    public String toString() {
        return (array ? "[]" : "") + (map ? "[" + keyType + "]" : "") + (optional ? "?" : "") + name.lexeme;
    }

}