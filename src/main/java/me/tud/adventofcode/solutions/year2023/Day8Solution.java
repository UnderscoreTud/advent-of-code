package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AdventOfCodeSolution(year = 2023, day = 8, name = "Haunted Wasteland", link = "https://adventofcode.com/2023/day/8")
public class Day8Solution extends Solution {

    private static final Pattern NODE_PATTERN = Pattern.compile("(.{3}) = \\((.{3}), (.{3})\\)");
    
    @Override
    public Long part1Solution() {
        return solution(node -> node.equals("AAA"));
    }

    @Override
    public Long part2Solution() {
        return solution(node -> node.charAt(node.length() - 1) == 'A');
    }

    private long solution(Predicate<String> startingNode) {
        Iterator<String> lineIterator = lineStream().iterator();
        char[] instructions = lineIterator.next().toCharArray();
        lineIterator.next();
        short[][] nodeLookupTable = new short[Short.MAX_VALUE][2];
        List<Short> currentNodes = new ArrayList<>();
        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            String node = line.substring(0, 3);
            short encodedNode = encodeToShort(node);
            nodeLookupTable[encodedNode] = new short[]{
                    encodeToShort(line.substring(7, 10)),
                    encodeToShort(line.substring(12, 15))
            };
            if (startingNode.test(node)) currentNodes.add(encodedNode);
        }
        long[] nodeSteps = new long[currentNodes.size()];
        ListIterator<Short> iterator = currentNodes.listIterator();
        outer:
        while (iterator.hasNext()) {
            short node = iterator.next();
            int steps = 0;
            while (true) {
                for (char instruction : instructions) {
                    node = nodeLookupTable[node][instruction == 'L' ? 0 : 1];
                    steps++;
                    if (getLastChar(node) != 'Z') continue;
                    nodeSteps[iterator.previousIndex()] = steps;
                    continue outer;
                }
            }
        }
        return nodeSteps.length == 1 ? nodeSteps[0] : lcm(nodeSteps);
    }
    
    private static short encodeToShort(String node) {
        return (short) ((node.charAt(0) - 'A') << 10 | (node.charAt(1) - 'A') << 5 | (node.charAt(2) - 'A'));
    }
    
    private static char getLastChar(short encodedNode) {
        return (char) ((encodedNode & (1 << 5) - 1) + 'A');
    }
    
    private static long lcm(long[] array) {
        long lcm = array[0];
        for (long num : array) {
            long gcd = gcd(lcm, num);
            lcm = (lcm * num) / gcd;
        }
        return lcm;
    }

    private static long gcd(long num1, long num2) {
        return num2 == 0 ? num1 : gcd(num2, num1 % num2);
    }
    
}
