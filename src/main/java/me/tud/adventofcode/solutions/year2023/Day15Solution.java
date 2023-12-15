package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@AdventOfCodeSolution(year = 2023, day = 15, name = "Lens Library", link = "https://adventofcode.com/2023/day/15")
public class Day15Solution extends Solution {

    @Override
    public Object part1Solution() {
        return Arrays.stream(getInput().split(","))
                .mapToInt(this::hash)
                .sum();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object part2Solution() {
        Map<String, Integer>[] boxes = new Map[256];
        for (String step : getInput().split(",")) {
            char operation;
            int operationIndex = step.indexOf(operation = '=');
            if (operationIndex == -1) {
                operation = '-';
                operationIndex = step.length() - 1;
            }

            String label = step.substring(0, operationIndex);
            int boxIndex = hash(label);
            Map<String, Integer> box = boxes[boxIndex];
            if (box == null) boxes[boxIndex] = box = new LinkedHashMap<>();

            if (operation == '-') {
                boxes[boxIndex].remove(label);
                continue;
            }

            int focalLength = Integer.parseInt(step.substring(operationIndex + 1));
            box.put(label, focalLength);
        }

        int sum = 0;
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i] == null || boxes[i].isEmpty()) continue;
            Iterator<Integer> iterator = boxes[i].values().iterator();
            int slot = 0;
            while (iterator.hasNext()) sum += (i + 1) * ++slot * iterator.next();
        }
        return sum;
    }

    private int hash(String string) {
        int hash = 0;
        for (int i = 0; i < string.length(); i++) {
            hash += string.charAt(i);
            hash *= 17;
            hash %= 256;
        }
        return hash;
    }

}
