package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@AdventOfCodeSolution(year = 2023, day = 18, name = "Lavaduct Lagoon", link = "https://adventofcode.com/2023/day/18")
public class Day18Solution extends Solution {

    @Override
    public Long part1Solution() {
        Iterator<String> lineIterator = lineStream().iterator();
        List<long[]> vertices = new ArrayList<>();
        long x = 0, y = 0, perimeter = 0;
        vertices.add(new long[]{x, y});
        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            long steps = Integer.parseInt(line.substring(2, line.indexOf(' ', 3)));
            perimeter += steps;
            switch (line.charAt(0)) {
                case 'R' -> x += steps;
                case 'D' -> y -= steps;
                case 'L' -> x -= steps;
                case 'U' -> y += steps;
            }
            vertices.add(new long[]{x, y});
        }

        return area(vertices) + perimeter / 2 + 1;
    }

    @Override
    public Long part2Solution() {
        Iterator<String> lineIterator = lineStream().iterator();
        List<long[]> vertices = new ArrayList<>();
        long x = 0, y = 0, perimeter = 0;
        vertices.add(new long[]{x, y});
        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            String hex = line.substring(line.indexOf('(') + 2, line.length() - 2);
            long steps = Integer.parseInt(hex, 16);
            perimeter += steps;
            switch (line.charAt(line.length() - 2)) {
                case '0' -> x += steps;
                case '1' -> y -= steps;
                case '2' -> x -= steps;
                case '3' -> y += steps;
            }
            vertices.add(new long[]{x, y});
        }

        return area(vertices) + perimeter / 2 + 1;
    }

    private long area(List<long[]> vertices) {
        long area = 0;
        for (int i = 0; i < vertices.size() - 1; i++)
            area += vertices.get(i)[0] * vertices.get(i + 1)[1] - vertices.get(i)[1] * vertices.get(i + 1)[0];
        return Math.abs(area) / 2;
    }

}
