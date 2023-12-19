package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.HashMap;
import java.util.Map;

@AdventOfCodeSolution(year = 2023, day = 16, name = "The Floor Will Be Lava", link = "https://adventofcode.com/2023/day/16")
public class Day16Solution extends Solution {

    private static final byte UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;

    @Override
    public Integer part1Solution() {
        char[][] contraption = lineStream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
        return shineBeam(contraption, 0, 0, RIGHT);
    }

    @Override
    public Integer part2Solution() {
        char[][] contraption = lineStream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
        int max = 0;
        for (int y = 0; y < contraption.length; y++) {
            max = Math.max(shineBeam(contraption, 0, y, RIGHT), max);
            max = Math.max(shineBeam(contraption, contraption[0].length - 1, y, LEFT), max);
        }
        for (int x = 0; x < contraption[0].length; x++) {
            max = Math.max(shineBeam(contraption, x, 0, DOWN), max);
            max = Math.max(shineBeam(contraption, x, contraption.length - 1, UP), max);
        }
        return max;
    }

    private int shineBeam(char[][] contraption, int x, int y, byte direction) {
        Map<Long, Byte> visitedNodes = new HashMap<>();
        shineBeam(contraption, x, y, direction, visitedNodes);
        return visitedNodes.size();
    }

    private void shineBeam(char[][] contraption, int x, int y, byte direction, Map<Long, Byte> visitedNodes) {
        while (true) {
            if (0 > x || x >= contraption[0].length || 0 > y || y >= contraption.length)
                break;
            long encodedNode = encodeNode((short) x, (short) y);
            byte encodedDirection = (byte) (1 << direction);
            if ((visitedNodes.getOrDefault(encodedNode, (byte) 0) & encodedDirection) == 1)
                break;
            visitedNodes.compute(encodedNode, (k, value) -> value != null ? (byte) (value | encodedDirection) : encodedDirection);
            char c = contraption[y][x];
            switch (c) {
                case '-' -> {
                    if (direction % 2 == 1) break;
                    shineBeam(contraption, x + 1, y, RIGHT, visitedNodes);
                    shineBeam(contraption, x - 1, y, LEFT, visitedNodes);
                    return;
                }
                case '|' -> {
                    if (direction % 2 == 0) break;
                    shineBeam(contraption, x, y - 1, UP, visitedNodes);
                    shineBeam(contraption, x, y + 1, DOWN, visitedNodes);
                    return;
                }
                case '/' -> direction = (byte) mod(direction + (direction % 2 == 0 ? 1 : -1), 4);
                case '\\' -> direction = (byte) mod(direction + (direction % 2 == 1 ? 1 : -1), 4);
            }
            switch (direction) {
                case UP -> y--;
                case RIGHT -> x++;
                case DOWN -> y++;
                case LEFT -> x--;
            }
        }
    }

    private long encodeNode(short x, short y) {
        return (long) x << Short.SIZE | y;
    }

    private int mod(int value, int mod) {
        return (value % mod + mod) % mod;
    }

}
