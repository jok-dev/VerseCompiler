package dev.jok.verse.ast.types.decl;

import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.Expr;
import dev.jok.verse.ast.types.AstStmt;
import dev.jok.verse.ast.types.AstType;
import dev.jok.verse.lexer.Token;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RequiredArgsConstructor
public class AstVariableDecl extends AstStmt {

    public final @NotNull Token name;
    public final @NotNull List<AstType> specifier;
    public final @Nullable AstType type;
    public final @NotNull Expr initializer;
    public final boolean mutable;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitVariableDecl(this);
    }
}
