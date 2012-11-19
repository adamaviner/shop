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
	
	public ProductAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
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
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View view = mInflater.inflate(R.layout.product_item, parent, false);
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
