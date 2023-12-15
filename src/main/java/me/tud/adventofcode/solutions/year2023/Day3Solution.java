package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

@AdventOfCodeSolution(year = 2023, day = 3, name = "Gear Ratios", link = "https://adventofcode.com/2023/day/3")
public class Day3Solution extends Solution {

    @Override
    public Integer part1Solution() {
        Iterator<String> lineIterator = lineStream().iterator();
        int sum = 0;
        String upper = null, current = lineIterator.next(), lower = lineIterator.next();
        while (current != null) {
            sum += sumOfPartNumbers(upper, current, lower);
            upper = current;
            current = lower;
            lower = lineIterator.hasNext() ? lineIterator.next() : null;
        }
        return sum;
    }

    @Override
    public Integer part2Solution() {
        Iterator<String> lineIterator = lineStream().iterator();
        int sum = 0;
        String upper = null, current = lineIterator.next(), lower = lineIterator.next();
        while (current != null) {
            sum += sumOfGearRatios(upper, current, lower);
            upper = current;
            current = lower;
            lower = lineIterator.hasNext() ? lineIterator.next() : null;
        }
        return sum;
    }

    private int sumOfPartNumbers(@Nullable String upper, String current, @Nullable String lower) {
        if (upper == null) upper = ".".repeat(current.length());
        if (lower == null) lower = ".".repeat(current.length());
        int sum = 0;
        Range numberRange = nextNumber(current, 0);
        while (numberRange != null) {
            if (isPartNumber(upper, current, lower, numberRange))
                sum += Integer.parseInt(numberRange.substring(current)); 
            numberRange = nextNumber(current, numberRange.end());
        }
        return sum;
    }

    private boolean isPartNumber(String upper, String current, String lower, Range range) {
        for (int i = Math.max(0, range.start() - 1); i < Math.min(upper.length(), range.end() + 1); i++) {
            if (upper.charAt(i) == '.' && lower.charAt(i) == '.') continue;
            return true;
        }
        if (range.start() != 0 && current.charAt(range.start() - 1) != '.') return true;
        return range.end != current.length() && current.charAt(range.end) != '.';
    }

    private Range nextNumber(String string, int startIndex) {
        int from = -1, to = 0;
        for (int i = Math.max(0, startIndex); i < string.length(); i++) {
            char c = string.charAt(i);
            if ('0' > c || c > '9') {
                if (from == -1) continue;
                break;
            }
            if (from == -1) from = i;
            to = i;
        }
        if (from == -1) return null;
        return new Range(from, to + 1);
    }

    private int sumOfGearRatios(@Nullable String upper, String current, @Nullable String lower) {
        if (upper == null) upper = ".".repeat(current.length());
        if (lower == null) lower = ".".repeat(current.length());
        int sum = 0;
        int lastGearIndex = current.indexOf('*');
        String[] lines = {upper, current, lower};
        while (lastGearIndex != -1) {
            int[] found = new int[2];
            int currentIndex = 0;
            for (String line : lines) {
                line = line.substring(Math.max(0, lastGearIndex - 3), Math.min(lastGearIndex + 4, line.length()));
                Range range;

                if ((range = nextNumber(line, 0)) != null && range.end() > 2 && range.start() < 5) {
                    found[currentIndex++] = Integer.parseInt(range.substring(line));
                    if (currentIndex == 2) break;
                }

                if (range != null && range.end() <= 3 && (range = nextNumber(line, range.end())) != null && range.start() < 5) {
                    found[currentIndex++] = Integer.parseInt(range.substring(line));
                    if (currentIndex == 2) break;
                }
            }
            if (currentIndex == 2) sum += found[0] * found[1];
            lastGearIndex = current.indexOf('*', lastGearIndex + 1);
        }
        return sum;
    }

    private record Range(int start, int end) {

        public String substring(String string) {
            return string.substring(start, end);
        }

    }

}
