package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.Arrays;

@AdventOfCodeSolution(year = 2023, day = 13, name = "Point of Incidence", link = "https://adventofcode.com/2023/day/13")
public class Day13Solution extends Solution {

    @Override
    public Long part1Solution() {
        return solve(0);
    }

    @Override
    public Long part2Solution() {
        return solve(1);
    }

    private long solve(int allowedSmudges) {
        long columns = 0, rows = 0;
        String[] data = getInputLines();
        for (int from = 0, to = 0; to < data.length; to++) {
            if (data.length - to != 1 && !data[to].isEmpty()) continue;
            String[] layers = Arrays.copyOfRange(data, from, to);
            columns += findVerticalReflection(layers, allowedSmudges);
            rows += findHorizontalReflection(layers, allowedSmudges);
            from = to + 1;
        }
        return columns + rows * 100;
    }

    private int findVerticalReflection(String[] layers, int smudges) {
        outer:
        for (int i = 1; i < layers[0].length(); i++) {
            int totalSmudges = 0;
            for (String line : layers) {
                totalSmudges += isMirrored(line, i);
                if (totalSmudges > smudges) continue outer;
            }
            if (totalSmudges == smudges) return i;
        }
        return 0;
    }

    private int findHorizontalReflection(String[] layers, int smudges) {
        outer:
        for (int i = 1; i < layers.length; i++) {
            int totalSmudges = 0;
            for (int j = 0; j < layers[0].length(); j++) {
                totalSmudges += isMirrored(getVerticalLine(layers, j), i);
                if (totalSmudges > smudges) continue outer;
            }
            if (totalSmudges == smudges) return i;
        }
        return 0;
    }

    private String getVerticalLine(String[] lines, int index) {
        StringBuilder line = new StringBuilder();
        for (String s : lines)
            line.append(s.charAt(index));
        return line.toString();
    }

    /**
     * @return the amount of differences
     */
    private int isMirrored(String string, int index) {
        int length = Math.min(string.length() - index, index);
        if (length == 0) return -1;
        String leftPart = string.substring(index - length, index);
        String rightPart = string.substring(index, index + length);
        int diff = 0;
        for (int i = 0; i < length; i++) {
            if (leftPart.charAt(i) != rightPart.charAt(length - i - 1))
                diff++;
        }
        return diff;
    }

}
