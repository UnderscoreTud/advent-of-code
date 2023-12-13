package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.*;

@AdventOfCodeSolution(year = 2023, day = 12, name = "Hot Springs", link = "https://adventofcode.com/2023/day/12")
public class Day12Solution extends Solution {

    private static final Map<SpringRow, Long> cache = new HashMap<>();
    
    @Override
    public Long part1Solution() {
        return lineStream()
                .map(this::processSprings)
                .mapToLong(this::getAllArrangements)
                .sum();
    }

    @Override
    public Long part2Solution() {
        return lineStream()
                .map(this::processSprings)
                .map(row -> row.unfold(5))
                .mapToLong(this::getAllArrangements)
                .sum();
    }

    private SpringRow processSprings(String line) {
        int split = line.indexOf(' ');
        List<Integer> damagedSprings = Arrays.stream(line.substring(split + 1).split(","))
                .map(Integer::parseInt)
                .toList();
        return new SpringRow(line.substring(0, split), damagedSprings);
    }

    private long getAllArrangements(SpringRow row) {
        if (cache.containsKey(row))
            return cache.get(row);
        long arrangements = calculateArrangements(row);
        cache.put(row, arrangements);
        return arrangements;
    }

    private long calculateArrangements(SpringRow row) {
        if (row.pattern.isEmpty()) return row.damagedSprings.isEmpty() ? 1 : 0;

        switch (row.pattern.charAt(0)) {
            case '.' -> { return getAllArrangements(row.next()); }
            case '?' -> { return getAllArrangements(row.attempt('#')) + getAllArrangements(row.attempt('.')); }
        }

        if (row.damagedSprings.isEmpty())
            return 0;

        int groupSize = row.damagedSprings.get(0);
        if (row.pattern.length() < groupSize)
            return 0;

        if (!row.pattern.chars().limit(groupSize).allMatch(c -> c == '#' || c == '?'))
            return 0;

        if (row.pattern.length() == groupSize)
            return row.damagedSprings.size() == 1 ? 1 : 0;

        return switch (row.pattern.charAt(groupSize)) {
            case '.', '?' -> getAllArrangements(row.nextGroup());
            default -> 0;
        };
    }

    public record SpringRow(String pattern, List<Integer> damagedSprings) {

        public SpringRow unfold(int times) {
            String unfoldedPattern = (pattern + '?').repeat(times);
            unfoldedPattern = unfoldedPattern.substring(0, unfoldedPattern.length() - 1);

            List<Integer> unfoldedDamagedSprings = new ArrayList<>(damagedSprings.size() * times);
            for (int i = 0; i < damagedSprings.size() * times; i++)
                unfoldedDamagedSprings.add(damagedSprings.get(i % damagedSprings.size()));

            return new SpringRow(unfoldedPattern, unfoldedDamagedSprings);
        }

        public SpringRow next() {
            return new SpringRow(pattern.substring(1), damagedSprings);
        }

        public SpringRow attempt(char firstChar) {
            return new SpringRow(firstChar + pattern.substring(1), damagedSprings);
        }

        public SpringRow nextGroup() {
            int length = damagedSprings.get(0);
            return new SpringRow(pattern.substring(length + 1), damagedSprings.subList(1, damagedSprings.size()));
        }

    }

}
