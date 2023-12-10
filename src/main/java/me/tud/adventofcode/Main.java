package me.tud.adventofcode;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class Main {

    public static final String SOLUTION_LOCATION = "me.tud.adventofcode.solutions.year%s.Day%sSolution";
    public static final String SOLUTION_INPUT_LOCATION = "inputs/year%s/day%s.txt";

    private static final Supplier<int[]> ALL_DAYS = () -> {
        int[] days = new int[25];
        for (int i = 0; i < days.length; i++)
            days[i] = i + 1;
        return days;
    };

    public static void main(String[] args) throws IOException {
        int[] years, days;
        int warmupIterations;

        if (args.length >= 1) years = Main.parseYears(args[0]);
        else years = ask("What year(s) do you want to execute? (E.g. all,current,2022)", Main::parseYears);

        if (args.length >= 2) days = Main.parseDays(args[1]);
        else days = ask("What day(s) do you want to execute? (E.g. all,current,3)", Main::parseDays);
        
        if (args.length >= 3) {
            try {
                warmupIterations = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.err.println("Couldn't parse number for warmup iterations: " + args[2]);
                System.err.println("Defaulting to 0");
                warmupIterations = 0;
            }
        } else {
            warmupIterations = ask("How many warmup iterations do you want to run before measuring the performance? (0 for none)", answer -> {
                try {
                    return Integer.parseInt(answer);
                } catch (NumberFormatException e) {
                    System.err.println("Couldn't parse number for warmup iterations: " + answer);
                    System.err.println("Defaulting to 0");
                    return 0;
                }
            });
        }

        if (warmupIterations > 0) warmup(years, days, warmupIterations);

        System.out.println();
        System.out.println("================================");
        System.out.println("===== Executing solutions ======");
        System.out.println("================================");
        System.out.println();

        long totalExecutionMs = 0;
        Iterator<Solution> solutionIterator = getSolutionIterator(years, days);
        while (solutionIterator.hasNext()) {
            Solution solution = solutionIterator.next();
            if (solution == null) continue;

            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("\tYear " + solution.getYear() + ", Day " + solution.getDay());
            System.out.println("\tName: " + solution.getName());
            System.out.println("\tLink: " + solution.getLink());
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println();

            totalExecutionMs += solution.run();
        }

        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println();
        System.out.println("Execution for the entire program: " + totalExecutionMs + "ms");
    }

    private static void warmup(int[] years, int[] days, int iterations) {
        int iterationInterval = iterations / 10;
        System.out.println();
        System.out.println("====== Warming Up... =======");
        for (int i = 0; i < iterations; i++) {
            if (i % iterationInterval == 0)
                System.out.println("Iteration: " + i);
            Iterator<Solution> solutionIterator = getSolutionIterator(years, days);
            while (solutionIterator.hasNext()) {
                Solution solution = solutionIterator.next();
                if (solution == null) continue;
                solution.part1Solution();
                solution.part2Solution();
            }
        }
        System.out.println("===== Finished Warmup ======");
    }

    private static Iterator<Solution> getSolutionIterator(int[] years, int[] days) {
        return new Iterator<>() {

            private int yearIndex, dayIndex;

            @Override
            public boolean hasNext() {
                return yearIndex < years.length && dayIndex < days.length;
            }

            @Override
            public Solution next() {
                Solution solution = getSolution(years[yearIndex], days[dayIndex]);
                if (++dayIndex >= days.length) {
                    dayIndex = 0;
                    yearIndex++;
                }
                return solution;
            }

        };
    }

    private static Solution getSolution(int year, int day) {
        try {
            Class<?> cls = Class.forName(SOLUTION_LOCATION.formatted(year, day));
            if (!Solution.class.isAssignableFrom(cls))
                throw new IllegalStateException("Class '" + cls + "' must extend '" + Solution.class + "'");
            return cls.asSubclass(Solution.class).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            System.err.printf("Couldn't find solution for year %s, day %s%n", year, day);
            return null;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T ask(String question, Function<String, T> parser) throws IOException {
        System.out.println(question);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return parser.apply(reader.readLine());
    }

    private static int currentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) - (calendar.get(Calendar.MONTH) == Calendar.DECEMBER ? 0 : 1);
    }

    private static int[] parseYears(String input) {
        String formattedInput = input.toLowerCase(Locale.ENGLISH);
        if (input.equals("all")) {
            try {
                return ClassUtils.getSubpackages("me.tud.adventofcode.solutions").stream()
                        .map(pkg -> pkg.substring(pkg.length() - 4))
                        .mapToInt(Integer::parseInt)
                        .toArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (input.equals("current"))
            return new int[]{currentYear()};
        return parseInts(input, () -> {
            int currentYear = currentYear();
            System.err.println("Couldn't parse year(s): " + formattedInput);
            System.err.println("Defaulting to the current year (" + currentYear + ")");
            return new int[]{currentYear};
        });
    }

    private static int[] parseDays(String input) {
        String formattedInput = input.toLowerCase(Locale.ENGLISH);
        if (input.equals("all"))
            return ALL_DAYS.get();
        int[] days = parseInts(input, () -> {
            System.err.println("Couldn't parse day(s): " + formattedInput);
            System.err.println("Defaulting to all the days (1..25)");
            return ALL_DAYS.get();
        });
        for (int day : days) {
            if (0 > day || day > 25) {
                System.err.println("A day can only be from 1 to 25");
                System.err.println("Given: " + Arrays.toString(days));
                System.err.println("Defaulting to all the days (1..25)");
                return ALL_DAYS.get();
            }
        }
        return days;
    }

    private static int[] parseInts(String input, Supplier<int[]> ifError) {
        try {
            return Arrays.stream(input.split(","))
                    .map(String::trim)
                    .mapToInt(Integer::parseInt)
                    .toArray();
        } catch (NumberFormatException e) {
            return ifError.get();
        }
    }
    
}
