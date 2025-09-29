package com.example.fastfoodsimulator.randomGenerators;

import java.util.Random;

public class DishGenerator {
    private static final String[] FASTFOOD_ITEMS = {
            "Чизбургер", "Гамбургер", "Картошка фри", "Наггетсы", "Хот-дог", "Пицца", "Тако", "Шаурма",
            "Буррито", "Куриные крылышки", "Сэндвич", "Донер", "Кебаб", "Луковые кольца", "Фиш-бургер",
            "Чикен-бургер", "Сырные палочки", "Мини-пицца", "Кока-кола", "Молочный коктейль", "Чуррос",
            "Блинчики с мясом", "Суши-роллы", "Куриный стрипс", "Фалафель"
    };
    private static final Random random = new Random();
    public static String getRandomDish() {
        int index = random.nextInt(FASTFOOD_ITEMS.length);
        return FASTFOOD_ITEMS[index];
    }
}
