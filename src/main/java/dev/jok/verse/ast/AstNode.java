package dev.jok.verse.ast;

public abstract class AstNode {

    public abstract <R> R accept(AstVisitor<R> visitor);

}
