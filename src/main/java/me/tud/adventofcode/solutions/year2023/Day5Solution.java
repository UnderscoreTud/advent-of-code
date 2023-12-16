package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@AdventOfCodeSolution(year = 2023, day = 5, name = "If You Give A Seed A Fertilizer", link = "https://adventofcode.com/2023/day/5")
public class Day5Solution extends Solution {

    @Override
    public Object part1Solution() {
        Iterator<String> lineIterator = lineStream().iterator();
        long[] seeds = Arrays.stream(lineIterator.next().split(" "))
                .skip(1) // skip the 'seeds:' part
                .map(String::trim)
                .mapToLong(Long::parseLong)
                .toArray();
        List<List<Processor>> maps = parseMaps(lineIterator);

        long lowestLocation = Long.MAX_VALUE;
        for (long seed : seeds) {
            long value = seed;
            for (List<Processor> processors : maps) {
                for (Processor processor : processors) {
                    if (processor.sourceStart > value || value >= processor.sourceStart + processor.length) continue;
                    value += processor.offset;
                    break;
                }
            }
            lowestLocation = Math.min(lowestLocation, value);
        }
        return lowestLocation;
    }

    @Override
    public Object part2Solution() {
        Iterator<String> lineIterator = lineStream().iterator();
        long[] seeds = Arrays.stream(lineIterator.next().split(" "))
                .skip(1) // skip the 'seeds:' part
                .map(String::trim)
                .mapToLong(Long::parseLong)
                .toArray();
        Range[] seedRanges = new Range[seeds.length / 2];
        for (int i = 0; i < seedRanges.length; i++) {
            long start = seeds[i * 2], length = seeds[i * 2 + 1];
            seedRanges[i] = new Range(start, start + length);
        }
        List<List<Processor>> maps = parseMaps(lineIterator);

        long lowestLocation = Long.MAX_VALUE;
        for (Range seedRange : seedRanges) {
            for (Range processedRange : processSeed(seedRange, maps))
                lowestLocation = Math.min(lowestLocation, processedRange.start);
        }
        return lowestLocation;
    }

    @NotNull
    private static List<Range> processSeed(Range seedRange, List<List<Processor>> maps) {
        List<Range> ranges = new LinkedList<>();
        ranges.add(seedRange);
        for (List<Processor> processors : maps) {
            List<Range> processedRanges = new LinkedList<>();
            for (Processor processor : processors) {
                if (ranges.isEmpty()) break;
                for (Range range : List.copyOf(ranges)) {
                    long start = Math.max(processor.sourceStart, range.start);
                    long end = Math.min(processor.sourceStart + processor.length, range.end);
                    if (end <= start) continue;
                    ranges.remove(range);
                    processedRanges.add(new Range(start + processor.offset, end + processor.offset));
                    if (range.start < start) ranges.add(new Range(range.start, start));
                    if (range.end > end) ranges.add(new Range(end, range.end));
                }
            }
            ranges.addAll(processedRanges);
        }
        return ranges;
    }

    private List<List<Processor>> parseMaps(Iterator<String> lineIterator) {
        List<List<Processor>> maps = new LinkedList<>();
        List<Processor> currentProcessors = new LinkedList<>();
        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            if (line.isEmpty()) {
                lineIterator.next();
                currentProcessors = new LinkedList<>();
                maps.add(currentProcessors);
                continue;
            }
            long[] processorValues = Arrays.stream(line.split(" "))
                    .mapToLong(Long::parseLong)
                    .toArray();
            currentProcessors.add(new Processor(
                    processorValues[1],
                    processorValues[2],
                    processorValues[0] - processorValues[1]
            ));
        }
        return maps;
    }

    private record Processor(long sourceStart, long length, long offset) {}

    private record Range(long start, long end) {}

}
