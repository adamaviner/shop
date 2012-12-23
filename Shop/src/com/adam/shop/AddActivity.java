package com.adam.shop;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

/**
 * This is where we search the database for suggestions for the user to add
 * products to his list.
 * 
 * @author token
 * 
 */
public class AddActivity extends ListActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		
		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		handleIntent(intent);
	}
	
	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// handles a search query
			String query = intent.getStringExtra(SearchManager.QUERY);
			getAddSuggestions(query);
		}
	}
	
	// Sets the adapter to the adapter returned from the database
	private void getAddSuggestions(String query) {
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_add, menu);
		return true;
	}
	
}
