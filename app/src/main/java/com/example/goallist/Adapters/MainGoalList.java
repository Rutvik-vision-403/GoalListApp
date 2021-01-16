package com.example.goallist.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainGoalList extends RecyclerView.Adapter<MainGoalList.MainActViewHolder> implements DragDropCallBacks, Filterable {

    public static List<MasterGoal> masterGoalsWithDragData = null;
    public List<MasterGoal> filterList;
    final Context mainActivityContext;
    public static boolean isFromEditButton = false;
    public static boolean isFromEditButtonForSave = false;

    final DatabaseHelper databaseHelper;

    public static class MainActViewHolder extends RecyclerView.ViewHolder {

        private final ImageView expand, collapse, deleteNote, updateNote;
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


    public MainGoalList(List<MasterGoal> masterGoals, Context mainActivityContext) {

        masterGoalsWithDragData = masterGoals;
        filterList = masterGoals;
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

        MasterGoal masterGoal = filterList.get(position);
        RecyclerView subGoal = holder.getChildRecycler();

        holder.getGoalTitle().setText(masterGoal.getGoalTitle());
        holder.getEndDate().setText(masterGoal.getEndDate());

        // Logic for Date color change accordingly 3 month( < 3 RED, > 3 BLUE ) 6 month ( < 6 BLUE, > 6 Green )
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

                // databaseHelper.deleteGoalAndSubGoal(databaseHelper.getAllGoalAndDateFromMasterTable().get(holder.getAdapterPosition()));

                databaseHelper.deleteGoalAndSubGoal(filterList.get(holder.getAdapterPosition()));
                MasterGoal tempDelete = filterList.remove(holder.getAdapterPosition());

                for (int iterate = 0; iterate < masterGoalsWithDragData.size(); iterate++) {
                    if (masterGoalsWithDragData.get(iterate).getRecyclerPosition() == tempDelete.getRecyclerPosition()) {
                        masterGoalsWithDragData.remove(iterate);
                    }
                }
                notifyDataSetChanged();

            }).setNegativeButton("Cancel", (dialogInterface, i) -> {

            }).setCancelable(false).create();

            alertDialog.show();
        });

        LinearLayout temp = holder.getShowVisible();

        holder.getExpand().setOnClickListener(view -> {

            // TODO : Remove this line after testing
            //int tempMasterRecId = databaseHelper.getAllGoalAndDateFromMasterTable().get(holder.getAdapterPosition()).getRecyclerPosition();

            int tempMasterRecId = filterList.get(holder.getAdapterPosition()).getRecyclerPosition();
            List<SubGoal> dataOnly = databaseHelper.getSubGoalWithMasterId(tempMasterRecId);

            subGoal.setLayoutManager(new LinearLayoutManager(mainActivityContext));
            subGoal.setAdapter(new SubTaskWhenExpand(dataOnly, mainActivityContext));

            temp.setVisibility(View.VISIBLE);
            holder.getExpand().setVisibility(View.INVISIBLE);

        });

        holder.getCollapse().setOnClickListener(view -> {
            temp.setVisibility(View.GONE);
            holder.getExpand().setVisibility(View.VISIBLE);
        });


        holder.getUpdateNote().setOnClickListener(view -> {

            isFromEditButton = true;
            isFromEditButtonForSave = true;
            forUpdate = true;

            Intent intent = new Intent(mainActivityContext, AddGoal.class);

            // TODO : remove below comment after testing
            //int tableId;
            //int tempRecPosition = databaseHelper.getAllGoalAndDateFromMasterTable().get(holder.getAdapterPosition()).getRecyclerPosition();

//            if (!listIsFilter) {
//                tableId = databaseHelper.getAllGoalAndDateFromMasterTable().get(holder.getAdapterPosition()).getId();
//                Toast.makeText(mainActivityContext, ""+listIsFilter, Toast.LENGTH_SHORT).show();
//            } else {
//                tableId = filterList.get(holder.getAdapterPosition()).getId();
//                Toast.makeText(mainActivityContext, ""+ listIsFilter + tableId, Toast.LENGTH_SHORT).show();
//            }

            int tempRecPosition = filterList.get(holder.getAdapterPosition()).getRecyclerPosition();

            // TODO : remove second passing data line and comment after testing
            intent.putExtra("Current master id", tempRecPosition);
            //intent.putExtra("Current Master Index ID", tableId);
            intent.putExtra("Current Master Index ID", tempRecPosition);

            mainActivityContext.startActivity(intent);
        });


    }

    @Override
    public void onItemMove(int from, int to) {
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(filterList, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(filterList, i, i - 1);
            }
        }
        notifyItemMoved(from, to);
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String filterString = charSequence.toString();
                if (filterString.isEmpty()) {
                    filterList = masterGoalsWithDragData;
                } else {

                    List<MasterGoal> tempMasterGoal = new ArrayList<>();

                    for (MasterGoal f : masterGoalsWithDragData) {

                        List<SubGoal> subGoals = databaseHelper.getSubGoalWithRecyclerPosition(f.getRecyclerPosition());

                        if (f.getGoalTitle().toLowerCase().contains(filterString.toLowerCase())) {
                            tempMasterGoal.add(f);
                        }

                        for (SubGoal s : subGoals) {
                            if (s.getSubGoal().toLowerCase().contains(filterString.toLowerCase())) {
                                if (!tempMasterGoal.contains(f)){
                                    tempMasterGoal.add(f);
                                }
                            }
                        }

                    }

                    filterList = tempMasterGoal;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filterList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterList = (ArrayList<MasterGoal>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

}
