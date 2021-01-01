package com.example.goallist.AddGoalList;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goallist.R;

import java.util.ArrayList;
import java.util.List;

public class AddGoalList extends RecyclerView.Adapter<AddGoalList.MyViewHolder> {

    ArrayList<Integer> goalItem;
    Context activityContext;

    public static List<String> subTask = new ArrayList<String>();

    public AddGoalList(ArrayList<Integer> goalItem, Context activityContext) {
        this.goalItem = goalItem;
        this.activityContext = activityContext;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView deleteNote;
        private final EditText userNotes;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteNote = itemView.findViewById(R.id.deleteNote);
            userNotes = itemView.findViewById(R.id.addNewItem);
        }

        public ImageView getDeleteNote() {
            return deleteNote;
        }

        public EditText getUserNotes() {
            return userNotes;
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);

        return  new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.getDeleteNote().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                goalItem.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, goalItem.size());
            }
        });

    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.getUserNotes().requestFocus();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return goalItem.size();
    }


}
