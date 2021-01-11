package com.example.goallist;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goallist.AddGoalList.ShowGoalListInMainActivityAdapter;
import com.example.goallist.Database.DatabaseHelper;
import com.example.goallist.Database.MasterGoal;
import com.example.goallist.DragDropInterfaces.DragDropCallBacks;

import java.util.List;

import static com.example.goallist.AddGoalList.ShowGoalListInMainActivityAdapter.masterGoalsWithDragData;

public class ItemMoveCallBack extends ItemTouchHelper.Callback {

    DragDropCallBacks itemMoveCallBack;
    Context context;
    DatabaseHelper databaseHelper;
    List<MasterGoal> masterGoals;

    public ItemMoveCallBack(Context context, DragDropCallBacks callBack) {
        this.itemMoveCallBack = callBack;
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
        masterGoals = databaseHelper.getAllGoalAndDateFromMasterTable();

    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        int movementFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;

        return makeMovementFlags(movementFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

        if (viewHolder.getItemViewType() != target.getItemViewType()){
            return false;
        }
        itemMoveCallBack.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        for (int i = 0; i < masterGoalsWithDragData.size(); i++) {
           databaseHelper.updateMasterTableWithDragEvent(masterGoalsWithDragData.get(i).getRecyclerPosition(),masterGoalsWithDragData.get(i).getGoalTitle(),
                    masterGoalsWithDragData.get(i).getEndDate(), masterGoals.get(i).getId());

           // Log.d("TAG", "id + recy id" + masterGoalsWithDragData.get(i).getId() + " " + masterGoalsWithDragData.get(i).getRecyclerPosition());
            ShowGoalListInMainActivityAdapter.updatedDragData.add(masterGoalsWithDragData.get(i));
        }

    }
}
