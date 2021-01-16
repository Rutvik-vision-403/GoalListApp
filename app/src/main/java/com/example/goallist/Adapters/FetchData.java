package com.example.goallist.Adapters;

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

import com.example.goallist.CallBackClasses.ItemMoveCallbackForSubGoal;
import com.example.goallist.Database.SubGoal;
import com.example.goallist.DragDropInterfaces.DragDropCallBacks;
import com.example.goallist.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FetchData extends RecyclerView.Adapter<FetchData.viewHolder> implements ItemMoveCallbackForSubGoal.ItemTouchHelperContract {

    final ArrayList<SubGoal> subGoalsDataOnly;

    final Context addGoalActivityContext;
    final RecyclerView currentRecyclerView;

    public FetchData(ArrayList<SubGoal> subGoalsDataOnly, RecyclerView currentRecyclerView, Context addGoalActivityContext) {
        this.subGoalsDataOnly = subGoalsDataOnly;
        this.addGoalActivityContext = addGoalActivityContext;
        this.currentRecyclerView = currentRecyclerView;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        /*
         * used below method to cancel viewHolder recycle
         * because old data show if item delete and add new item inside list from cache memory
         * */
        holder.setIsRecyclable(false);

        if (subGoalsDataOnly.get(position).getSubGoal() == null) {
            holder.getSetDataOnIt().setHint("Note");
        } else {
            holder.getSetDataOnIt().setText(subGoalsDataOnly.get(position).getSubGoal());
        }

        holder.getSetDataOnIt().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                subGoalsDataOnly.get(position).setSubGoal(editable.toString());
            }
        });

        holder.getDeleteGoal().setOnClickListener(v -> {

            subGoalsDataOnly.remove(position);
            currentRecyclerView.removeViewAt(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return subGoalsDataOnly.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(subGoalsDataOnly, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(subGoalsDataOnly, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        private final EditText setDataOnIt;
        private final ImageView deleteGoal;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            setDataOnIt = itemView.findViewById(R.id.addNewItem);
            deleteGoal = itemView.findViewById(R.id.deleteNote);
        }

        public EditText getSetDataOnIt() {
            return setDataOnIt;
        }

        public ImageView getDeleteGoal() {
            return deleteGoal;
        }

    }

    //    TODO : remove below code after testing

    //    @Override
    //    public int getItemViewType(int position) {
    //        return position;
    //    }


    @Override
    public void onViewAttachedToWindow(@NonNull viewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if (holder.getSetDataOnIt().getText().toString().isEmpty()) {
            holder.getSetDataOnIt().requestFocus();

            InputMethodManager imm = (InputMethodManager) addGoalActivityContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(holder.getSetDataOnIt(), InputMethodManager.SHOW_IMPLICIT);
        }

    }
}
