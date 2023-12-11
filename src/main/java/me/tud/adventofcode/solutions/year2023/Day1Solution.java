package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

@AdventOfCodeSolution(year = 2023, day = 1, name = "Trebuchet?!", link = "https://adventofcode.com/2023/day/1")
public class Day1Solution extends Solution {

    private static final String[] DIGITS = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};

    @Override
    public Integer part1Solution() {
        return lineStream()
                .mapToInt(line -> {
                    int firstDigit = -1, lastDigit = firstDigit;
                    for (int i = 0; i < line.length(); i++) {
                        int digit = line.charAt(i) - '0';
                        if (0 > digit || digit > 9) continue;
                        if (firstDigit == -1) firstDigit = digit;
                        lastDigit = digit;
                    }
                    return firstDigit * 10 + lastDigit;
                })
                .sum();
    }

    @Override
    public Integer part2Solution() {
        return lineStream()
                .mapToInt(line -> {
                    int firstDigit = -1, lastDigit = firstDigit;
                    for (int i = 0; i < line.length(); i++) {
                        int digit = line.charAt(i) - '0';
                        if (0 <= digit && digit <= 9) {
                            if (firstDigit == -1) firstDigit = digit;
                            lastDigit = digit;
                            continue;
                        }

                        for (int j = 0; j < DIGITS.length; j++) {
                            if (!line.startsWith(DIGITS[j], i)) continue;
                            if (firstDigit == -1) firstDigit = j + 1;
                            lastDigit = j + 1;
                            break;
                        }
                    }
                    return firstDigit * 10 + lastDigit;
                })
                .sum();
    }

}
