package com.adam.shop;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.adam.shop.database.ChoiceTable;
import com.adam.shop.database.ProductAdapter;
import com.adam.shop.database.ProductAdapter.Holder;
import com.adam.shop.database.ShopContentProvider;

public class ChooseActivity extends Activity implements LoaderCallbacks<Cursor> {
	
	private CursorAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose);
		fillData();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_choose, menu);
		return true;
	}
	
	private void fillData() {
		getLoaderManager().initLoader(0, null, this);
		adapter = new ProductAdapter(this, null, 0);
		GridView grid = (GridView) findViewById(R.id.grid);
		grid.setAdapter(adapter);
	}
	
	public void add(View view) {
		EditText editText = (EditText) findViewById(R.id.addText);
		String name = editText.getText().toString();
		if (TextUtils.isEmpty(name)) return;
		ContentValues values = new ContentValues();
		values.put(ChoiceTable.COLUMN_NAME, name);
		getContentResolver().insert(ShopContentProvider.CONTENT_URI, values);
		editText.setText("");
	}
	
	public void remove(View view) {
		RelativeLayout rl = (RelativeLayout) view.getParent();
		Holder holder = (Holder) rl.getTag();
		Uri uri = Uri.parse(ShopContentProvider.CONTENT_URI + "/" + holder.productId);
		getContentResolver().delete(uri, null, null);
	}
	
	public void checkBoxCheck(View view) {
		CheckBox checkBox = (CheckBox) view;
		RelativeLayout rl = (RelativeLayout) view.getParent();
		Holder holder = (Holder) rl.getTag();
		Uri uri = Uri.parse(ShopContentProvider.CONTENT_URI + "/" + holder.productId);
		ContentValues values = new ContentValues();
		values.put(ChoiceTable.COLUMN_CHECKED, checkBox.isChecked());
		getContentResolver().update(uri, values, null, null);
	}
	
	// Creates a new loader after the initLoader () call
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { ChoiceTable.COLUMN_ID, ChoiceTable.COLUMN_NAME,
				ChoiceTable.COLUMN_QUANTITY, ChoiceTable.COLUMN_CHECKED };
		String checked = ChoiceTable.COLUMN_CHECKED;
		String name = ChoiceTable.COLUMN_NAME;
		CursorLoader cursorLoader = new CursorLoader(this, ShopContentProvider.CONTENT_URI,
				projection, null, null, checked + ", UPPER(" + name + ")," + name);
		return cursorLoader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		adapter.swapCursor(null);
	}
	
}
