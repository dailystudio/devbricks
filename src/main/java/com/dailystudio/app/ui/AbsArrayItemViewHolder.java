package com.dailystudio.app.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by nanye on 16/6/9.
 */
public abstract class AbsArrayItemViewHolder<Item> extends RecyclerView.ViewHolder {

    public AbsArrayItemViewHolder(View itemView) {
        super(itemView);
    }

    abstract public void bindItem(Context context, Item item);

}
