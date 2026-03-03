package edu.miu.mongodemo.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * Calculator Tool for Spring AI
 * 
 * This tool provides calculator functions that the AI can call
 * when users ask mathematical questions.
 */
@Component
public class CalculatorTool {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorTool.class);

    @Tool(description = "Adds two numbers together")
    public long add(
            @ToolParam(description = "First number") long a,
            @ToolParam(description = "Second number") long b) {
        logger.info("Adding {} + {}", a, b);
        return a + b;
    }

    @Tool(description = "Subtracts number b from a")
    public long subtract(
            @ToolParam(description = "Number a") long a,
            @ToolParam(description = "Number b") long b) {
        logger.info("Subtracting {} - {}", a, b);
        return a - b;
    }

    @Tool(description = "Multiplies two numbers together")
    public long multiply(
            @ToolParam(description = "First number") long a,
            @ToolParam(description = "Second number") long b) {
        logger.info("Multiplying {} * {}", a, b);
        return a * b;
    }

    @Tool(description = "Divides number a by b")
    public double divide(
            @ToolParam(description = "Number a (dividend)") long a,
            @ToolParam(description = "Number b (divisor)") long b) {
        logger.info("Dividing {} / {}", a, b);
        if (b == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return (double) a / b;
    }
}
