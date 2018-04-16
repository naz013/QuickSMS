package com.hexrain.design.quicksms.helpers;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hexrain.design.quicksms.databinding.ListItemLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class QuickAdapter extends RecyclerView.Adapter<QuickAdapter.ViewHolder> {

    private List<TemplateItem> mDataList = new ArrayList<>();
    private Context mContext;
    private int selectedPosition = -1;
    private ColorSetter themeUtil;

    public QuickAdapter(List<TemplateItem> mDataList, Context context) {
        this.mDataList.clear();
        this.mDataList.addAll(mDataList);
        this.mContext = context;
        themeUtil = new ColorSetter(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListItemLayoutBinding.inflate(LayoutInflater.from(mContext), parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TemplateItem item = mDataList.get(position);
        holder.binding.messageView.setText(Crypter.decrypt(item.getMessage()));
        if (item.isSelected()) {
            holder.binding.cardView.setCardBackgroundColor(themeUtil.getColor(themeUtil.colorStatus()));
        } else {
            holder.binding.cardView.setCardBackgroundColor(themeUtil.getCardStyle());
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ListItemLayoutBinding binding;

        ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            if (binding != null) {
                binding.getRoot().setOnClickListener(view -> selectItem(getAdapterPosition()));
            }
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public TemplateItem getItem(int position) {
        return mDataList.get(position);
    }

    private void selectItem(int position) {
        if (position == selectedPosition) return;
        if (selectedPosition != -1 && selectedPosition < mDataList.size()) {
            mDataList.get(selectedPosition).setSelected(false);
            notifyItemChanged(selectedPosition);
        }
        this.selectedPosition = position;
        if (position < mDataList.size()) {
            mDataList.get(position).setSelected(true);
            notifyItemChanged(position);
        }
    }
}