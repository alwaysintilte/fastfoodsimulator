package com.example.fastfoodsimulator.randomGenerators;

import java.util.Random;

public class NameGenerator {
    private static final String[] NAMES = {
            "Алексей", "Мария", "Даниил", "Екатерина", "Иван", "Ольга", "Никита", "София"
    };

    private static final Random random = new Random();

    public static String getRandomName() {
        int index = random.nextInt(NAMES.length);
        return NAMES[index];
    }
}
