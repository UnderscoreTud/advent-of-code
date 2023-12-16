package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@AdventOfCodeSolution(year = 2023, day = 10, name = "Pipe Maze", link = "https://adventofcode.com/2023/day/10")
public class Day10Solution extends Solution {

    @Override
    public Object part1Solution() {
        Maze maze = parseMaze();
        return maze.getLargestLoop().perimeter() / 2;
    }

    @Override
    public Object part2Solution() {
        Maze maze = parseMaze();
        return maze.getLargestLoop().area();
    }

    private Maze parseMaze() {
        char[][] charGrid = lineStream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
        Pos startingPoint = null;
        Pipe[][] pipeGrid = new Pipe[charGrid.length][charGrid[0].length];
        for (int y = 0; y < pipeGrid.length; y++) {
            for (int x = 0; x < pipeGrid[y].length; x++) {
                if (startingPoint == null && charGrid[y][x] == 'S') startingPoint = new Pos(x, y);
                pipeGrid[y][x] = Pipe.fromChar(charGrid[y][x]);
            }
        }
        return new Maze(startingPoint, pipeGrid);
    }

    private record Maze(Pos startingPoint, Pipe[][] pipeGrid) {

        public Pipe getPipe(Pos position) {
            return getPipe(position.x, position.y);
        }

        public Pipe getPipe(int x, int y) {
            if (0 > y || 0 > x || y >= pipeGrid.length || x >= pipeGrid[y].length)
                return null;
            return pipeGrid[y][x];
        }

        public Loop getLargestLoop() {
            Loop largestLoop = null;
            for (Pipe pipe : Pipe.values()) {
                Loop loop = getLoop(pipe);
                if (loop == null) continue;
                if (largestLoop == null || largestLoop.perimeter() < loop.perimeter()) largestLoop = loop;
            }
            return largestLoop;
        }

        private Loop getLoop(Pipe startingPipe) {
            Set<Pos> positions = new LinkedHashSet<>();
            Pipe currentPipe = startingPipe;
            Pos currentPosition = startingPoint;
            for (Direction direction : startingPipe.directions) {
                Pipe adjacent = getPipe(currentPosition.offset(direction));
                if (!startingPipe.connectsTo(adjacent, direction)) return null;
            }
            Direction lastDirection = null;
            positions.add(currentPosition);
            while (true) {
                Direction direction = currentPipe.directions[0];
                if (direction.opposite() == lastDirection) direction = currentPipe.directions[1];
                lastDirection = direction;
                currentPosition = currentPosition.offset(direction);
                if (positions.contains(currentPosition)) break;
                Pipe adjacentPipe = getPipe(currentPosition);
                if (!currentPipe.connectsTo(adjacentPipe, direction))
                    return null;
                currentPipe = adjacentPipe;
                positions.add(currentPosition);
            }
            return new Loop(this, positions, startingPipe);
        }

    }

    private enum Pipe {

        NORTH_SOUTH('|', false, Direction.UP, Direction.DOWN),
        EAST_WEST('-', false, Direction.LEFT, Direction.RIGHT),
        NORTH_EAST('L', true, Direction.UP, Direction.RIGHT),
        NORTH_WEST('J', true, Direction.UP, Direction.LEFT),
        SOUTH_WEST('7', true, Direction.DOWN, Direction.LEFT),
        SOUTH_EAST('F', true, Direction.DOWN, Direction.RIGHT);

        private final char pipeChar;
        private final boolean corner;
        private final Direction[] directions;

        Pipe(char pipeChar, boolean corner, Direction... directions) {
            this.pipeChar = pipeChar;
            this.corner = corner;
            this.directions = directions;
        }

        public boolean connectsTo(Pipe other, Direction direction) {
            if (other == null || direction == null) return false;
            for (Direction d : directions) {
                if (d != direction) continue;
                Direction opposite = direction.opposite();
                for (Direction d2 : other.directions)
                    if (d2 == opposite) return true;
            }
            return false;
        }

        public static Pipe fromChar(char pipeChar) {
            for (Pipe value : values())
                if (value.pipeChar == pipeChar) return value;
            return null;
        }

    }

    private enum Direction {

        UP(0, -1),
        LEFT(-1, 0),
        DOWN(0, 1),
        RIGHT(1, 0);

        private final Pos offset;

        Direction(int dx, int dy) {
            this.offset = new Pos(dx, dy);
        }

        public Direction opposite() {
            return switch (this) {
                case UP -> DOWN;
                case LEFT -> RIGHT;
                case DOWN -> UP;
                case RIGHT -> LEFT;
            };
        }
        
    }

    private record Pos(int x, int y) {

        public Pos offset(Direction direction) {
            return offset(direction.offset);
        }

        public Pos offset(Pos pos) {
            return new Pos(x + pos.x, y + pos.y);
        }

    }

    private record Loop(Maze maze, Set<Pos> points, Pipe startingPipe) {

        public int area() {
            List<Pos> vertices = new ArrayList<>();
            points.forEach(pos -> {
                Pipe pipe = maze.getPipe(pos);
                if (pipe == null) pipe = startingPipe;
                if (pipe.corner) vertices.add(pos);
            });
            vertices.add(vertices.get(0));
            int sum1 = 0, sum2 = 0;
            for (int i = 0; i < vertices.size() - 1; i++) {
                sum1 += vertices.get(i).x * vertices.get(i + 1).y;
                sum2 += vertices.get(i).y * vertices.get(i + 1).x;
            }
            return (Math.abs(sum2 - sum1) - perimeter()) / 2 + 1;
        }

        public int perimeter() {
            return points.size();
        }

    }

}
