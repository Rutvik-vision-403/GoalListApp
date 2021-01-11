package com.example.goallist.AddGoalList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goallist.AddGoal;
import com.example.goallist.Database.DatabaseHelper;
import com.example.goallist.Database.MasterGoal;
import com.example.goallist.Database.SubGoal;
import com.example.goallist.DragDropInterfaces.DragDropCallBacks;
import com.example.goallist.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.example.goallist.AddGoal.forUpdate;

public class ShowGoalListInMainActivityAdapter extends RecyclerView.Adapter<ShowGoalListInMainActivityAdapter.MainActViewHolder> implements DragDropCallBacks {

    public static List<MasterGoal> masterGoalsWithDragData = null;
    public static List<MasterGoal> updatedDragData = new ArrayList<>();
    public List<MasterGoal> masterGoals;
    final Context mainActivityContext;
    public static boolean isFromEditButton = false;
    public static boolean isIsFromEditButtonForSave = false;
    RecyclerView recyclerView;

    final DatabaseHelper databaseHelper;

    public ShowGoalListInMainActivityAdapter(RecyclerView recyclerView, List<MasterGoal> masterGoals, Context mainActivityContext) {

        masterGoalsWithDragData = masterGoals;
        this.mainActivityContext = mainActivityContext;
        databaseHelper = new DatabaseHelper(mainActivityContext);
        updatedDragData = databaseHelper.getAllGoalAndDateFromMasterTable();
        this.recyclerView = recyclerView;
        this.masterGoals = masterGoals;
    }

    @NonNull
    @Override
    public MainActViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_goal_item, parent, false);
        return new MainActViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainActViewHolder holder, int position) {

        MasterGoal masterGoal = masterGoalsWithDragData.get(position);
        RecyclerView subGoal = holder.getChildRecycler();

        holder.getGoalTitle().setText(masterGoal.getGoalTitle());
        holder.getEndDate().setText(masterGoal.getEndDate());

        try {

            if (masterGoal.getEndDate() != null) {
                String stringToDate = masterGoal.getEndDate();

                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date userDate = simpleDateFormat.parse(stringToDate);
                String userDateInString = simpleDateFormat.format(userDate);

                String[] dayMonthYear = userDateInString.split("/");
                int userYear = Integer.parseInt(dayMonthYear[2]);
                int userMonth = Integer.parseInt(dayMonthYear[1]);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
                Date sysDateAndTime = Calendar.getInstance().getTime();
                String systemDate = simpleDateFormat1.format(sysDateAndTime);

                String[] sysDayMonthYear = systemDate.split("/");
                int sysYear = Integer.parseInt(sysDayMonthYear[2]);
                int sysMonth = Integer.parseInt(sysDayMonthYear[1]);

                if (userDate == null) {
                    throw new Exception();
                }

                if (sysYear > userYear) {
                    holder.getEndDate().setTextColor(Color.RED);
                } else if (sysYear < userYear) {

                    int tempSysMonth = 12 - sysMonth;
                    if ((tempSysMonth + userMonth) > 6) {
                        holder.getEndDate().setTextColor(Color.GREEN);

                    } else if ((tempSysMonth + userMonth) <= 6) {

                        if ((tempSysMonth + userMonth) >= 3) {
                            holder.getEndDate().setTextColor(Color.BLUE);

                        } else {
                            holder.getEndDate().setTextColor(Color.RED);
                        }
                    }
                } else if (sysYear == userYear) {

                    if ((userMonth - sysMonth) > 6) {
                        holder.getEndDate().setTextColor(Color.GREEN);
                    } else if ((userMonth - sysMonth) <= 6) {
                        if ((userMonth - sysMonth) >= 3) {
                            holder.getEndDate().setTextColor(Color.BLUE);
                        } else {
                            holder.getEndDate().setTextColor(Color.RED);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.getDeleteNote().setOnClickListener(view -> {

            AlertDialog alertDialog = new AlertDialog.Builder(mainActivityContext).setMessage("Confirm delete!").setPositiveButton("Delete", (dialogInterface, i) -> {
               // databaseHelper.deleteGoalAndSubGoal(masterGoalsWithDragData.get(position));

                databaseHelper.deleteGoalAndSubGoal(databaseHelper.getAllGoalAndDateFromMasterTable().get(holder.getAdapterPosition()));
                masterGoalsWithDragData.remove(holder.getAdapterPosition());
                recyclerView.removeViewAt(holder.getAdapterPosition());
                notifyDataSetChanged();
            }).setNegativeButton("Cancel", (dialogInterface, i) -> {

            }).setCancelable(false).create();

            alertDialog.show();
        });

        LinearLayout temp = holder.getShowVisible();

        holder.getExpand().setOnClickListener(view -> {

            //List<SubGoal> dataOnly = databaseHelper.getSubGoalWithMasterId(updatedDragData.get(position).getRecyclerPosition());

            int tempMasterRecyId = databaseHelper.getAllGoalAndDateFromMasterTable().get(holder.getAdapterPosition()).getRecyclerPosition();
            List<SubGoal> dataOnly = databaseHelper.getSubGoalWithMasterId(tempMasterRecyId);

            subGoal.setLayoutManager(new LinearLayoutManager(mainActivityContext));
            subGoal.setAdapter(new AddGoalSubTaskWhenExpandAdapter(dataOnly, mainActivityContext));

            temp.setVisibility(View.VISIBLE);
            holder.getExpand().setVisibility(View.INVISIBLE);
        });

        holder.getCollapse().setOnClickListener(view -> {
            temp.setVisibility(View.GONE);
            holder.getExpand().setVisibility(View.VISIBLE);
        });

        holder.getUpdateNote().setOnClickListener(view -> {
            isFromEditButton = true;
            isIsFromEditButtonForSave = true;
            forUpdate = true;

            //int recyPostion = updatedDragData.get(position).getRecyclerPosition();

            Intent intent = new Intent(mainActivityContext, AddGoal.class);
            // for Sub goals
           // intent.putExtra("Current master id", recyPostion);

            // For Title and Date
            //intent.putExtra("Current Master Index ID", databaseHelper.getAllGoalAndDateFromMasterTable().get(holder.getAdapterPosition()).getId());

            int solidRecPostion = databaseHelper.getAllGoalAndDateFromMasterTable().get(holder.getAdapterPosition()).getRecyclerPosition();

            intent.putExtra("Current master id", solidRecPostion);
            intent.putExtra("Current Master Index ID", databaseHelper.
                    getAllGoalAndDateFromMasterTable().get(holder.getAdapterPosition()).getId());
            mainActivityContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return masterGoalsWithDragData.size();
    }

    @Override
    public boolean onItemMove(int from, int to) {
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(masterGoalsWithDragData, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(masterGoalsWithDragData, i, i - 1);
            }
        }
        notifyItemMoved(from, to);
        return true;
    }

    public static class MainActViewHolder extends RecyclerView.ViewHolder {

        private final ImageView expand, collapse, deleteNote, updateNote, dragView;
        private final LinearLayout showVisible;
        private final TextView goalTitle, endDate;
        private final RecyclerView childRecycler;

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
            dragView = itemView.findViewById(R.id.dragView);
        }

        public ImageView getDragView() {
            return dragView;
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
