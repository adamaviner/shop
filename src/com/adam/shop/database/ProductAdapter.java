package com.adam.shop.database;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.adam.shop.ChooseActivity;
import com.adam.shop.R;

import java.util.concurrent.Callable;

public class ProductAdapter extends CursorAdapter {

    private final LayoutInflater mInflater;
    private final boolean isGridView;

    public ProductAdapter(final Context context, final Cursor c, final int flags, final boolean isGridView) {
        super(context, c, flags);
        mInflater = LayoutInflater.from(context);
        this.isGridView = isGridView;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        Holder holder = (Holder) view.getTag();

        if (holder == null) {
            holder = new Holder();
            holder.productName = (TextView) view.findViewById(R.id.productName);
            holder.productQuantity = (TextView) view.findViewById(R.id.productQuantity);
            holder.productDescription = (TextView) view.findViewById(R.id.productDescription);
            holder.nameIndex = cursor.getColumnIndexOrThrow(ProductTable.NAME);
            holder.DescriptionIndex = cursor.getColumnIndexOrThrow(ProductTable.DESCRIPTION);
            holder.quantityIndex = cursor.getColumnIndexOrThrow(ProductTable.QUANTITY);
            holder.idIndex = cursor.getColumnIndexOrThrow(ProductTable.ROW_ID);
            holder.callWhenBinding = null;
            view.setTag(holder);
        }

        holder.productName.setText(cursor.getString(holder.nameIndex));
        holder.productQuantity.setText(cursor.getString(holder.quantityIndex));
        String desc = cursor.getString(holder.DescriptionIndex);
        desc = desc == null ? "" : desc;
        holder.productDescription.setText(desc.toUpperCase());
        holder.productId = cursor.getInt(holder.idIndex);
        Log.d(ChooseActivity.TAG, "Bound view: " + view.getId() + " to Product: " + holder.productName.getText());
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
        View view;
        if (isGridView) view = mInflater.inflate(R.layout.product_item, parent, false);
        else view = mInflater.inflate(R.layout.list_item, parent, false);
        return view;
    }

    public static class Holder {
        public int idIndex;
        public int nameIndex;
        public int DescriptionIndex;
        public int productId;
        public TextView productName;
        public TextView productQuantity;
        public Callable<Void> callWhenBinding;
        public TextView productDescription;
        public int quantityIndex;
    }
}
