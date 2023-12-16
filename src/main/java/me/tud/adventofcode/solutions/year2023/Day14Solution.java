package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@AdventOfCodeSolution(year = 2023, day = 14, name = "Parabolic Reflector Dish", link = "https://adventofcode.com/2023/day/14")
public class Day14Solution extends Solution {

    private static final int ITERATIONS = 1_000_000_000;
    
    @Override
    public Long part1Solution() {
        Universe universe = new Universe(Arrays.stream(getInputLines())
                .map(String::toCharArray)
                .toArray(char[][]::new));
        universe.pushNorth();
        return universe.countLoad();
    }

    @Override
    public Long part2Solution() {
        Universe universe = new Universe(Arrays.stream(getInputLines())
                .map(String::toCharArray)
                .toArray(char[][]::new));
        Map<Integer, Integer> cache = new HashMap<>();
        int remaining = 0;
        for (int i = 1; i <= ITERATIONS; i++) {
            universe.cycle();
            int hashCode = universe.hashCode();
            Integer seen = cache.get(hashCode);
            if (seen != null) {
                remaining = (ITERATIONS - i) % (i - seen);
                break;
            }
            cache.put(hashCode, i);
        }
        for (int i = 0; i < remaining; i++)
            universe.cycle();
        return universe.countLoad();
    }

    private record Universe(char[][] rocks) {

        public void cycle() {
            pushNorth();
            pushWest();
            pushSouth();
            pushEast();
        }

        public void pushNorth() {
            for (int y = 1; y < rocks.length; y++) {
                for (int x = 0; x < rocks[y].length; x++) {
                    push(x, y, 0, -1);
                }
            }
        }

        public void pushWest() {
            for (int y =  0; y < rocks.length; y++) {
                for (int x = 1; x < rocks[y].length; x++) {
                    push(x, y, -1, 0);
                }
            }
        }

        public void pushSouth() {
            for (int y = rocks.length - 2; y >= 0; y--) {
                for (int x = 0; x < rocks[y].length; x++) {
                    push(x, y, 0, 1);
                }
            }
        }

        public void pushEast() {
            for (int y =  0; y < rocks.length; y++) {
                for (int x = rocks[y].length - 2; x >= 0; x--) {
                    push(x, y, 1, 0);
                }
            }
        }

        private void push(int x, int y, int dx, int dy) {
            if (rocks[y][x] != 'O') return;
            rocks[y][x] = '.';
            while (0 <= y + dy && y + dy < rocks.length && 0 <= x + dx && x + dx < rocks[y].length && rocks[y + dy][x + dx] == '.') {
                x += dx;
                y += dy;
            }
            rocks[y][x] = 'O';
        }

        public long countLoad() {
            long load = 0;
            for (int y = 0; y < rocks.length; y++) {
                for (int x = 0; x < rocks[y].length; x++) {
                    if (rocks[y][x] != 'O') continue;
                    load += rocks.length - y;
                }
            }
            return load;
        }

        @Override
        public int hashCode() {
            StringBuilder builder = new StringBuilder();
            for (char[] rock : rocks)
                builder.append(rock).append('\n');
            return builder.toString().hashCode();
        }

    }

}
