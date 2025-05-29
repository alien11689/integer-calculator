package com.github.alien11689.integercalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiFunction;

class IntegerCalculator {

    private IntegerCalculator() {
    }

    static int calculate(String expression) {
        if (expression == null) {
            throw new IllegalArgumentException("Expression cannot be null");
        }

        List<ExpressionElement> expressionInReversePolishNotation = convertExpressionToReversePolishNotation(expression);

        return calculateExpressionInReversePolishNotation(expressionInReversePolishNotation);
    }

    private static List<ExpressionElement> convertExpressionToReversePolishNotation(String expression) {
        List<ExpressionElement> expressionInReversePolishNotation = new ArrayList<>();
        Stack<Operator> stack = new Stack<>();

        StringBuilder curNumber = new StringBuilder();
        boolean readingNumber = true;

        for (int i = 0; i < expression.length(); ++i) {
            char current = expression.charAt(i);
            if (Character.isWhitespace(current)) {
                if (readingNumber && !curNumber.isEmpty()) {
                    expressionInReversePolishNotation.add(new Num(Integer.parseInt(curNumber.toString())));
                    curNumber = new StringBuilder();
                    readingNumber = false;
                }
            } else if (Character.isDigit(current)) {
                readingNumber = true;
                curNumber.append(current);
            } else if (readingNumber && curNumber.isEmpty() && current == '-') {
                curNumber.append(current);
            } else if (current == '+' || current == '-' || current == '*' || current == '/') {
                if (readingNumber) {
                    expressionInReversePolishNotation.add(new Num(Integer.parseInt(curNumber.toString())));
                    curNumber = new StringBuilder();
                }
                Operator operator = Operator.fromChar(current);
                while (!stack.isEmpty() && stack.peek().getPriority() >= operator.getPriority()) {
                    expressionInReversePolishNotation.add(stack.pop());
                }
                stack.push(operator);
                readingNumber = true;
            } else {
                throw new IllegalArgumentException("Unexpected character: " + current + " at position " + i);
            }
        }
        if (readingNumber) {
            expressionInReversePolishNotation.add(new Num(Integer.parseInt(curNumber.toString())));
        }
        while (!stack.isEmpty()) {
            expressionInReversePolishNotation.add(stack.pop());
        }
        return expressionInReversePolishNotation;
    }

    private static int calculateExpressionInReversePolishNotation(List<ExpressionElement> expression) {
        Stack<Integer> stack = new Stack<>();
        for (ExpressionElement expressionElement : expression) {
            switch (expressionElement) {
                case Num(int value) -> stack.push(value);
                case Operator o -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    stack.push(o.calculate(left, right));
                }
            }
        }
        if (stack.size() == 1) {
            return stack.pop();
        } else {
            throw new IllegalArgumentException("Invalid expression");
        }
    }

    private sealed interface ExpressionElement {
    }

    private record Num(int value) implements ExpressionElement {
    }

    private enum Operator implements ExpressionElement {
        ADD(1, Math::addExact),
        SUBTRACT(1, Math::subtractExact),
        MULTIPLY(2, Math::multiplyExact),
        DIVISION(2, (a, b) -> a / b);

        private final int priority;

        private final BiFunction<Integer, Integer, Integer> operation;

        Operator(int priority, BiFunction<Integer, Integer, Integer> operation) {
            this.priority = priority;
            this.operation = operation;
        }

        static Operator fromChar(char c) {
            return switch (c) {
                case '+' -> ADD;
                case '-' -> SUBTRACT;
                case '*' -> MULTIPLY;
                case '/' -> DIVISION;
                default -> throw new IllegalArgumentException("Unknown operator: " + c);
            };
        }

        int calculate(int left, int right) {
            return operation.apply(left, right);
        }

        int getPriority() {
            return priority;
        }
    }

}

