package com.adam.shop;

import android.app.ListActivity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.SearchManager;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.adam.shop.database.ChoiceTable;
import com.adam.shop.database.ProductAdapter;
import com.adam.shop.database.ProductAdapter.Holder;
import com.adam.shop.database.ShopContentProvider;
import com.fortysevendeg.android.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.android.swipelistview.SwipeListView;

public class ChooseActivity extends ListActivity implements LoaderCallbacks<Cursor> {
    private CursorAdapter adapter;
    private final boolean isGridView = false;
    private SwipeListView listView;
    private SwipeListView archivedListView;

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

        //setChoiceMode();

        fillData();
        setListListener(listView);
        handleIntent(getIntent());

        // Activate animations for changes in the list
        // final ListView view = (ListView) findViewById(R.id.lines);
        // final LayoutTransition transition = view.getLayoutTransition();
        // transition.enableTransitionType(LayoutTransition.CHANGING);
    }

    private void setListListener(final SwipeListView listView) {
        listView.setSwipeListViewListener(new BaseSwipeListViewListener(){
            @Override
            public void onDismiss(final int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions){
                    View view = adapter.getView(position, null, null);
                    archive(view);
                }
                listView.invalidate();
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

    private void setChoiceMode() {
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new MultiChoiceModeListener(listView));
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
            // handles a click on a search suggestion; launches activity to show word
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            final String query = intent.getStringExtra(SearchManager.QUERY);
            add(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_choose, menu);
        // Get the SearchView and set the searchable configuration
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_add).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName("com.adam.shop", "com.adam.shop.ChooseActivity")));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget;
        searchView.setSubmitButtonEnabled(true); // enable submit
        // expand it by default
        return true;
    }

    private void fillData() {
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
        if (TextUtils.isEmpty(name)) return;
        final ContentValues values = new ContentValues();
        values.put(ChoiceTable.COLUMN_NAME, name);
        getContentResolver().insert(ShopContentProvider.CONTENT_URI, values);
        listView.invalidate();
    }

    /**
     * remove product from the list
     *
     * @param view - the RelativeLayout of the item we wish to remove
     */
    private void remove(final View view) {
        final Holder holder = (Holder) view.getTag();
        final Uri uri = Uri.parse(ShopContentProvider.CONTENT_URI + "/" + holder.productId);
        getContentResolver().delete(uri, null, null);
    }

    /**
     * reverses the checked state of the view
     * @param view
     */
    private void archive(final View view){
        final Holder holder = (Holder) view.getTag();
        final Uri uri = Uri.parse(ShopContentProvider.CONTENT_URI + "/" + holder.productId);
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
        final Uri uri = Uri.parse(ShopContentProvider.CONTENT_URI + "/" + holder.productId);
        final ContentValues values = new ContentValues();
        values.put(ChoiceTable.COLUMN_CHECKED, checkBox.isChecked());
        getContentResolver().update(uri, values, null, null);
    }

    // Creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        final String[] projection = {ChoiceTable.COLUMN_ID, ChoiceTable.COLUMN_NAME, ChoiceTable.COLUMN_QUANTITY, ChoiceTable.COLUMN_CHECKED};
        final String checked = ChoiceTable.COLUMN_CHECKED;
        final String name = ChoiceTable.COLUMN_NAME;
        return new CursorLoader(this, ShopContentProvider.CONTENT_URI, projection, null, null, checked + ", UPPER(" + name + ")," + name);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }
}
