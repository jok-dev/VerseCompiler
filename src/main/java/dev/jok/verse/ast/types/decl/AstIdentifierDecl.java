package dev.jok.verse.ast.types.decl;

import dev.jok.verse.ast.types.AstType;
import dev.jok.verse.lexer.Token;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AstIdentifierDecl {

    public final Token name;
    public final List<AstType> specifiers;

}
