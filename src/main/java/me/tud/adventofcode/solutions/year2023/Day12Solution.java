package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AdventOfCodeSolution(year = 2023, day = 12, name = "Cube Conundrum", link = "https://adventofcode.com/2023/day/12")
public class Day12Solution extends Solution {

    @Override
    public Integer part1Solution() {
        return lineStream()
                .map(this::processSprings)
                .map(this::getAllArrangements)
                .mapToInt(List::size)
                .sum();
    }

    @Override
    public Integer part2Solution() {
        return null;
    }

    private SpringRow processSprings(String line) {
        int split = line.indexOf(' ');
        SpringStatus[] springStatuses = line.substring(0, split).chars()
                .mapToObj(codePoint -> SpringStatus.fromChar((char) codePoint))
                .toArray(SpringStatus[]::new);
        int[] damagedSprings = Arrays.stream(line.substring(split + 1).split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
        return new SpringRow(springStatuses, damagedSprings);
    }

    private List<Range[]> getAllArrangements(SpringRow row) {
        return getAllArrangements(row, 0, 0);
    }
    
    private List<Range[]> getAllArrangements(SpringRow row, int startIndex, int groupIndex) {
        List<Range[]> arrangements = new ArrayList<>();
        int[] groups = row.damagedSprings;
        boolean lastGroup = groups.length - groupIndex == 1;
        int lastIndex = lastPossibleIndex(row, groupIndex);
        for (int i = startIndex; i < lastIndex; i++) {
            Range range = new Range(i, groups[groupIndex]);
            if (!validateRangePlacement(row, range)) continue;

            List<Range[]> subArrangements = lastGroup 
                    ? null
                    : getAllArrangements(row, i + range.length + 1, groupIndex + 1);

            if (subArrangements == null) {
                arrangements.add(new Range[]{range});
                continue;
            } else if (subArrangements.isEmpty()) continue;

            for (Range[] subArrangement : subArrangements) {
                Range[] arrangement = new Range[groups.length - groupIndex];
                arrangement[0] = range;
                System.arraycopy(subArrangement, 0, arrangement, 1, arrangement.length - 1);
                if (groupIndex == 0 && !validateArrangement(row, arrangement)) continue;
                arrangements.add(arrangement);
            }
        }
        return arrangements;
    }

    private int lastPossibleIndex(SpringRow row, int groupIndex) {
        int sum = 0;
        for (int i = groupIndex; i < row.damagedSprings.length; i++)
            sum += row.damagedSprings[i];
        return row.springs.length - (sum + (row.damagedSprings.length - groupIndex - 2));
    }

    private boolean validateArrangement(SpringRow row, Range[] arrangement) {
        List<Integer> damagedSprings = new ArrayList<>(row.springs.length);
        for (int i = 0; i < row.springs.length; i++)
            if (row.springs[i] == SpringStatus.DAMAGED) damagedSprings.add(i);
        for (Range range : arrangement) {
            for (int i = range.startIndex; i < range.endIndex(); i++)
                damagedSprings.remove((Object) i);
        }
        return damagedSprings.isEmpty();
    }

    private boolean validateRangePlacement(SpringRow row, Range range) {
        SpringStatus[] springs = row.springs;

        if (range.startIndex > 0 && springs[range.startIndex - 1] == SpringStatus.DAMAGED)
            return false;

        if (range.endIndex() < springs.length && springs[range.endIndex()] == SpringStatus.DAMAGED)
            return false;

        for (int i = range.startIndex; i < range.endIndex(); i++)
            if (springs[i] == SpringStatus.OPERATIONAL) return false;

        return true;
    }

    public record SpringRow(SpringStatus[] springs, int[] damagedSprings) {}

    private record Range(int startIndex, int length) {

        private int endIndex() {
            return startIndex + length;
        }

    }

    private enum SpringStatus {

        OPERATIONAL,
        DAMAGED,
        UNKNOWN;

        public static SpringStatus fromChar(char c) {
            return switch (c) {
                case '.' -> OPERATIONAL;
                case '#' -> DAMAGED;
                case '?' -> UNKNOWN;
                default -> throw new IllegalStateException("Unexpected value: " + c);
            };
        }

    }

}
