package com.github.alien11689.integercalculator;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;

class IntegerCalculator {

    private static final Set<String> operators = Set.of("+", "-", "*", "/");

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
        String[] tokens = expression.split("\\s+");

        List<ExpressionElement> expressionInReversePolishNotation = new ArrayList<>();
        Stack<Operator> stack = new Stack<>();

        for (String token : tokens) {
            if (operators.contains(token)) {
                Operator operator = Operator.parse(token);
                while (!stack.isEmpty() && stack.peek().getPriority() >= operator.getPriority()) {
                    expressionInReversePolishNotation.add(stack.pop());
                }
                stack.push(operator);
            } else {
                expressionInReversePolishNotation.add(new Num(Integer.parseInt(token)));
            }
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
                    try {
                        int right = stack.pop();
                        int left = stack.pop();
                        stack.push(o.calculate(left, right));
                    } catch (EmptyStackException __) {
                        throw new IllegalArgumentException("Invalid expression");
                    }
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

        static Operator parse(String token) {
            return switch (token) {
                case "+" -> ADD;
                case "-" -> SUBTRACT;
                case "*" -> MULTIPLY;
                case "/" -> DIVISION;
                default -> throw new IllegalArgumentException("Unknown operator: " + token);
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

