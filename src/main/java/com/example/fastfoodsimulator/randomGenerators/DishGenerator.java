package com.example.fastfoodsimulator.randomGenerators;

import java.util.Random;

public class DishGenerator {
    private static final String[] FASTFOOD_ITEMS = {
            "Чизбургер", "Гамбургер", "Картошка фри", "Наггетсы", "Хот-дог", "Пицца", "Тако", "Шаурма", "Буррито", "Куриные крылышки"
    };

    private static final Random random = new Random();

    public static String getRandomDish() {
        int index = random.nextInt(FASTFOOD_ITEMS.length);
        return FASTFOOD_ITEMS[index];
    }
}
