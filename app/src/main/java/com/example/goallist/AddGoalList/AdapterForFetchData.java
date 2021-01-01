package com.example.goallist.AddGoalList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goallist.Database.SubGoal;
import com.example.goallist.R;

import java.util.List;

public class AdapterForFetchData extends RecyclerView.Adapter<AdapterForFetchData.viewHolder> {

    List<SubGoal> subGoalsDataOnly;
    Context addGoalActivityContext;


    public AdapterForFetchData(List<SubGoal> subGoalsDataOnly,Context addGoalActivityContext){
        this.subGoalsDataOnly = subGoalsDataOnly;
        this.addGoalActivityContext = addGoalActivityContext;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        holder.getSetDataOnIt().setText(subGoalsDataOnly.get(position).getSubGoal());
    }

    @Override
    public int getItemCount() {
        return subGoalsDataOnly.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{

        private final EditText setDataOnIt;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            setDataOnIt = itemView.findViewById(R.id.addNewItem);
        }

        public EditText getSetDataOnIt() {
            return setDataOnIt;
        }
    }
}
