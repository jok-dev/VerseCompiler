package dev.jok.verse.ast.types.decl;

import dev.jok.verse.ast.AstNode;
import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.Expr;
import dev.jok.verse.ast.types.AstStmt;
import dev.jok.verse.ast.types.AstType;
import dev.jok.verse.lexer.Token;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AstVariableDecl extends AstStmt {

    public final Token name;
    public final List<AstType> specifier;
    public final AstType type;
    public final Expr initializer;
    public final boolean mutable;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitVariableDecl(this);
    }
}
