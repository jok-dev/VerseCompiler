package dev.jok.verse;

import dev.jok.verse.ast.Stmt;
import dev.jok.verse.interpreter.VerseInterpreter;
import dev.jok.verse.lexer.Token;
import dev.jok.verse.lexer.VerseScanner;
import dev.jok.verse.parser.VerseParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VerseLang {

    private static final Logger LOGGER = Logger.getLogger("Verse");

    private static boolean hadSyntaxError = false;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            LOGGER.log(Level.SEVERE, "Usage: verse [script]");
            System.exit(64);
        }

        String fileName = args[0];
        File file = new File(fileName);
        if (!file.exists()) {
            LOGGER.log(Level.SEVERE, "File does not exist: " + fileName);
            System.exit(64);
        }

        if (!file.isFile()) {
            LOGGER.log(Level.SEVERE, "Input is not a file: " + fileName);
            System.exit(64);
        }

        LOGGER.log(Level.INFO, "Parsing file: " + fileName);

        String fileContent = Files.readString(file.toPath());
        List<Token> tokens = new VerseScanner(fileContent).scanTokens();
        VerseParser parser = new VerseParser(tokens);
        List<Stmt> statements = parser.parse();

        if (hadSyntaxError) {
            System.exit(65);
        }

        VerseInterpreter interp = new VerseInterpreter();
        interp.interpret(statements);
    }

    public static void syntaxError(@NotNull Token current, @Nullable Token next, String message) {
        message = message.replace("{peek}", current.type + " \"" + current.lexeme + "\"");
        message = message.replace("{peekNext}", next != null ? current.type + " " + next.lexeme : "null");
        syntaxError(current.line, current.col, message);
    }

    public static void syntaxError(int line, int col, String message) {
        hadSyntaxError = true;
        LOGGER.log(Level.SEVERE, "Syntax Error: " + message + " [ Ln " + line + ", Col " + col + " ]");
    }

}
