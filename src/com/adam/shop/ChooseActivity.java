package com.adam.shop;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.SearchManager;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.adam.shop.database.ChoiceTable;
import com.adam.shop.database.ProductAdapter;
import com.adam.shop.database.ProductAdapter.Holder;
import com.adam.shop.database.ProductTable;
import com.adam.shop.database.ShopContentProvider;
import com.fortysevendeg.android.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.android.swipelistview.SwipeListView;

public class ChooseActivity extends ListActivity implements LoaderCallbacks<Cursor> {
    private CursorAdapter adapter;
    private final boolean isGridView = false;
    private SwipeListView listView;
    private SwipeListView archivedListView;
    public static final String TAG = "ChooseActivity";

    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
        getListView().setItemChecked(position, true);
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isGridView) setContentView(R.layout.activity_choose);
        else setContentView(R.layout.line_list);
        listView = (SwipeListView) getListView();

        Log.d(TAG, "Starting Activity");
        fillData();
        setListListener(listView);
        handleIntent(getIntent());
    }

    private void setListListener(final SwipeListView listView) {
        listView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onDismiss(final int position) {
                View view = adapter.getView(position, null, null);
                dismiss(view);
            }

//            @Override       TODO: make the shit work.
//            public void onClickFrontView(final int position) {
//                final View view = listView.findViewByPosition(position);
//                final EditText quantity = (EditText) view.findViewById(R.id.productQuantity);
//                quantity.requestFocus();
//                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.showSoftInput(quantity, 0);
//                quantity.selectAll();
//                super.onClickFrontView(position);
//            }
        });
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        handleIntent(intent);
    }

    private void handleIntent(final Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // handles a click on a search suggestion
            Uri item = intent.getData();
            add(item);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            final String query = intent.getStringExtra(SearchManager.QUERY);
//            final Uri uri = ShopContentProvider.PRODUCTS_URI;
//            Cursor c = getContentResolver().query(uri, null, null, new String[]{query}, null);
            add(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        // Get the SearchView and set the searchable configuration TODO fix the inconsistent search bar.
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget;
        searchView.setSubmitButtonEnabled(true); // enable submit
        return true;
    }

    private void fillData() {
        Log.d(TAG, "Filling data");
        getLoaderManager().initLoader(0, null, this);
        adapter = new ProductAdapter(this, null, 0, isGridView);
        AbsListView view;
        if (isGridView) view = (GridView) findViewById(R.id.grid);
        else view = (ListView) findViewById(android.R.id.list);
        view.setAdapter(adapter);
    }

    /**
     * Adds the product to the list
     *
     * @param name - name of the product we want to add to the list
     */
    private void add(final String name) {
        Log.d(TAG, "trying to add product: " + name);
        if (TextUtils.isEmpty(name)) return;
        final ContentValues values = new ContentValues();
        values.put(ChoiceTable.COLUMN_NAME, name);
        getContentResolver().insert(ShopContentProvider.CHOICES_URI, values);
        final ContentValues ftsValues = new ContentValues();
        ftsValues.put(ProductTable.NAME, name); //insert to fts
        getContentResolver().insert(ShopContentProvider.PRODUCTS_URI, ftsValues);
        Log.d(TAG, "added product: " + name);
        adapter.notifyDataSetChanged();
    }

    private void add(final Uri itemUri) {
        Log.d(TAG, "trying to add product by id: " + itemUri);
        ContentValues values = new ContentValues();
        Cursor c = getContentResolver().query(itemUri, null, null, null, null);
        int quantity = c.getInt(c.getColumnIndex(ProductTable.QUANTITY));
        quantity = quantity == 0 ? 1 : quantity;
        values.put(ProductTable.QUANTITY, quantity);
        getContentResolver().update(itemUri, values, null, null);
        adapter.notifyDataSetChanged();
    }


    /**
     * remove product from the list
     *
     * @param view - the RelativeLayout of the item we wish to remove
     */
    private void dismiss(final View view) {
        final Holder holder = (Holder) view.getTag();
        final Uri uri = Uri.parse(ShopContentProvider.CHOICES_URI + "/" + holder.productId);
        Log.d(TAG, "deleting product: " + holder.productName);
        getContentResolver().delete(uri, null, null);
        adapter.notifyDataSetChanged();
    }

    /**
     * reverses the checked state of the view
     *
     * @param view
     */
    private void archive(final View view) {
        final Holder holder = (Holder) view.getTag();
        final Uri uri = Uri.parse(ShopContentProvider.CHOICES_URI + "/" + holder.productId);
        final ContentValues values = new ContentValues();
        values.put(ChoiceTable.COLUMN_CHECKED, !holder.checked);
        getContentResolver().update(uri, values, null, null);
    }

    public void undo(View view) {
        listView.closeOpenedItems();
    }

    public void checkBoxCheck(final View view) {
        int pos = listView.getCheckedItemPosition();
        listView.setItemChecked(pos, true);
    }

    public void checkBoxChecked(final View view) {
        final CheckBox checkBox = (CheckBox) view;
        final RelativeLayout rl = (RelativeLayout) view.getParent();
        final Holder holder = (Holder) rl.getTag();
        final Uri uri = Uri.parse(ShopContentProvider.CHOICES_URI + "/" + holder.productId);
        final ContentValues values = new ContentValues();
        values.put(ChoiceTable.COLUMN_CHECKED, checkBox.isChecked());
        getContentResolver().update(uri, values, null, null);
    }

    // Creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        Log.d(TAG, "creating Loader");
        final String[] projection = {ChoiceTable.COLUMN_ID, ChoiceTable.COLUMN_NAME, ChoiceTable.COLUMN_QUANTITY, ChoiceTable.COLUMN_CHECKED};
        final String checked = ChoiceTable.COLUMN_CHECKED;
        final String name = ChoiceTable.COLUMN_NAME;
        return new CursorLoader(this, ShopContentProvider.CHOICES_URI, projection, null, null, checked + ", UPPER(" + name + ")," + name);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        Log.d(TAG, "resetting Loader");
        adapter.swapCursor(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                return true;
            default:
                return false;
        }
    }
}
