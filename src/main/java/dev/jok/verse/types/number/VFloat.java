package dev.jok.verse.types.number;

import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public class VFloat extends VNumber<VFloat> {

    private final float value;

    public static VFloat parseFloat(String text) {
        return new VFloat(Float.parseFloat(text));
    }

    @Override
    public VFloat negate() {
        ensureSameType(this);
        return new VFloat(-value);
    }

    @Override
    public VFloat add(VNumber<?> rightVal) {
        ensureSameType(rightVal);
        return new VFloat(value + ((VFloat) rightVal).value);
    }

    @Override
    public VFloat subtract(VNumber<?> rightVal) {
        ensureSameType(rightVal);
        return new VFloat(value - ((VFloat) rightVal).value);
    }

    @Override
    public VFloat multiply(VNumber<?> rightVal) {
        ensureSameType(rightVal);
        return new VFloat(value * ((VFloat) rightVal).value);
    }

    @Override
    public VFloat divide(VNumber<?> rightVal) {
        ensureSameType(rightVal);
        return new VFloat(value / ((VFloat) rightVal).value);
    }

    @Override
    protected Number getRawValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof VNumber<?> otherVal)) {
            return false;
        }
        return Objects.equals(value, otherVal.getRawValue());
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
