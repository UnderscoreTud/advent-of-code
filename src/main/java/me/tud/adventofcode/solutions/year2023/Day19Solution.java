package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.*;

@AdventOfCodeSolution(year = 2023, day = 19, name = "", link = "https://adventofcode.com/2023/day/19")
public class Day19Solution extends Solution {

    @Override
    public Object part1Solution() {
        Iterator<String> iterator = lineStream().iterator();
        Map<String, Workflow> workflowMap = parseWorkflows(iterator);
        List<int[]> ratings = parseRatings(iterator);
        int sum = 0;
        for (int[] rating : ratings) {
            String dest = "in";

            do {
                Workflow workflow = workflowMap.get(dest);
                dest = workflow.apply(rating);
            } while (!dest.equals("A") && !dest.equals("R"));

            if (dest.equals("R")) continue;
            sum += Arrays.stream(rating).sum();
        }
        return sum;
    }

    @Override
    public Object part2Solution() {
        Iterator<String> iterator = lineStream().iterator();
        Map<String, Workflow> workflowMap = parseWorkflows(iterator);
        return combinations(new int[][]{
                {1, 4000},
                {1, 4000},
                {1, 4000},
                {1, 4000},
        }, workflowMap, "in");
    }

    private long combinations(int[][] ratings, Map<String, Workflow> workflowMap, String workflowName) {
        if (ratings == null) return 0;

        if (workflowName.equals("A")) return Arrays.stream(ratings)
                .mapToLong(ints -> ints[1] - ints[0] + 1)
                .reduce(1, (a, b) -> a * b);
        else if (workflowName.equals("R")) return 0;

        Workflow workflow = workflowMap.get(workflowName);
        long combinations = 0;
        for (Rule rule : workflow.rules)
            combinations += combinations(rule.split(ratings), workflowMap, rule.dest);

        combinations += combinations(ratings, workflowMap, workflow.otherwise);
        return combinations;
    }

    private Map<String, Workflow> parseWorkflows(Iterator<String> iterator) {
        Map<String, Workflow> workflows = new HashMap<>();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.isEmpty()) break;
            Workflow workflow = parseWorkflow(line);
            workflows.put(workflow.name, workflow);
        }
        return workflows;
    }

    private Workflow parseWorkflow(String line) {
        String name = line.substring(0, line.indexOf('{'));
        List<Rule> rules = Arrays.stream(line.substring(line.indexOf('{') + 1).split(","))
                .map(data -> {
                    if (data.indexOf(':') == -1) return null;
                    int which = switch (data.charAt(0)) {
                        case 'x' -> 0;
                        case 'm' -> 1;
                        case 'a' -> 2;
                        case 's' -> 3;
                        default -> throw new IllegalStateException("Unexpected value: " + data.charAt(0));
                    };
                    boolean lessThan = data.charAt(1) == '<';
                    int amount = Integer.parseInt(data.substring(2, data.indexOf(':')));
                    String dest = data.substring(data.indexOf(':') + 1);
                    return new Rule(which, lessThan, amount, dest);
                })
                .filter(Objects::nonNull)
                .toList();
        return new Workflow(name, rules, line.substring(line.lastIndexOf(',') + 1, line.length() - 1));
    }

    private List<int[]> parseRatings(Iterator<String> iterator) {
        List<int[]> ratingsList = new ArrayList<>();
        while (iterator.hasNext()) {
            String line = iterator.next();
            ratingsList.add(Arrays.stream(line.substring(1, line.length() - 1).split(","))
                    .mapToInt(data -> Integer.parseInt(data.substring(2)))
                    .toArray());
        }
        return ratingsList;
    }

    private record Rule(int which, boolean lessThan, int value, String dest) {

        public boolean test(int[] ratings) {
            return test(ratings[which]);
        }

        private boolean test(int x) {
            return lessThan ? x < value : x > value;
        }

        public int[][] split(int[][] ratings) {
            if (!test(ratings[which][lessThan ? 0 : 1])) return null;
            int[][] clone = new int[4][2];
            for (int i = 0; i < clone.length; i++) clone[i] = ratings[i].clone();
            if (lessThan) {
                clone[which][1] = Math.min(ratings[which][1], value - 1);
                ratings[which][0] = Math.max(value, ratings[which][0]);
                return clone;
            }
            clone[which][0] = Math.max(value + 1, ratings[which][0]);
            ratings[which][1] = Math.min(ratings[which][1], value);
            return clone;
        }

    }

    private record Workflow(String name, List<Rule> rules, String otherwise) {

        public String apply(int[] ratings) {
            for (Rule rule : rules) {
                if (!rule.test(ratings)) continue;
                return rule.dest;
            }
            return otherwise;
        }

    }
    
}
