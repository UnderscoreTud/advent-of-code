package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@AdventOfCodeSolution(year = 2023, day = 21, name = "Step Counter", link = "https://adventofcode.com/2023/day/21")
public class Day21Solution extends Solution {

    private static final int[][] OFFSETS = {
            {0, -1}, // UP
            {1, 0}, // RIGHT
            {0, 1}, // DOWN
            {-1, 0}, // LEFT
    };

    @Override
    public Integer part1Solution() {
        int[] startingPos = {-1, -1};
        char[][] grid = lineStream()
                .peek(line -> {
                    if (startingPos[0] == -1) startingPos[1]++;
                    else return;
                    int x = line.indexOf('S');
                    if (x != -1) startingPos[0] = x;
                })
                .map(String::toCharArray)
                .toArray(char[][]::new);
        Set<Pos> crops = new HashSet<>();
        crops.add(new Pos(startingPos[0], startingPos[1]));
        for (int i = 0; i < 64; i++) {
            for (Pos crop : Set.copyOf(crops)) {
                crops.remove(crop);
                for (int[] offset : OFFSETS) {
                    Pos newCrop = crop.offset(offset);
                    if (0 > newCrop.x || newCrop.x >= grid[0].length || 0 > newCrop.y || newCrop.y >= grid.length)
                        continue;
                    if (grid[newCrop.y][newCrop.x] == '#') continue;
                    crops.add(newCrop);
                }
            }
        }
        return crops.size();
    }

    @Override
    public Integer part2Solution() {
        return 0;
    }

    private record Pos(int x, int y) {

        private Pos offset(int[] offset) {
            return new Pos(x + offset[0], y + offset[1]);
        }

    }
    
}
