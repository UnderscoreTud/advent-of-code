package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

@AdventOfCodeSolution(year = 2023, day = 17, name = "Clumsy Crucible", link = "https://adventofcode.com/2023/day/17")
public class Day17Solution extends Solution {

    @Override
    public Object part1Solution() {
        return djikstra(parseGrid(), 0, 3);
    }

    @Override
    public Object part2Solution() {
        return djikstra(parseGrid(), 3, 10);
    }

    private int djikstra(int[][] grid, int min, int max) {
        Queue<Item> queue = new PriorityQueue<>();
        Set<Node> visited = new HashSet<>();
        int[] endPoint = {grid[0].length - 1, grid.length - 1};

        queue.add(new Item(new Node(1, 0, Direction.RIGHT, 0), grid));
        queue.add(new Item(new Node(0, 1, Direction.DOWN, 0), grid));
        while (!queue.isEmpty()) {
            Item current = queue.poll();
            if (!visited.add(current.node)) continue;
            if (current.node.x == endPoint[0]
                    && current.node.y == endPoint[1]
                    && current.node.distInDir >= min) return current.heatLoss;
            for (Direction direction : Direction.values()) {
                Item next = current.move(grid, direction, min, max);
                if (next != null) queue.add(next);
            }
        }
        return 0;
    }

    private int[][] parseGrid() {
        return lineStream()
                .map(String::chars)
                .map(charStream -> charStream.map(c -> c - '0').toArray())
                .toArray(int[][]::new);
    }

    private enum Direction {

        UP(0, -1),
        RIGHT(1, 0),
        DOWN(0, 1),
        LEFT(-1, 0);

        private final int[] offset;

        Direction(int dx, int dy) {
            this.offset = new int[]{dx, dy};
        }

        public boolean isOpposite(Direction other) {
            return Math.abs(ordinal() - other.ordinal()) == 2;
        }

    }

    private record Node(int x, int y, Direction direction, int distInDir) implements Comparable<Node> {

        @Override
        public int compareTo(@NotNull Node other) {
            if (direction == other.direction && distInDir != other.distInDir)
                return Integer.compare(distInDir, other.distInDir);
            return y != other.y ? Integer.compare(y, other.y) : Integer.compare(x, other.x);
        }

    }

    private record Item(Node node, int heatLoss) implements Comparable<Item> {

        public Item(Node node, int[][] grid) {
            this(node, grid[node.y][node.x]);
        }

        public @Nullable Item move(int[][] grid, Direction direction, int min, int max) {
            if (node.direction.isOpposite(direction)) return null;
            if (node.distInDir < min && node.direction != direction) return null;
            int newDistance = node.direction == direction ? node.distInDir + 1 : 0;
            if (newDistance >= max) return null;
            Node newNode = new Node(node.x + direction.offset[0], node.y + direction.offset[1], direction, newDistance);
            if (0 > newNode.x || newNode.x >= grid[0].length || 0 > newNode.y || newNode.y >= grid.length) return null;
            return new Item(newNode, heatLoss + grid[newNode.y][newNode.x]);
        }

        @Override
        public int compareTo(@NotNull Item other) {
            return heatLoss != other.heatLoss ? Integer.compare(heatLoss, other.heatLoss) : node.compareTo(other.node);
        }

    }

}
