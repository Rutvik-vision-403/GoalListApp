package com.example.goallist.AddGoalList;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goallist.AddGoal;
import com.example.goallist.Database.DatabaseHelper;
import com.example.goallist.Database.MasterGoal;
import com.example.goallist.Database.SubGoal;
import com.example.goallist.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShowGoalListInMainActivity extends RecyclerView.Adapter<ShowGoalListInMainActivity.MainActViewHolder> {

    List<MasterGoal> masterGoals;
    List<SubGoal> subGoals;
    Context mainActivityContext;
    public static final String CURRENT_SELECTED_GOAL_INDEX_TAG = "Goal title index";
    public static final String LIST_OF_ALL_SUB_GOALS_WITH_PERTICULAR_INDEX = "sub goal data with specific index";
    public static boolean isFromEditButton = false;

    DatabaseHelper databaseHelper;

    public ShowGoalListInMainActivity(List<MasterGoal> masterGoals, List<SubGoal> subGoals, Context mainActivityContext) {

        this.masterGoals = masterGoals;
        this.subGoals = subGoals;
        this.mainActivityContext = mainActivityContext;

        databaseHelper = new DatabaseHelper(mainActivityContext);
    }

    @NonNull
    @Override
    public MainActViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_goal_item, parent, false);
        return new MainActViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainActViewHolder holder, int position) {

        MasterGoal masterGoal = masterGoals.get(position);

        holder.getGoalTitle().setText(masterGoal.getGoalTitle());
        holder.getEndDate().setText(masterGoal.getEndDate());

        holder.getDeleteNote().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper databaseHelper = new DatabaseHelper(mainActivityContext);
                databaseHelper.deleteGoalAndSubGoal(masterGoals.get(position));
                masterGoals.remove(position);

                notifyDataSetChanged();
            }
        });

        LinearLayout temp = holder.getShowVisible();

        holder.getExpand().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper databaseHelper = new DatabaseHelper(mainActivityContext);
                List<SubGoal> dataOnly = databaseHelper.getSubGoalDataWithPosition(position);

                RecyclerView subGoal = holder.getChildRecycler();
                subGoal.setLayoutManager(new LinearLayoutManager(mainActivityContext));
                subGoal.setAdapter(new AddGoalSubTaskWhenExpand(dataOnly,mainActivityContext));
                temp.setVisibility(View.VISIBLE);
                holder.getExpand().setVisibility(View.INVISIBLE);
            }
        });

        holder.getCollapse().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp.setVisibility(View.GONE);
                holder.getExpand().setVisibility(View.VISIBLE);
            }
        });

        holder.getUpdateNote().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromEditButton = true;

                List<SubGoal> allCurrentMasterSubGoals = databaseHelper.getSubGoalDataWithPosition(position);
                // pass position of this current onclick view in intent and get this position goal title and all sub goals
                Intent intent = new Intent(mainActivityContext, AddGoal.class);
                intent.putExtra(CURRENT_SELECTED_GOAL_INDEX_TAG,position);
                intent.putExtra(LIST_OF_ALL_SUB_GOALS_WITH_PERTICULAR_INDEX, (Serializable)allCurrentMasterSubGoals);
                mainActivityContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return masterGoals.size();
    }


    public static class MainActViewHolder extends RecyclerView.ViewHolder {

        private ImageView expand, collapse, deleteNote, updateNote;
        private LinearLayout showVisible;
        private TextView goalTitle, endDate;
        private RecyclerView childRecycler;

        public MainActViewHolder(@NonNull View itemView) {
            super(itemView);

            collapse = itemView.findViewById(R.id.collapseView);
            expand = itemView.findViewById(R.id.expandView);
            showVisible = itemView.findViewById(R.id.expandMenu);
            goalTitle = itemView.findViewById(R.id.goalTitle);
            endDate = itemView.findViewById(R.id.goalDate);
            deleteNote = itemView.findViewById(R.id.deleteGoal);
            updateNote = itemView.findViewById(R.id.updateGoals);
            childRecycler = itemView.findViewById(R.id.addSubTaskHere);
        }

        public ImageView getCollapse() {
            return collapse;
        }

        public ImageView getExpand() {
            return expand;
        }

        public ImageView getDeleteNote() {
            return deleteNote;
        }

        public LinearLayout getShowVisible() {
            return showVisible;
        }

        public TextView getGoalTitle() {
            return goalTitle;
        }

        public TextView getEndDate() {
            return endDate;
        }

        public ImageView getUpdateNote() {
            return updateNote;
        }

        public RecyclerView getChildRecycler() {
            return childRecycler;
        }
    }


}
