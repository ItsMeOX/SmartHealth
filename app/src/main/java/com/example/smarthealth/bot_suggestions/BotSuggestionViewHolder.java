package com.example.smarthealth.bot_suggestions;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;

import java.util.List;

public class BotSuggestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final ImageView botSuggestionImage;
    public final TextView botSuggestionTitle;
    public final TextView botSuggestionDescp;
    private final List<BotSuggestion> botSuggestions;
    private final BotSuggestionAdapter.OnItemListener onItemListener;

    public BotSuggestionViewHolder(@NonNull View itemView, BotSuggestionAdapter.OnItemListener onItemListener, List<BotSuggestion> botSuggestions) {
        super(itemView);
        botSuggestionImage = itemView.findViewById(R.id.botSuggestionImage);
        botSuggestionTitle = itemView.findViewById(R.id.botSuggestionTitle);
        botSuggestionDescp = itemView.findViewById(R.id.botSuggestionDesc);
        this.onItemListener = onItemListener;
        this.botSuggestions = botSuggestions;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onItemListener.onBotSuggestionClick(getAdapterPosition(), botSuggestions);
    }
}
