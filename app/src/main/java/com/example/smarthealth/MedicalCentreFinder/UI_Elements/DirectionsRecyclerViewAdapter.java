package com.example.smarthealth.MedicalCentreFinder.UI_Elements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;

import java.util.HashMap;
import java.util.List;

public class DirectionsRecyclerViewAdapter extends RecyclerView.Adapter<DirectionsRecyclerViewAdapter.ViewHolder> {

    private final List<HashMap<String, String>> mData;

    public List<HashMap<String, String>> getmData()
    {
        return mData;
    }
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    DirectionsRecyclerViewAdapter(Context context, List<HashMap<String, String>> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        //here, mdata refers to the particular list of all of the sequential directions to take, to reach given destination
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row_directions, parent, false);
        //initialise to the recyclerview_directions layout resource file (.xml)
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //position refers to the particular index position of the list of hashmap data which is mData,
        //or in this case the list of all hashmap direction instructions.
        //So, we get one instruction by passing in position as an index argument.
        HashMap<String, String> aSingleInstruction = (HashMap<String, String>) mData.get(position);
        Log.d("aSingleOne",aSingleInstruction.toString());
        //String instructionType = aSingleInstruction.get("Instruction Type: ");
        String direction = aSingleInstruction.get("Directions: ");

        Log.d("mdata",mData.toString());

        //now, set the text of this particular holder at this particular index,
        //because the recyclerview will have n total viewholder instances.
        //This sets the attributes of the ith, or position-indexed holder

        // Explicitly set text
        //holder.directionsText.setTextColor(Color.BLACK); // Temporary

       // holder.instructionTypeText.setText(instructionType);
        holder.instructionTypeText.setText(direction);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData != null? mData.size():0;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //creating variables to reference the UI .xml Text view elements in recyclerview_row.xml!
        TextView instructionTypeText;

        ViewHolder(View itemView) {
            super(itemView);
            instructionTypeText = itemView.findViewById(R.id.instruction_type);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}