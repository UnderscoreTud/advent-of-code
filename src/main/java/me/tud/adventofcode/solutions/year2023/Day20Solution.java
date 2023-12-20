package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@AdventOfCodeSolution(year = 2023, day = 20, name = "Pulse Propagation", link = "https://adventofcode.com/2023/day/20")
public class Day20Solution extends Solution {

    @Override
    public Long part1Solution() {
        long lowPulses = 0, highPulses = 0;
        Map<String, Module> modules = parseModules();
        Queue<Pulse> queue = new ArrayDeque<>();
        Module broadcaster = Objects.requireNonNull(modules.get("broadcaster"));
        for (int i = 0; i < 1000; i++) {
            queue.add(new Pulse(null, broadcaster, false));
            while (!queue.isEmpty()) {
                Pulse pulse = queue.poll();
                if (pulse.high) highPulses++;
                else lowPulses++;
                if (!pulse.receiver.prepare(pulse)) continue;
                pulse.receiver.destinationModules.stream()
                        .map(modules::get)
                        .forEach(module -> queue.add(pulse.receiver.receive(pulse, module)));
            }
        }
        return lowPulses * highPulses;
    }

    @Override
    public Long part2Solution() {
        Map<String, Module> modules = parseModules();
        Module rxSender = modules.values().stream()
                .filter(module -> module.destinationModules.contains("rx"))
                .findFirst().orElse(null);
        if (rxSender == null) return -1L;

        Map<String, Integer> counts = new HashMap<>();
        AtomicInteger presses = new AtomicInteger();
        long inputs = modules.values().stream()
                .filter(ConjunctionModule.class::isInstance)
                .map(ConjunctionModule.class::cast)
                .filter(module -> module.destinationModules.contains(rxSender.name))
                .peek(module -> module.onTrigger(() -> counts.putIfAbsent(module.name, presses.get())))
                .count();

        Queue<Pulse> queue = new ArrayDeque<>();
        Module broadcaster = Objects.requireNonNull(modules.get("broadcaster"));
        do {
            presses.incrementAndGet();
            queue.add(new Pulse(null, broadcaster, false));
            while (!queue.isEmpty()) {
                Pulse pulse = queue.poll();
                if (!pulse.receiver.prepare(pulse)) continue;
                pulse.receiver.destinationModules.stream()
                        .map(modules::get)
                        .forEach(module -> queue.add(pulse.receiver.receive(pulse, module)));
            }
        } while (counts.size() != inputs);
        return lcm(counts.values().stream().mapToLong(Integer::longValue).toArray());
    }

    private Map<String, Module> parseModules() {
        Map<String, Module> modules = new HashMap<>();
        lineStream()
                .map(this::parseModule)
                .forEach(module -> modules.put(module.name, module));
        List.copyOf(modules.values()).forEach(module -> module.destinationModules.stream()
                .map(destination -> modules.compute(destination, (k, v) -> v != null ? v : new OutputModule(k)))
                .filter(ConjunctionModule.class::isInstance)
                .map(ConjunctionModule.class::cast)
                .forEach(destination -> destination.addInput(module)));
        return modules;
    }

    private Module parseModule(String line) {
        char prefix = line.charAt(0);
        String name = line.substring(prefix == 'b' ? 0 : 1, line.indexOf(" "));
        List<String> destination = List.of(line.substring(line.indexOf('>') + 2).split(", "));
        return switch (prefix) {
            case 'b' -> new BroadcasterModule(name, destination);
            case '%' -> new FlipFlopModule(name, destination);
            case '&' -> new ConjunctionModule(name, destination);
            default -> throw new IllegalStateException("Unexpected value: " + prefix);
        };
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

    private record Pulse(Module sender, Module receiver, boolean high) {

        @Override
        public String toString() {
            return (sender != null ? sender : "button") + " -" + (high ? "high" : "low") + "-> " + receiver;
        }

    }

    private static abstract class Module {

        final String name;
        protected final List<String> destinationModules;

        private Module(String name, List<String> destinationModules) {
            this.name = name;
            this.destinationModules = destinationModules;
        }

        public boolean prepare(Pulse pulse) {
            return true;
        }

        protected abstract Pulse receive(Pulse pulse, Module destination);

        @Override
        public String toString() {
            return name;
        }

    }

    private static class BroadcasterModule extends Module {

        private BroadcasterModule(String name, List<String> destinationModules) {
            super(name, destinationModules);
        }

        @Override
        protected Pulse receive(Pulse pulse, Module destination) {
            return new Pulse(this, destination, pulse.high);
        }

    }

    private static class FlipFlopModule extends Module {

        private boolean state = false;

        private FlipFlopModule(String name, List<String> destinationModules) {
            super(name, destinationModules);
        }

        @Override
        public boolean prepare(Pulse pulse) {
            if (pulse.high) return false;
            state = !state;
            return true;
        }

        @Override
        protected Pulse receive(Pulse pulse, Module destination) {
            return new Pulse(this, destination, state);
        }

    }

    private static class ConjunctionModule extends Module {

        private final Map<Module, Boolean> memory = new HashMap<>();
        private final Set<Module> inputs = new HashSet<>();
        private Runnable onTrigger = null;

        private ConjunctionModule(String name, List<String> destinationModules) {
            super(name, destinationModules);
        }

        @Override
        protected Pulse receive(Pulse pulse, Module destination) {
            memory.put(pulse.sender, pulse.high);
            boolean newPulse = !pulse.high || memory.values().stream().filter(bool -> bool).count() < inputs.size();
            if (onTrigger != null && newPulse)
                onTrigger.run();
            return new Pulse(this, destination, newPulse);
        }

        public void addInput(Module input) {
            inputs.add(input);
        }

        public void onTrigger(Runnable runnable) {
            this.onTrigger = runnable;
        }
        
    }

    private static class OutputModule extends Module {

        private OutputModule(String name) {
            super(name, Collections.emptyList());
        }

        @Override
        public boolean prepare(Pulse pulse) {
            return false;
        }

        @Override
        protected Pulse receive(Pulse pulse, Module destination) {
            throw new UnsupportedOperationException();
        }

    }

}
