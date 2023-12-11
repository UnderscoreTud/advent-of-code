package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.Arrays;

@AdventOfCodeSolution(year = 2023, day = 2, name = "Cube Conundrum", link = "https://adventofcode.com/2023/day/2")
public class Day2Solution extends Solution {

    @Override
    public Integer part1Solution() {
        int sum = 0;
        for (String line : getInputLines()) {
            int gameID = Integer.parseInt(line.substring(5, line.indexOf(':')));
            boolean possible = Arrays.stream(line.substring(line.indexOf(':') + 2).split("; "))
                    .flatMap(set -> Arrays.stream(set.split(", ")))
                    .noneMatch(data -> {
                        int amount = Integer.parseInt(data.substring(0, data.indexOf(' ')));
                        String color = data.substring(data.indexOf(' ') + 1);
                        return amount > getColorIndex(color) + 12;
                    });
            if (possible) sum += gameID;
        }
        return sum;
    }

    @Override
    public Integer part2Solution() {
        int sum = 0;
        for (String line : getInputLines()) {
            int[] maxAmounts = {0, 0, 0};
            int power = 1;
            Arrays.stream(line.substring(line.indexOf(':') + 2).split("; "))
                    .flatMap(set -> Arrays.stream(set.split(", ")))
                    .forEach(data -> {
                        int amount = Integer.parseInt(data.substring(0, data.indexOf(' ')));
                        String color = data.substring(data.indexOf(' ') + 1);
                        int index = getColorIndex(color);
                        if (amount > maxAmounts[index]) maxAmounts[index] = amount;
                    });
            for (int maxAmount : maxAmounts) power *= maxAmount;
            sum += power;
        }
        return sum;
    }

    private static int getColorIndex(String color) {
        return switch (color) {
            case "red" -> 0;
            case "green" -> 1;
            case "blue" -> 2;
            default -> throw new IllegalStateException("Unexpected color: " + color);
        };
    }
    
}
