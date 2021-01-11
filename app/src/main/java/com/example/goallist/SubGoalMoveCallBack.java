package com.example.goallist;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goallist.DragDropInterfaces.SubGoalCallBack;

public class SubGoalMoveCallBack extends ItemTouchHelper.Callback {

    Context context;
    SubGoalCallBack callBackAdapter;

    public SubGoalMoveCallBack(Context context,SubGoalCallBack subGoalCallBack){
        this.context = context;
        callBackAdapter = subGoalCallBack;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int movementFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;

        return makeMovementFlags(movementFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

//        if (viewHolder.getItemViewType() != target.getItemViewType()){
//            return false;
//        }
        callBackAdapter.onSubGoalMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
}
