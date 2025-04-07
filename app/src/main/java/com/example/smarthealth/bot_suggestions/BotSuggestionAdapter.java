package com.example.smarthealth.bot_suggestions;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;

import java.util.List;

public class BotSuggestionAdapter extends RecyclerView.Adapter<BotSuggestionViewHolder> {
    private final List<BotSuggestion> botSuggestions;
    private final OnItemListener onItemListener;

    public BotSuggestionAdapter(List<BotSuggestion> botSuggestions, OnItemListener onItemListener) {
        this.botSuggestions = botSuggestions;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public BotSuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.bot_suggestion_view, parent, false);

        return new BotSuggestionViewHolder(view, onItemListener, botSuggestions);
    }

    @Override
    public void onBindViewHolder(@NonNull BotSuggestionViewHolder holder, int position) {
        BotSuggestion botSuggestion = botSuggestions.get(position);
        holder.botSuggestionTitle.setText(botSuggestion.getTitle());
        holder.botSuggestionDescp.setText(botSuggestion.getDescription());
        holder.botSuggestionImage.setImageDrawable(AppCompatResources.getDrawable(holder.itemView.getContext(), botSuggestion.getImageId()));
    }

    @Override
    public int getItemCount() {
        return botSuggestions.size();
    }

    public interface OnItemListener {
        void onBotSuggestionClick(int position, List<BotSuggestion> botSuggestions);
    }
}
