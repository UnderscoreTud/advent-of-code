package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AdventOfCodeSolution(year = 2023, day = 11, name = "Cosmic Expansion", link = "https://adventofcode.com/2023/day/11")
public class Day11Solution extends Solution {

    @Override
    public Long part1Solution() {
        return parseSpace(getInputLines(), 2).sumOfAllShortestPaths();
    }

    @Override
    public Long part2Solution() {
        return parseSpace(getInputLines(), 1_000_000).sumOfAllShortestPaths();
    }

    private Space parseSpace(String[] data, int expansionRate) {
        int columns = data[0].length(), rows = data.length;
        boolean[] emptyColumns = new boolean[columns];
        int[] xOffsets = new int[columns], yOffsets = new int[rows];
        Set<Pos> galaxies = new HashSet<>();
        Arrays.fill(emptyColumns, true);
        for (int row = 0; row < rows; row++) {
            boolean rowEmpty = true;
            for (int column = 0; column < columns; column++) {
                if (data[row].charAt(column) == '.') continue;
                emptyColumns[column] = false;
                rowEmpty = false;
                galaxies.add(new Pos(column, row));
            }
            if (!rowEmpty) continue;
            for (int j = row; j < yOffsets.length; j++)
                yOffsets[j] += expansionRate - 1;
        }
        for (int i = 0; i < emptyColumns.length; i++) {
            if (!emptyColumns[i]) continue;
            for (int j = i; j < xOffsets.length; j++)
                xOffsets[j] += expansionRate - 1;
        }
        columns += xOffsets[xOffsets.length - 1];
        rows += yOffsets[yOffsets.length - 1];
        return new Space(columns, rows, galaxies.stream()
                .map(pos -> new Pos(pos.x + xOffsets[(int) pos.x], pos.y + yOffsets[(int) pos.y]))
                .toList());
    }

    private record Space(long columns, long rows, List<Pos> galaxies) {

        public long sumOfAllShortestPaths() {
            long sum = 0;
            for (int i = 0; i < galaxies.size() - 1; i++) {
                Pos galaxy1 = galaxies.get(i);
                for (int j = i + 1; j < galaxies.size(); j++) {
                    Pos galaxy2 = galaxies.get(j);
                    sum = Math.addExact(sum, Math.abs(galaxy1.x - galaxy2.x) + Math.abs(galaxy1.y - galaxy2.y));
                }
            }
            return sum;
        }

    }

    private record Pos(long x, long y) {

        public Pos offset(long x, long y) {
            return new Pos(Math.addExact(this.x, x), Math.addExact(this.y, y));
        }

    }

}
