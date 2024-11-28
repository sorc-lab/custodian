package com.sorclab.custodian.config;

public enum BrewType {
    ORANGE("Orange"),
    YELLOW("Yellow"),
    PINK("Pink"),
    VIOLET("Violet"),
    MAGENTA("Magenta"),

    FIRE("Fire"),
    COLD("Cold"),
    ACID("Acid"),
    POISON("Poison"),
    MAGIC("Magic"),
    PSIONIC("Psionic"),
    SHARP("Sharp"),
    BLUNT("Blunt"),
    PIERCE("Pierce"),
    ELECTRICITY("Electricity"),

    MANA("Mana"),
    CYAN("Cyan"),
    RESTORE_WATER("Restore"),
    CURE_WATER("Cure");

    private final String value;

    BrewType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
