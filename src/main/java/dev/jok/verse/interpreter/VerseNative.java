package dev.jok.verse.interpreter;

import dev.jok.verse.interpreter.modules.VerseNativeModule;
import dev.jok.verse.types.number.VFloat;
import dev.jok.verse.types.number.VNumber;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class VerseNative {

    private final Map<String, Method> nativeMethods = new HashMap<>();

    public VerseNative() {
        registerModule(new VerseNativeModule());
    }

    public Object callNative(String name, Object... args) {
        Method method = nativeMethods.get(name);
        if (method == null) {
            throw new RuntimeException("Native method " + name + " not found");
        }

        if (method.getParameterCount() != args.length) {
            throw new RuntimeException("Native method " + name + " requires " + method.getParameterCount() + " arguments");
        }

        // convert args to correct type
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof VNumber<?> num) {
                args[i] = num.getRawValue();
            }
        }

        // check each type
        for (int i = 0; i < args.length; i++) {
            Class<?> paramType = method.getParameterTypes()[i];

            // get primitive version of type
            if (paramType == int.class) {
                paramType = Integer.class;
            } else if (paramType == float.class) {
                paramType = Float.class;
            }

            if (!paramType.isInstance(args[i])) {
                throw new RuntimeException("Native method " + name + " requires argument " + i + " to be of type " + paramType.getSimpleName() + ", but got " + args[i].getClass().getSimpleName());
            }
        }

        try {
            return method.invoke(null, args);
        } catch (Exception e) {
            throw new RuntimeException("Calling native method " + name + " failed", e);
        }
    }

    private void registerModule(VerseNativeModule verseNativeModule) {
        for (Method method : verseNativeModule.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(VerseNativeImpl.class)) {
                nativeMethods.put(method.getName(), method);
            }
        }
    }


}
