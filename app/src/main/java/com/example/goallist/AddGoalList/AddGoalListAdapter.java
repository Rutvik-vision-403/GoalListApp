package com.example.goallist.AddGoalList;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goallist.Database.SubGoal;
import com.example.goallist.DragDropInterfaces.SubGoalCallBack;
import com.example.goallist.R;

import java.util.Collections;
import java.util.List;

public class AddGoalListAdapter extends RecyclerView.Adapter<AddGoalListAdapter.MyViewHolder> implements SubGoalCallBack {

    final List<SubGoal> goalItem;
    final Context activityContext;
    final RecyclerView currentRecyclerView;

    public AddGoalListAdapter(List<SubGoal> goalItem, RecyclerView currentRecyclerView , Context activityContext) {
        this.goalItem = goalItem;
        this.activityContext = activityContext;
        this.currentRecyclerView = currentRecyclerView;
    }

    @Override
    public boolean onSubGoalMove(int from, int to) {
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(goalItem, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(goalItem, i, i - 1);
            }
        }
        notifyItemMoved(from, to);


        return true;
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

        holder.setIsRecyclable(false);

        if(goalItem.get(position).getSubGoal() == null){
            holder.getUserNotes().setHint("Note");
        }else{
            holder.getUserNotes().setText(goalItem.get(position).getSubGoal());
        }

        holder.getUserNotes().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {

                goalItem.get(position).setSubGoal(editable.toString());
            }
        });


        holder.getDeleteNote().setOnClickListener(view -> {

            goalItem.remove(position);
            currentRecyclerView.removeViewAt(position);
            notifyItemRemoved(position);
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return goalItem.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if (holder.getUserNotes().getText().toString().isEmpty()){
            holder.getUserNotes().requestFocus();
            InputMethodManager imm = (InputMethodManager) activityContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(holder.getUserNotes(), InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
