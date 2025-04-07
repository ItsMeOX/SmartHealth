package com.example.smarthealth.bot_suggestions;

import com.example.smarthealth.R;

import java.util.ArrayList;
import java.util.List;

public class DatabaseBotSuggestionProvider implements BotSuggestionProvider {
    @Override
    public List<BotSuggestion> getBotSuggestions() {
        String[] titles = {"Increase Protein Intake", "Choose complex carbohydrates", "Stay hydrated"};
        String[] descriptions = {"Increase in take of fish, chicken, eggs, tofu, legumes to...",
                "Choose complex carbohydrates for example whole grains, vegetables to avoid sugar spikes. Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao." +
                        "Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                        +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                        +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                        +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                        +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                        +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                        +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.",
                "Drink more water, or herbal teas, soups to stay hydrated, because H2O make you human.",};

        List<BotSuggestion> botSuggestions = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            botSuggestions.add(new BotSuggestion(titles[i], descriptions[i], R.drawable.bot_suggestion_image));
        }

        return botSuggestions;
    }

}
