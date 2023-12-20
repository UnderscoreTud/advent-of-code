package me.tud.adventofcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;

public abstract class Solution {

    private static final double MILLIS_TO_NANOS = 1_000_000d;
    private final AdventOfCodeSolution adventOfCodeSolution;
    
    {
        adventOfCodeSolution = getClass().getAnnotation(AdventOfCodeSolution.class);
        if (adventOfCodeSolution == null) throw new IllegalStateException("Day " + getClass() + " doesn't have a solution annotation");
    }

    /**
     * @return total execution time in milliseconds
     */
    public double run() {
        int day = getDay();
        long start = System.nanoTime();
        double executionMs, totalExecutionMs = 0;
        Object solution = part1Solution();
        executionMs = (System.nanoTime() - start) / MILLIS_TO_NANOS;
        totalExecutionMs += executionMs;
        System.out.printf("\tSolution for day %s part 1: %s\n", day, solution);
        System.out.printf("\tExecution time for day %s part 1: %.3fms\n", day, executionMs);
        System.out.println();

        start = System.nanoTime();
        solution = part2Solution();
        executionMs = (System.nanoTime() - start) / MILLIS_TO_NANOS;
        totalExecutionMs += executionMs;
        System.out.printf("\tSolution for day %s part 2: %s\n", day, solution);
        System.out.printf("\tExecution time for day %s part 2: %.3fms\n", day, executionMs);
        System.out.println();

        System.out.printf("\tExecution time for day %s: %.3fms\n", day, totalExecutionMs);
        System.out.println();
        return totalExecutionMs;
    }

    public abstract Object part1Solution();

    public abstract Object part2Solution();

    @SuppressWarnings("resource")
    protected InputStream getInputStream() {
        int year = getYear(), day = getDay();
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(Main.SOLUTION_INPUT_LOCATION.formatted(year, day));
        return Objects.requireNonNull(stream, "Couldn't find input file for year " + year + ", day " + day);
    }

    protected String getInput() {
        try {
            return new String(getInputStream().readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Scanner getInputScanner() {
        return new Scanner(getInputStream());
    }

    public String[] getInputLines() {
        return lineStream().toArray(String[]::new);
    }
    
    public Stream<String> lineStream() {
        BufferedReader stream = new BufferedReader(new InputStreamReader(getInputStream()));
        return stream.lines();
    }

    public int getYear() {
        return adventOfCodeSolution.year();
    }

    public int getShortYear() {
        return adventOfCodeSolution.year() % 100;
    }

    public int getDay() {
        return adventOfCodeSolution.day();
    }

    public String getName() {
        return adventOfCodeSolution.name();
    }
    
    public String getLink() {
        return adventOfCodeSolution.link();
    }

    @Override
    public String toString() {
        return "y" + getShortYear() + "_d" + getDay();
    }

}
