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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

// TODO: Logic is a mess. Finish prototype, write tests, then refactor.

@Service
@RequiredArgsConstructor
public class BrewService {
    private static final String CURRENT_STOCK_FILE = "current_stock.txt";
    private static final String IGNORE_STOCK_ENTRY = "q to quit";
    private static final String KEYWORD_MANY = "Many";

    private final BrewConfig brewConfig;

    public BrewConfig brewConfig() {
        return brewConfig;
    }

    public Map<String, Integer> currentStockMap() {
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

                    if (currentStockMap.containsKey(key)) {
                        System.out.println(key + ": KEY EXISTS ALREADY, INCREMENT COUNT!");
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
                .filter(brewType -> brewType != null) // Ignore nulls (no match)
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

        String quantityStr = entrySplit.get(2);

        return convertQuantityStr(quantityStr);
    }

    private int convertQuantityStr(String quantityStr) {
        String quantityToLower = quantityStr.toLowerCase();
        System.out.println(quantityToLower);
        int quantity = switch (quantityToLower) {
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

        if (quantity == 0) {
            throw new RuntimeException("Failed to convert quantity String to Integer.");
        }

        return quantity;
    }
}
