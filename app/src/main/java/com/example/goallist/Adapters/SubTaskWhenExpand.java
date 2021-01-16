package com.example.goallist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goallist.Database.SubGoal;
import com.example.goallist.R;

import java.util.List;

public class SubTaskWhenExpand extends RecyclerView.Adapter<SubTaskWhenExpand.MyAdapter> {

    final List<SubGoal> subGoalsDataOnly;
    final Context mainActivityContext;

    public SubTaskWhenExpand(List<SubGoal> subGoalsDataOnly, Context mainActivityContext){

        this.subGoalsDataOnly = subGoalsDataOnly;
        this.mainActivityContext = mainActivityContext;
    }

    @NonNull
    @Override
    public MyAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_goal_sub_task_item,parent,false);
        return new MyAdapter(view);
    }

    @Override
    public int getItemCount() {
        return subGoalsDataOnly.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter holder, int position) {

        SubGoal subGoal = subGoalsDataOnly.get(position);

        holder.getSubGoals().setText(subGoal.getSubGoal());
    }

    public static class MyAdapter extends RecyclerView.ViewHolder{

        private final TextView subGoalsShow;

        public MyAdapter(@NonNull View itemView) {
            super(itemView);

            subGoalsShow = itemView.findViewById(R.id.mySubGoals);
        }

        public TextView getSubGoals() {
            return subGoalsShow;
        }
    }
}
