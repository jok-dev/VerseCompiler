package dev.jok.verse.types.number;

import dev.jok.verse.interpreter.VerseInterpreter;

public abstract class VNumber<T extends VNumber<T>> {

    public abstract T negate();

    public abstract T add(VNumber<?> rightVal);
    public abstract T subtract(VNumber<?> rightVal);
    public abstract T multiply(VNumber<?> rightVal);
    public abstract T divide(VNumber<?> rightVal);

    protected abstract Number getRawValue();

    protected void ensureSameType(VNumber<?> rightVal) {
        if (!isSameType(rightVal)) {
            throw VerseInterpreter.runtimeError(null, "Cannot operate on different types");
        }
    }

    private boolean isSameType(VNumber<?> other) {
        return this.getClass().equals(other.getClass());
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof VNumber<?> otherVal)) {
            return false;
        }
        return getRawValue().equals(otherVal.getRawValue());
    }

    @Override
    public abstract String toString();
}
