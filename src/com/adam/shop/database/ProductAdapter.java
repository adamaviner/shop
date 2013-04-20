package com.adam.shop.database;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;
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
            holder.productQuantity = (EditText) view.findViewById(R.id.productQuantity);
            //holder.checked = (CheckBox) view.findViewById(R.id.purchasedCheckbox);
            holder.nameIndex = cursor.getColumnIndexOrThrow(ChoiceTable.COLUMN_NAME);
            holder.checkedIndex = cursor.getColumnIndexOrThrow(ChoiceTable.COLUMN_CHECKED);
            holder.idIndex = cursor.getColumnIndexOrThrow(ChoiceTable.COLUMN_ID);
            holder.checked = false;
            holder.callWhenBinding = null;
            view.setTag(holder);
        }

//        try{ //call the function if possible TODO: move this to a custom view?
//            holder.callWhenBinding.call();
//        } catch (Exception e) {
//        }
//        holder.callWhenBinding = null;

        holder.productName.setText(cursor.getString(holder.nameIndex));
        holder.productId = cursor.getInt(holder.idIndex);
        holder.checked = (cursor.getInt(holder.checkedIndex) != 0);
        if (holder.checked) {
            view.setAlpha(0.6f);
        } else view.setAlpha(1f);
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
        View view;
        if (isGridView) view = mInflater.inflate(R.layout.product_item, parent, false);
        else view = mInflater.inflate(R.layout.list_item, parent, false);
        return view;
    }

    public static class Holder {
        public int checkedIndex;
        public int idIndex;
        public int nameIndex;
        public int productId;
        public boolean checked;
        public TextView productName;
        public EditText productQuantity;
        public Callable<Void> callWhenBinding;

        public void callWhenBinding(Callable<Void> callWhenBinding) {
            this.callWhenBinding = callWhenBinding;
        }
    }
}
