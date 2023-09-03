package dev.jok.verse;

import dev.jok.verse.ast.Expr;
import dev.jok.verse.lexer.Token;
import dev.jok.verse.lexer.VerseScanner;
import dev.jok.verse.util.AstPrinter;
import dev.jok.verse.parser.VerseParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VerseLang {

    private static final Logger LOGGER = Logger.getLogger("Verse");

    private static boolean hadError = false;

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

        String fileContent = Files.readString(file.toPath());
        List<Token> tokens = new VerseScanner(fileContent).scanTokens();
        VerseParser parser = new VerseParser(tokens);
        Expr expression = parser.parse();

        if (hadError) {
            System.exit(65);
        }

        System.out.println(expression == null ? "null" : new AstPrinter().print(expression));
    }

    public static void syntaxError(int line, String message) {
        hadError = true;
        LOGGER.log(Level.SEVERE, "[line " + line + "] Syntax Error: " + message);
    }

}
