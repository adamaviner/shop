package com.adam.shop.database;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.adam.shop.R;

public class ProductAdapter extends CursorAdapter {
	
	private final LayoutInflater mInflater;
	private final boolean isGridView;
	
	public ProductAdapter(final Context context, final Cursor c, final int flags,
			final boolean isGridView) {
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
			holder.checked = (CheckBox) view.findViewById(R.id.purchasedCheckbox);
			holder.nameIndex = cursor.getColumnIndexOrThrow(ChoiceTable.COLUMN_NAME);
			holder.checkedIndex = cursor.getColumnIndexOrThrow(ChoiceTable.COLUMN_CHECKED);
			holder.idIndex = cursor.getColumnIndexOrThrow(ChoiceTable.COLUMN_ID);
			view.setTag(holder);
		}
		holder.productName.setText(cursor.getString(holder.nameIndex));
		holder.productId = cursor.getInt(holder.idIndex);
		holder.checked.setChecked(cursor.getInt(holder.checkedIndex) != 0);
		if (holder.checked.isChecked()) {
			view.setAlpha(0.6f);
		} else
			view.setAlpha(1f);
	}
	
	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		View view;
		if (isGridView)
			view = mInflater.inflate(R.layout.product_item, parent, false);
		else
			view = mInflater.inflate(R.layout.line_list_item, parent, false);
		return view;
	}
	
	public static class Holder {
		public int checkedIndex;
		public int idIndex;
		public int nameIndex;
		public int productId;
		public CheckBox checked;
		public TextView productName;
		public EditText productQuantity;
	}
}
