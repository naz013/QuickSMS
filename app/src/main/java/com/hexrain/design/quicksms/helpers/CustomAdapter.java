package com.hexrain.design.quicksms.helpers;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hexrain.design.quicksms.CreateEditActivity;
import com.hexrain.design.quicksms.R;
import com.hexrain.design.quicksms.databinding.ListItemLayoutBinding;

import java.util.List;

public class CustomAdapter extends FilterableAdapter<TemplateItem, String, CustomAdapter.ViewHolder> {

    @NonNull
    private Context mContext;

    public CustomAdapter(@NonNull Context mContext, @NonNull List<TemplateItem> mDataList,
                         @Nullable Filter<TemplateItem, String> filter) {
        super(mDataList, filter);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListItemLayoutBinding.inflate(LayoutInflater.from(mContext), parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.messageView.setText(Crypter.decrypt(getItem(position).getMessage()));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ListItemLayoutBinding binding;

        ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            if (binding != null) {
                binding.getRoot().setOnClickListener(view -> openTemplate(getAdapterPosition()));
                binding.getRoot().setOnLongClickListener(view -> {
                    showMenu(getAdapterPosition());
                    return true;
                });
            }
        }
    }

    private void showMenu(int position) {
        String[] items = new String[]{mContext.getString(R.string.string_edit_template), mContext.getString(R.string.delete)};
        AppUtils.showLCAM(mContext, item -> {
            switch (item) {
                case 0:
                    openTemplate(position);
                    break;
                case 1:
                    deleteTemplate(position);
                    break;
            }
        }, items);
    }

    private void deleteTemplate(int position) {
        TemplateItem item = getItem(position);
        Database db = new Database(mContext);
        db.open();
        db.deleteTemplate(item);
        db.close();
        removeItem(position);
        Toast.makeText(mContext, mContext.getString(R.string.string_deleted), Toast.LENGTH_SHORT).show();
    }

    private void openTemplate(int position) {
        mContext.startActivity(new Intent(mContext, CreateEditActivity.class).putExtra("id", getItem(position).getId()));
    }
}