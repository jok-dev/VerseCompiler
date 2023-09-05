package dev.jok.verse.ast.types;

import dev.jok.verse.ast.AstVisitor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AstBlock extends AstStmt {

    public final List<AstStmt> statements;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitBlock(this);
    }

}
