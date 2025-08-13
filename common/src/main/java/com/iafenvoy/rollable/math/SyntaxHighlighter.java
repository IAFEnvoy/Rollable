package com.iafenvoy.rollable.math;

import com.iafenvoy.rollable.Rollable;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SyntaxHighlighter {
    private static final boolean debugLog = false;

    public static Text highlightText(String text) {
        MutableText formattedText = Text.literal("");
        SyntaxHighlightContext context = new SyntaxHighlightContext(text);

        if (debugLog) Rollable.LOGGER.info("Begun syntax highlighting");

        while (context.getCurrent() != (char) 0) {
            if (context.getCurrent() == '$') { //variables
                formattedText.append(String.valueOf(context.getCurrent()));
                context.position++;
                if (debugLog) Rollable.LOGGER.info("Begun coloring variable");

                while (isLetter(context.getCurrent()) || context.getCurrent() == '_') {
                    formattedText.append(formatText(context.getCurrent(), SyntaxType.Variable));
                    context.position++;
                    if (debugLog) Rollable.LOGGER.info("Coloring variable");
                }
            } else if (context.getCurrent() == '-' || context.getCurrent() == '+') { //unary operators
                if (Character.isDigit(context.peek()) && context.lastIsNotValue()) {
                    formattedText.append(formatText(context.getCurrent(), SyntaxType.Number));
                    context.position++;
                    if (debugLog) Rollable.LOGGER.info("Coloring number");
                } else if (isLetter(context.peek()) && context.lastIsNotValue()) {
                    formattedText.append(formatText(context.getCurrent(), SyntaxType.Function));
                    context.position++;
                    if (debugLog) Rollable.LOGGER.info("Coloring function");
                } else {
                    formattedText.append(formatText(context.getCurrent(), SyntaxType.Operator));
                    context.position++;
                    if (debugLog) Rollable.LOGGER.info("Coloring operator");
                }
            } else if (Character.isDigit(context.getCurrent()) || context.getCurrent() == '.') { //numbers
                formattedText.append(formatText(context.getCurrent(), SyntaxType.Number));
                context.position++;
                if (debugLog) Rollable.LOGGER.info("Coloring number");
            } else if (isLetter(context.getCurrent())) { //functions and constants
                StringBuilder builder = new StringBuilder();

                while (isLetter(context.getCurrent()) || context.getCurrent() == '_') {
                    builder.append(context.getCurrent());
                    context.position++;
                    if (debugLog) Rollable.LOGGER.info("Reading possible function or constant");
                }

                String builtResult = builder.toString();

                if (isKeyword(builtResult) && context.getCurrent() == '(') {
                    formattedText.append(formatText(builtResult, SyntaxType.Function));
                    if (debugLog) Rollable.LOGGER.info("Coloring function");
                } else if (isConstant(builtResult)) {
                    formattedText.append(formatText(builtResult, SyntaxType.Constant));
                    if (debugLog) Rollable.LOGGER.info("Coloring constant");
                } else {
                    formattedText.append(formatText(builtResult, SyntaxType.Error));
                    if (debugLog) Rollable.LOGGER.info("Coloring error");
                }
            } else if (isOperator(context.getCurrent())) { //typical operators
                formattedText.append(formatText(context.getCurrent(), SyntaxType.Operator));
                context.position++;
                if (debugLog) Rollable.LOGGER.info("Coloring operator");
            } else if (isScope(context.getCurrent())) { //parentheses
                formattedText.append(formatText(context.getCurrent(), SyntaxType.Scope));
                context.position++;
                if (debugLog) Rollable.LOGGER.info("Skipping parentheses");
            } else if (Character.isWhitespace(context.getCurrent())) { //whitespace
                formattedText.append(String.valueOf(context.getCurrent()));
                context.position++;
                if (debugLog) Rollable.LOGGER.info("Skipping whitespace");
            } else { //errors
                formattedText.append(formatText(context.getCurrent(), SyntaxType.Error));
                context.position++;
                if (debugLog) Rollable.LOGGER.info("Coloring errors");
            }
        }

        if (debugLog) Rollable.LOGGER.info("Finished syntax coloring");
        return formattedText;
    }

    public static boolean isConstant(String str) {
        switch (str) {
            case "PI", "E", "TO_RAD", "TO_DEG" -> {
                return true;
            }
        }

        return false;
    }

    public static boolean isKeyword(String str) {
        switch (str) {
            case "sqrt", "sin", "cos",
                 "tan", "asin", "acos",
                 "atan", "abs", "exp",
                 "ceil", "floor", "log",
                 "round", "randint", "min",
                 "max" -> {
                return true;
            }
        }

        return false;
    }

    public static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    public static boolean isScope(char c) {
        return c == ',' || c == '(' || c == ')';
    }

    public static MutableText formatText(char ch, SyntaxType type) {
        String str = String.valueOf(ch);
        return formatText(str, type);
    }

    public static MutableText formatText(String str, SyntaxType type) {
        switch (type) {
            case Variable -> {
                return Text.literal(str).formatted(Formatting.GREEN);
            }

            case Operator -> {
                return Text.literal(str).formatted(Formatting.LIGHT_PURPLE);
            }

            case Error -> {
                return Text.literal(str).formatted(Formatting.RED);
            }

            case Number -> {
                return Text.literal(str).formatted(Formatting.AQUA);
            }

            case Function -> {
                return Text.literal(str).formatted(Formatting.YELLOW);
            }

            case Constant -> {
                return Text.literal(str).setStyle(Style.EMPTY.withColor(0xFFA500));
            }

            case Scope -> {
                return Text.literal(str);
            }
        }

        return null;
    }
}

class SyntaxHighlightContext {
    public int position = 0;
    public String rawText;

    public SyntaxHighlightContext(String raw) {
        this.rawText = raw;
    }

    public String peek(int amount) {
        if (this.position + amount >= this.rawText.length()) return null;
        return this.rawText.substring(this.position, this.position + amount);
    }

    public char peek() {
        return this.getByIndex(this.position + 1);
    }

    public char getCurrent() {
        return this.getByIndex(this.position);
    }

    public char getByIndex(int i) {
        if (i >= this.rawText.length()) return (char) 0;
        return this.rawText.charAt(i);
    }

    public boolean lastIsNotValue() {
        int tempPos = this.position;

        while (tempPos > 0) {
            tempPos--;

            if (SyntaxHighlighter.isOperator(this.getByIndex(tempPos))
                    || SyntaxHighlighter.isScope(this.getByIndex(tempPos))) {
                return true;
            } else if (!Character.isWhitespace(this.getByIndex(tempPos))) {
                break;
            }
        }

        return false;
    }
}

enum SyntaxType {
    Variable,
    Operator,
    Error,
    Scope,
    Function,
    Number,
    Constant
}