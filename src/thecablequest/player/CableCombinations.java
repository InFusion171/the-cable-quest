package thecablequest.player;

import thecablequest.gameboard.XMLElementClasses.CableConfig;
import breitband.preset.*;
import java.util.*;

public class CableCombinations {

    public static List<Map<CableConfig, Integer>> getValidCombinations(ImmutableList<CableConfig> cables, int maxValue) {
        List<Map<CableConfig, Integer>> combinations = new ArrayList<>();
        generateCombinations(cables, maxValue, new HashMap<>(), 0, combinations);
        return combinations;
    }

    private static void generateCombinations(ImmutableList<CableConfig> cables, int maxValue, Map<CableConfig, Integer> currentCombination, int currentValue, List<Map<CableConfig, Integer>> combinations) {
        if (currentValue <= maxValue) {
            combinations.add(new HashMap<>(currentCombination));
        }

        for (CableConfig cable : cables) {
            if (currentValue + cable.getValue() <= maxValue) {
                currentCombination.put(cable, currentCombination.getOrDefault(cable, 0) + 1);
                generateCombinations(cables, maxValue, currentCombination, currentValue + cable.getValue(), combinations);
                currentCombination.put(cable, currentCombination.get(cable) - 1); // Backtrack
                if (currentCombination.get(cable) == 0) {
                    currentCombination.remove(cable);
                }
            }
        }
    }

    public static Map<CableConfig, Integer> getRandomValidCombination(ImmutableList<CableConfig> cables, int maxValue) {
        List<Map<CableConfig, Integer>> combinations = getValidCombinations(cables, maxValue);
        Random rand = new Random();
        return combinations.get(rand.nextInt(combinations.size()));
    }

    
    public static Map<CableConfig, Integer> getRandomInvalidCombination(ImmutableList<CableConfig> cables, int maxValue) {
        Random rand = new Random();
        Map<CableConfig, Integer> invalidCombination = new HashMap<>();
        int currentValue = 0;
    
        while (currentValue <= maxValue) {
            CableConfig randomCable = cables.get(rand.nextInt(cables.size()));
            int newValue = currentValue + randomCable.getValue();
            invalidCombination.put(randomCable, invalidCombination.getOrDefault(randomCable, 0) + 1);
            currentValue = newValue;
        }
    
        return invalidCombination;
    }
    
}