package dev.jok.verse.ast.types;

import dev.jok.verse.ast.AstNode;
import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.lexer.Token;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class AstParameter extends AstNode {

    public final Token name;
    public final @Nullable AstType type;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitParameter(this);
    }

}