package me.tud.adventofcode.solutions.year2023;

import me.tud.adventofcode.AdventOfCodeSolution;
import me.tud.adventofcode.Solution;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AdventOfCodeSolution(year = 2023, day = 7, name = "Camel Cards", link = "https://adventofcode.com/2023/day/7")
public class Day7Solution extends Solution {

    @Override
    public Integer part1Solution() {
        return calculateGameBids(parseHands(false));
    }

    @Override
    public Integer part2Solution() {
        return calculateGameBids(parseHands(true));
    }

    private List<Hand> parseHands(boolean usingJokers) {
        return lineStream()
                .map(line -> {
                    int[] cards = new int[5];
                    for (int i = 0; i < cards.length; i++)
                        cards[i] = getCardValue(line.charAt(i), usingJokers);
                    int bid = Integer.parseInt(line.substring(line.indexOf(' ') + 1));
                    return new Hand(cards, bid);
                })
                .sorted()
                .toList();
    }
    
    private int calculateGameBids(List<Hand> hands) {
        int sum = 0;
        for (int i = 0; i < hands.size(); i++)
            sum += hands.get(i).bid * (i + 1);
        return sum;
    }
    
    private int getCardValue(char card, boolean usingJokers) {
        return switch (card) {
            case 'A' -> 14;
            case 'K' -> 13;
            case 'Q' -> 12;
            case 'J' -> usingJokers ? 0 : 11;
            case 'T' -> 10;
            default -> card - '0';
        };
    }

    private record Hand(int type, int[] cards, int bid) implements Comparable<Hand> {

        public Hand(int[] cards, int bid) {
            this(getType(cards), cards, bid);
        }

        @Override
        public int compareTo(@NotNull Hand other) {
            if (type != other.type) return Integer.compare(type, other.type);
            for (int i = 0; i < cards.length; i++) {
                int result = Integer.compare(cards[i], other.cards[i]);
                if (result != 0) return result;
            }
            return 0;
        }

        private static int getType(int[] cards) {
            Map<Integer, Integer> cardAppearances = new HashMap<>();
            int jokers = 0;
            for (int card : cards) {
                if (card == 0) {
                    jokers++;
                    continue;
                }
                cardAppearances.compute(card, (k, appearances) -> appearances != null ? ++appearances : 1);
            }

            int distinctCards = cardAppearances.size();
            if (distinctCards <= 1)
                return 7;

            if (distinctCards == 2)
                return cardAppearances.containsValue(1) ? 6 : 5;

            if (distinctCards == 3)
                return jokers != 0 || cardAppearances.containsValue(3) ? 4 : 3;

            return distinctCards == 4 ? 2 : 1;
        }

    }

}
