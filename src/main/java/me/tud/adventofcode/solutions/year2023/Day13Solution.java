package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.Arrays;

@AdventOfCodeSolution(year = 2023, day = 13, name = "Point of Incidence", link = "https://adventofcode.com/2023/day/13")
public class Day13Solution extends Solution {

    @Override
    public Long part1Solution() {
        long columns = 0, rows = 0;
        String[] data = getInputLines();
        for (int from = data.length - 1, to = from + 1; from >= 0; from--) {
            if (from != 0 && !data[from - 1].isEmpty()) continue;
            String[] subArray = Arrays.copyOfRange(data, from, to);
            columns += findVerticalReflection(subArray);
            rows += findHorizontalReflection(subArray);
            to = from - 1;
        }
        return columns + rows * 100;
    }

    @Override
    public Long part2Solution() {
        return null;
    }

    private int findVerticalReflection(String[] data) {
        outer:
        for (int i = 1; i < data[0].length(); i++) {
            for (String line : data) {
                if (!isMirrored(line, i))
                    continue outer;
            }
            return i;
        }
        return 0;
    }

    private int findHorizontalReflection(String[] data) {
        outer:
        for (int i = 1; i < data.length; i++) {
            for (int j = 0; j < data[0].length(); j++) {
                if (!isMirrored(getVerticalLine(data, j), i))
                    continue outer;
            }
            return i;
        }
        return 0;
    }

    private String getVerticalLine(String[] lines, int index) {
        StringBuilder line = new StringBuilder();
        for (String s : lines)
            line.append(s.charAt(index));
        return line.toString();
    }

    private boolean isMirrored(String string, int index) {
        int length = Math.min(string.length() - index, index);
        if (length == 0) return false;
        String leftPart = string.substring(index - length, index);
        String rightPart = string.substring(index, index + length);
        for (int i = 0; i < length; i++) {
            if (leftPart.charAt(i) != rightPart.charAt(length - i - 1))
                return false;
        }
        return true;
    }

}
