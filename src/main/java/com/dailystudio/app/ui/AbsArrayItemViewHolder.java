package com.dailystudio.app.ui;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by nanye on 16/6/9.
 */
public abstract class AbsArrayItemViewHolder<Item> extends RecyclerView.ViewHolder {

    public AbsArrayItemViewHolder(View itemView) {
        super(itemView);

        itemView.setOnClickListener(mOnClickListener);
    }

    abstract public void bindItem(Context context, Item item);

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == null) {
                return;
            }

            Object tag = v.getTag();
            if (tag == null) {
                return;
            }

            onItemClick(v,
                    getAdapterPosition(),
                    getItemId(),
                    (Item)v.getTag());
        }
    };

    public void onItemClick(View view, int position, long id, Item item) {

    }

}
