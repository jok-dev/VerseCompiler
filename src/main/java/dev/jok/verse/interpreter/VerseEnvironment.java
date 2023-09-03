package dev.jok.verse.interpreter;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class VerseEnvironment {

    private final @Nullable VerseEnvironment parent;
    private final Map<String, Object> values = new HashMap<>();

    public VerseEnvironment() {
        this(null);
    }

    public VerseEnvironment(@Nullable VerseEnvironment parent) {
        this.parent = parent;
    }

    public void defineVariable(String name, Object value) {
        if (values.containsKey(name)) {
            throw VerseInterpreter.internalError("Variable '" + name + "' already defined.");
        }

        values.put(name, value);
    }

    public Object getValue(String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        }

        if (parent != null) {
            return parent.getValue(name);
        }

        throw VerseInterpreter.internalError("Undefined variable '" + name + "'.");
    }

    public void setValue(String name, Object value) {
        if (values.containsKey(name)) {
            values.put(name, value);
            return;
        }

        if (parent != null) {
            parent.setValue(name, value);
            return;
        }

        throw VerseInterpreter.internalError("Undefined variable '" + name + "'.");
    }

}
