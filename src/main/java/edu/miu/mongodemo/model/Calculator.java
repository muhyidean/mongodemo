package edu.miu.mongodemo.model;

public class Calculator {
    private double value = 0.0;

    public void add(double number) {
        value += number;
    }

    public double getValue() {
        return value;
    }
}

