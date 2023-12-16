package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AdventOfCodeSolution(year = 2023, day = 9, name = "Mirage Maintenance", link = "https://adventofcode.com/2023/day/9")
public class Day9Solution extends Solution {

    @Override
    public Integer part1Solution() {
        return lineStream()
                .map(line -> Arrays.stream(line.split(" ")).map(Integer::parseInt).toList())
                .mapToInt(data -> extrapolate(data, false))
                .sum();
    }

    @Override
    public Integer part2Solution() {
        return lineStream()
                .map(line -> Arrays.stream(line.split(" ")).map(Integer::parseInt).toList())
                .mapToInt(data -> extrapolate(data, true))
                .sum();
    }

    private int extrapolate(List<Integer> list, boolean backwards) {
        List<Integer> differences = new ArrayList<>(list.size() - 1);
        boolean allZeros = true;
        for (int i = 0; i < list.size() - 1; i++) {
            int difference = list.get(i + 1) - list.get(i);
            if (difference != 0) allZeros = false;
            differences.add(difference);
        }
        if (backwards)
            return list.get(0) - (allZeros ? 0  : extrapolate(differences, true));
        return list.get(list.size() - 1) + (allZeros ? 0 : extrapolate(differences, false));
    }
    
}
