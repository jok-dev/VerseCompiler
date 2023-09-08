package dev.jok.verse.interpreter.modules;

import dev.jok.verse.interpreter.VerseNativeImpl;

public class VerseNativeModule implements NativeModule {

    @VerseNativeImpl
    public static void Print(String message) {
        System.out.println(message);
    }

    @VerseNativeImpl
    public static float Sqrt(float value) {
        return (float) Math.sqrt(value);
    }

    @VerseNativeImpl
    public static String Join(String[] strings, String separator) {
        return String.join(separator, strings);
    }

    @VerseNativeImpl
    public static String ToString(char character) {
        return String.valueOf(character);
    }



}
