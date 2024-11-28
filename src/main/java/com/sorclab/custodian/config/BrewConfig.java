package com.sorclab.custodian.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("brew-config")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BrewConfig {
    private int orange;
    private int yellow;
    private int pink;
    private int violet;
    private int magenta;

    private int fire;
    private int cold;
    private int acid;
    private int magic;
    private int psionic;
    private int poison;
    private int sharp;
    private int blunt;
    private int pierce;
    private int electricity;

    private int mana;
    private int cyan;
    private int restoreWater;
    private int cureWater;
}
