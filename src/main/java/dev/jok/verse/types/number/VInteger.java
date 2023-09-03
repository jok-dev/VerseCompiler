package dev.jok.verse.types.number;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VInteger extends VNumber<VInteger> {

    private final int value;

    public static Object parseInt(String text) {
        return new VInteger(Integer.parseInt(text));
    }

    @Override
    public VInteger negate() {
        return new VInteger(-value);
    }

    @Override
    public VInteger add(VNumber<?> rightVal) {
        ensureSameType(rightVal);
        return new VInteger(value + ((VInteger) rightVal).value);
    }

    @Override
    public VInteger subtract(VNumber<?> rightVal) {
        ensureSameType(rightVal);
        return new VInteger(value - ((VInteger) rightVal).value);
    }

    @Override
    public VInteger multiply(VNumber<?> rightVal) {
        ensureSameType(rightVal);
        return new VInteger(value * ((VInteger) rightVal).value);
    }

    @Override
    public VInteger divide(VNumber<?> rightVal) {
        ensureSameType(rightVal);
        return new VInteger(value / ((VInteger) rightVal).value);
    }

    @Override
    protected Number getRawValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
