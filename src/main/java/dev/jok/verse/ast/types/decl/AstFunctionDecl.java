package dev.jok.verse.ast.types.decl;

import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.types.AstParameter;
import dev.jok.verse.ast.types.AstStmt;
import dev.jok.verse.ast.types.AstType;
import dev.jok.verse.lexer.Token;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RequiredArgsConstructor
public class AstFunctionDecl extends AstStmt {

    public final Token name;
    public final List<AstType> specifiers;
    public final List<AstType> effects;
    public final List<AstParameter> parameters;
    public final AstType type;
    public final @Nullable List<AstStmt> body;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitFunctionDecl(this);
    }

    public boolean hasSpecifier(String specifierName) {
        for (AstType specifier : specifiers) {
            if (specifier.name.lexeme.equals(specifierName)) {
                return true;
            }
        }

        return false;
    }

}
