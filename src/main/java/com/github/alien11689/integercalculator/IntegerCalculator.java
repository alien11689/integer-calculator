package com.github.alien11689.integercalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class IntegerCalculator {

    private IntegerCalculator() {
    }

    static int calculate(String expression) {
        if (expression == null) {
            throw new IllegalArgumentException("Expression cannot be null");
        }

        List<Integer> numbers = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();

        StringBuilder curNumber = new StringBuilder();
        boolean readingNumber = true;

        for (int i = 0; i < expression.length(); ++i) {
            char current = expression.charAt(i);
            if (Character.isWhitespace(current)) {
                if (readingNumber && !curNumber.isEmpty()) {
                    numbers.add(Integer.parseInt(curNumber.toString()));
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
                    numbers.add(Integer.parseInt(curNumber.toString()));
                    curNumber = new StringBuilder();
                }
                operators.add(Operator.fromChar(current));
                readingNumber = true;
            } else {
                throw new IllegalArgumentException("Unexpected character: " + current + " at position " + i);
            }
        }
        if (readingNumber) {
            numbers.add(Integer.parseInt(curNumber.toString()));
        }

        reduce(Set.of(Operator.MULTIPLY, Operator.DIVISION), operators, numbers);
        reduce(Set.of(Operator.ADD, Operator.SUBTRACT), operators, numbers);

        if (numbers.size() == 1 && operators.isEmpty()) {
            return numbers.getFirst();
        } else {
            throw new IllegalArgumentException("Invalid expression");
        }
    }

    private static void reduce(Set<Operator> onlyUseOperators, List<Operator> operators, List<Integer> numbers) {
        int i = 0;
        while (i < operators.size()) {
            Operator operator = operators.get(i);
            if (onlyUseOperators.contains(operator)) {
                int result = operator.calculate(numbers.get(i), numbers.get(i + 1));
                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
            } else {
                ++i;
            }
        }
    }

    private enum Operator {
        ADD {
            @Override
            int calculate(int left, int right) {
                return Math.addExact(left, right);
            }
        },
        SUBTRACT {
            @Override
            int calculate(int left, int right) {
                return Math.subtractExact(left, right);
            }
        },
        MULTIPLY {
            @Override
            int calculate(int left, int right) {
                return Math.multiplyExact(left, right);
            }
        },
        DIVISION {
            @Override
            int calculate(int left, int right) {
                return left / right;
            }
        };

        static Operator fromChar(char c) {
            return switch (c) {
                case '+' -> ADD;
                case '-' -> SUBTRACT;
                case '*' -> MULTIPLY;
                case '/' -> DIVISION;
                default -> throw new IllegalArgumentException("Unknown operator: " + c);
            };
        }

        abstract int calculate(int left, int right);
    }
}

