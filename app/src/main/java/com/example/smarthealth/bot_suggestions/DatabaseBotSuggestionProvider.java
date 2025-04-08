package com.example.smarthealth.bot_suggestions;

import com.example.smarthealth.R;

import java.util.ArrayList;
import java.util.List;

public class DatabaseBotSuggestionProvider implements BotSuggestionProvider {
    @Override
    public List<BotSuggestion> getBotSuggestions() {
        String[] titles = {
                "Prioritize Healthy Fats",
                "Eat More Fiber-Rich Foods",
                "Balance Your Plate"
        };
        String[] descriptions = {
               "Incorporate sources of healthy fats like avocados, nuts, seeds, and olive oil into your meals. These fats support brain function, hormone production, and help you stay full longer, reducing unhealthy snacking.",
               "Aim to include plenty of fruits, vegetables, whole grains, and legumes in your daily diet. Fiber supports healthy digestion, keeps blood sugar levels stable, and promotes a feeling of fullness throughout the day.",
               "Every meal should include a good mix of protein, carbs, healthy fats, and fiber. This balance helps maintain energy levels, improves metabolism, and prevents nutrient deficiencies. Think colorful and diverse when building your plate."
        };
        int[] imageIds = {
                R.drawable.bot_suggestion_image_1,
                R.drawable.bot_suggestion_image_2,
                R.drawable.bot_suggestion_image_3
        };

        List<BotSuggestion> botSuggestions = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            botSuggestions.add(new BotSuggestion(titles[i], descriptions[i], imageIds[i]));
        }

        return botSuggestions;
    }

}
