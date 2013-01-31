package com.adam.shop;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

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
		handleIntent(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// Because this activity has set launchMode="singleTop", the system
		// calls this method
		// to deliver the intent if this activity is currently the foreground
		// activity when
		// invoked again (when the user executes a search from this activity, we
		// don't create
		// a new instance of this activity, so the system delivers the search
		// intent here)
		handleIntent(intent);
	}
	
	private void handleIntent(Intent intent) {
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			// handles a click on a search suggestion; launches activity to show
			// word
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// handles a search query
			String query = intent.getStringExtra(SearchManager.QUERY);
			add(query);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_choose, menu);
		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_add).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(
				"com.adam.shop", "com.adam.shop.ChooseActivity")));
		searchView.setIconifiedByDefault(false); // Do not iconify the widget;
		searchView.setSubmitButtonEnabled(true); // enable submit
		// expand it by default
		
		return true;
	}
	
	private void fillData() {
		getLoaderManager().initLoader(0, null, this);
		adapter = new ProductAdapter(this, null, 0);
		GridView grid = (GridView) findViewById(R.id.grid);
		grid.setAdapter(adapter);
	}
	
	/**
	 * Adds the product to the list
	 * 
	 * @param name
	 *            - name of the product we want to add to the list
	 * 
	 */
	private void add(String name) {
		if (TextUtils.isEmpty(name)) return;
		ContentValues values = new ContentValues();
		values.put(ChoiceTable.COLUMN_NAME, name);
		getContentResolver().insert(ShopContentProvider.CONTENT_URI, values);
	}
	
	/**
	 * removes the product from the list
	 * 
	 * @param view
	 */
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
