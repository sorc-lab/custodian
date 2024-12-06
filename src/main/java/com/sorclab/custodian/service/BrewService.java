package com.sorclab.custodian.service;

import com.sorclab.custodian.config.BrewConfig;
import com.sorclab.custodian.config.BrewType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;

// TODO: Logic is a mess. Finish prototype, write tests, then refactor.
// TODO: Figure out how to update current_stock.txt at runtime. Requires reboot to pickup changes.

@Service
@RequiredArgsConstructor
public class BrewService {
    private static final String CURRENT_STOCK_FILE = "current_stock.txt";
    private static final String IGNORE_STOCK_ENTRY = "q to quit";
    private static final String KEYWORD_MANY = "Many";

    private final BrewConfig brewConfig;

    public BrewConfig brewOrder() {
        return BrewConfig.builder()
                .orange(calcDesiredAmount(desiredStock().getOrange(), currentStock().getOrange()))
                .yellow(calcDesiredAmount(desiredStock().getYellow(), currentStock().getYellow()))
                .pink(calcDesiredAmount(desiredStock().getPink(), currentStock().getPink()))
                .violet(calcDesiredAmount(desiredStock().getViolet(), currentStock().getViolet()))
                .magenta(calcDesiredAmount(desiredStock().getMagenta(), currentStock().getMagenta()))
                .fire(calcDesiredAmount(desiredStock().getFire(), currentStock().getFire()))
                .cold(calcDesiredAmount(desiredStock().getCold(), currentStock().getCold()))
                .acid(calcDesiredAmount(desiredStock().getAcid(), currentStock().getAcid()))
                .magic(calcDesiredAmount(desiredStock().getMagic(), currentStock().getMagic()))
                .psionic(calcDesiredAmount(desiredStock().getPsionic(), currentStock().getPsionic()))
                .poison(calcDesiredAmount(desiredStock().getPoison(), currentStock().getPoison()))
                .sharp(calcDesiredAmount(desiredStock().getSharp(), currentStock().getSharp()))
                .blunt(calcDesiredAmount(desiredStock().getBlunt(), currentStock().getBlunt()))
                .pierce(calcDesiredAmount(desiredStock().getPierce(), currentStock().getPierce()))
                .electricity(calcDesiredAmount(desiredStock().getElectricity(), currentStock().getElectricity()))
                .mana(calcDesiredAmount(desiredStock().getMana(), currentStock().getMana()))
                .cyan(calcDesiredAmount(desiredStock().getCyan(), currentStock().getCyan()))
                .restoreWater(calcDesiredAmount(desiredStock().getRestoreWater(), currentStock().getRestoreWater()))
                .cureWater(calcDesiredAmount(desiredStock().getCureWater(), currentStock().getCureWater()))
                .build();
    }

    // TODO: Just inline this calc. Do not need a function for it.
    private int calcDesiredAmount(int desired, int current) {
        return desired - current;
//        return Math.max(0, desired - current);
    }

    private BrewConfig desiredStock() {
        return BrewConfig.builder()
                .orange(brewConfig.getOrange())
                .yellow(brewConfig.getYellow())
                .pink(brewConfig.getPink())
                .violet(brewConfig.getViolet())
                .magenta(brewConfig.getMagenta())
                .fire(brewConfig.getFire())
                .cold(brewConfig.getCold())
                .acid(brewConfig.getAcid())
                .magic(brewConfig.getMagic())
                .psionic(brewConfig.getPsionic())
                .poison(brewConfig.getPoison())
                .sharp(brewConfig.getSharp())
                .blunt(brewConfig.getBlunt())
                .pierce(brewConfig.getPierce())
                .electricity(brewConfig.getElectricity())
                .mana(brewConfig.getMana())
                .cyan(brewConfig.getCyan())
                .restoreWater(brewConfig.getRestoreWater())
                .cureWater(brewConfig.getCureWater())
                .build();
    }

    private BrewConfig currentStock() {
        Map<String, Integer> currentStockMap = currentStockMap();

        return BrewConfig.builder()
                .orange(getAmountByBrewType(currentStockMap, BrewType.ORANGE))
                .yellow(getAmountByBrewType(currentStockMap, BrewType.YELLOW))
                .pink(getAmountByBrewType(currentStockMap, BrewType.PINK))
                .violet(getAmountByBrewType(currentStockMap, BrewType.VIOLET))
                .magenta(getAmountByBrewType(currentStockMap, BrewType.MAGENTA))
                .fire(getAmountByBrewType(currentStockMap, BrewType.FIRE))
                .cold(getAmountByBrewType(currentStockMap, BrewType.COLD))
                .acid(getAmountByBrewType(currentStockMap, BrewType.ACID))
                .magic(getAmountByBrewType(currentStockMap, BrewType.MAGIC))
                .psionic(getAmountByBrewType(currentStockMap, BrewType.PSIONIC))
                .poison(getAmountByBrewType(currentStockMap, BrewType.POISON))
                .sharp(getAmountByBrewType(currentStockMap, BrewType.SHARP))
                .blunt(getAmountByBrewType(currentStockMap, BrewType.BLUNT))
                .pierce(getAmountByBrewType(currentStockMap, BrewType.PIERCE))
                .electricity(getAmountByBrewType(currentStockMap, BrewType.ELECTRICITY))
                .mana(getAmountByBrewType(currentStockMap, BrewType.MANA))
                .cyan(getAmountByBrewType(currentStockMap, BrewType.CYAN))
                .restoreWater(getAmountByBrewType(currentStockMap, BrewType.RESTORE_WATER))
                .cureWater(getAmountByBrewType(currentStockMap, BrewType.CURE_WATER))
                .build();
    }

    private int getAmountByBrewType(Map<String, Integer> map, BrewType brewType) {
        int amount;
        try {
            amount = map.get(brewType.getValue());
        } catch (NullPointerException ex) {
            amount = 0;
        }

        return amount;
    }

    private Map<String, Integer> currentStockMap() {
        Map<String, Integer> currentStockMap = new HashMap<>();

        // TODO: Consider breaking this code up into separate methods vs. nested try/catch's
        try {
            ClassPathResource resource = new ClassPathResource(CURRENT_STOCK_FILE);
            InputStream inputStream = resource.getInputStream();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // TODO: Define map outside of loop scope.
                    // TODO: For each line, return Map<Line, intcount>
                    // TODO: Write separate func to convert the map to BrewConfig.
                    // TODO: Write display func to neatly display a checklist for brew order.

                    // TODO: If key exists, add to count for existing key.
                    /*
                    newMap.forEach((key, value) ->
                        existingMap.merge(key, value, (oldVal, newVal) -> Math.max(oldVal, newVal))
                    );
                     */
//                    currentStockEntry(line).forEach((k,v) ->
//                            currentStockMap.merge(k, v, (oldV, newV) -> Math.max(oldV, newV)));

                    Map<String, Integer> currentStockEntry = currentStockEntry(line);
                    String key = currentStockEntry.entrySet().iterator().next().getKey();

                    // IF key exists, increment count, else create new entry
                    if (currentStockMap.containsKey(key)) {
                        currentStockMap.put(key, currentStockMap.get(key) + currentStockEntry.get(key));
                    } else {
                        currentStockMap.put(key, currentStockEntry.get(key));
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException("Failed to parse current_stock.txt.", ex);
            }
        } catch (Exception ex) {
            // TODO: Still completely wrong. Any ex that is thrown in parsing will throw this ex.
            String msg = "Failed to open 'current_stock.txt'. File must exist in resources dir.";
            throw new RuntimeException(msg, ex);
        }

        return currentStockMap;
    }

    // TODO: This needs to simply return the map and another func will convert to Obj and tally counts
    // TODO: Re-think returning map of unknown or filter those out of final result later.
    private Map<String, Integer> currentStockEntry(String entry) {
        BrewType brewType = brewType(entry);

        if (!StringUtils.hasText(entry) || entry.contains(IGNORE_STOCK_ENTRY) || brewType == null) {
            return Map.of("UNKNOWN", 0);
        }

        int entryQuantity = entryQuantity(entry);

        // TODO: Check if this can be true at this point? Don't we throw ex if this happens?
        // NOTE: See quantity cnt switch logic
        if (entryQuantity == 0) {
            return Map.of("UNKNOWN", 0);
        }

        return Map.of(brewType.getValue(), entryQuantity);
    }

    private BrewType brewType(String entry) {
        List<String> splitEntry = List.of(entry.split(" "));

        return splitEntry.stream() // Stream the input array
                .filter(value -> value != null && !value.isEmpty()) // Ignore null/empty values
                .map(String::trim) // Trim whitespace
                .map(value -> Arrays.stream(BrewType.values())
                        .filter(brewType -> brewType.getValue().equalsIgnoreCase(value))
                        .findFirst()
                        .orElse(null)) // Map each value to a matching BrewType, if any
                .filter(Objects::nonNull) // Ignore nulls (no match)
                .findFirst() // Get the first match
                .orElse(null); // Return null if no match found
    }

    private int entryQuantity(String entry) {
        int quantity = 0;

        if (entry.contains(KEYWORD_MANY)) {
            int openParenIdx = entry.lastIndexOf('(');
            int closeParenIdx = entry.lastIndexOf(')');

            if (openParenIdx != -1 && closeParenIdx != -1 && openParenIdx < closeParenIdx) {
                quantity = Integer.parseInt(entry.substring(openParenIdx + 1, closeParenIdx));
            }

            return quantity;
        }

        // strip out null/empty elements
        List<String> entrySplit = Stream.of(entry.split(" "))
                .filter(s -> s != null && !s.isEmpty())
                .toList();

        // Example of reading stock as store owner vs. regular customer. Account for both readings.
        // 25)  4       4       Many Cyan Potions (25)
        // OR
        // 25)  4       Many Cyan Potions (25)
        // Find which element holds the quantity str, i.e. the "Many" keyword, or str quantity val.
        // Quantity str defaults to elem 3. TRY elem 2 is int, if not, then that elem has the quantity.
        String quantityStr = entrySplit.get(3);
        try {
            Integer.parseInt(entrySplit.get(2));
        } catch (NumberFormatException ex) {
            quantityStr = entrySplit.get(2);
        }

        return convertQuantityStr(quantityStr);
    }

    private int convertQuantityStr(String quantityStr) {
        int quantity = switch (quantityStr.toLowerCase()) {
            case "one" -> 1;
            case "two" -> 2;
            case "three" -> 3;
            case "four" -> 4;
            case "five" -> 5;
            case "six" -> 6;
            case "seven" -> 7;
            case "eight" -> 8;
            case "nine" -> 9;
            case "ten" -> 10;
            case "eleven" -> 11;
            case "twelve" -> 12;
            case "thirteen" -> 13;
            case "fourteen" -> 14;
            case "fifteen" -> 15;
            case "sixteen" -> 16;
            case "seventeen" -> 17;
            case "eighteen" -> 18;
            case "nineteen" -> 19;
            case "twenty" -> 20;
            default -> 0;
        };

        // TODO: This throws ex and shouldn't if item quantity is 1, or item stock is a armour, etc.
        // NOTE: Easy fix may be to just default to 1! Also, converting string "one" to 1 never happens.
//        if (quantity == 0) {
//            String msg = String.format("Failed to convert quantity (%s) String to Integer.", quantityStr);
//            throw new RuntimeException(msg);
//        }

        return quantity;
    }
}
