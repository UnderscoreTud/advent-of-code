package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AdventOfCodeSolution(year = 2023, day = 4, name = "Scratchcards", link = "https://adventofcode.com/2023/day/4")
public class Day4Solution extends Solution {

    @Override
    public Integer part1Solution() {
        return lineStream()
                .map(line -> line.substring(line.indexOf(':') + 1).trim())
                .mapToInt(line -> 1 << calculateWinnings(line) - 1)
                .sum();
    }

    @Override
    public Integer part2Solution() {
        String[] lines = getInputLines();
        int[] collectedCards = new int[lines.length];
        for (int i = 0; i < lines.length; i++) {
            collectedCards[i]++;
            int winnings = calculateWinnings(lines[i].substring(lines[i].indexOf(':') + 1).trim());
            for (int j = 1; j <= winnings; j++)
                collectedCards[i + j] += collectedCards[i];
        }
        return Arrays.stream(collectedCards).sum();
    }

    private int calculateWinnings(String line) {
        String[] data = line.split(" \\| ");
        Set<Integer> theirNumbers = Arrays.stream(data[0].split(" "))
                .filter(number -> !number.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        Set<Integer> ourNumbers = Arrays.stream(data[1].split(" "))
                .filter(number -> !number.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        ourNumbers.retainAll(theirNumbers);
        return ourNumbers.size();
    }
    
}
