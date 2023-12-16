package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.Arrays;

@AdventOfCodeSolution(year = 2023, day = 6, name = "Wait For It", link = "https://adventofcode.com/2023/day/6")
public class Day6Solution extends Solution {

    @Override
    public Long part1Solution() {
        String[] input = getInputLines();
        long[] times = Arrays.stream(input[0].substring("Time: ".length()).split(" "))
                .filter(num -> !num.isBlank())
                .map(String::trim)
                .mapToLong(Long::parseLong)
                .toArray();
        long[] distances = Arrays.stream(input[1].substring("Distance: ".length()).split(" "))
                .filter(num -> !num.isBlank())
                .map(String::trim)
                .mapToLong(Long::parseLong)
                .toArray();
        long product = 1;
        for (int i = 0; i < times.length; i++)
            product *= getWinningWays(times[i], distances[i]);
        return product;
    }

    @Override
    public Long part2Solution() {
        String[] input = getInputLines();
        long time = Long.parseLong(input[0].substring("Time: ".length()).replaceAll("\\s+", ""));
        long distance = Long.parseLong(input[1].substring("Distance: ".length()).replaceAll("\\s+", ""));
        return getWinningWays(time, distance);
    }

    private long getWinningWays(long time, long distance) {
        long halfTime = time / 2;
        for (long heldTime = 1; heldTime < halfTime; heldTime++) {
            long travelledDistance = heldTime * (time - heldTime);
            if (travelledDistance > distance) return (halfTime - heldTime + 1) * 2 - (1 - time & 1);
        }
        return 0;
    }

}
