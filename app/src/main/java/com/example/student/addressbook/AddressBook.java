package com.example.student.addressbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.graphics.Color;


public class AddressBook extends AppCompatActivity {
    // used as a key in key-value pair that's passed between activities
    public static final String ROW_ID = "row_id";

    private ListView contactListView;

    // adapter for populating the list view
    private CursorAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);
        contactListView = (ListView) findViewById(R.id.listView);
        contactListView.setOnItemClickListener(viewContactListener);
        contactListView.setBackgroundColor(Color.BLACK);
        // display message on empty list
        TextView emptyText = (TextView) View.inflate(this, R.layout.contact_list_empty_item, null);
        emptyText.setVisibility(View.GONE);

        ((ViewGroup)contactListView.getParent()).addView(emptyText);

        // map each contacts' name to a text view in the listview layout
        String[] from = new String[] {"name"};
        int[] to = new int[] {R.id.contactTextView};
        contactAdapter = new SimpleCursorAdapter(AddressBook.this,
                R.layout.contact_list_item, null, from, to, 0);

        contactListView.setAdapter(contactAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // create a new GetContactsTask and execute it
        // on background thread gets all rows and
        // populates the list view
        new GetContactsTask().execute((Object[]) null);
    }

    @Override
    protected void onStop() {
        Cursor cursor = contactAdapter.getCursor();
        // if not null deactivate it
        if (cursor != null) {
            cursor.close();
        }
        contactAdapter.changeCursor(null);
        super.onStop();
    }

    // performs database query outside of GUI thread
    private class GetContactsTask extends AsyncTask<Object, Object, Cursor> {
        DatabaseConnector databaseConnector = new DatabaseConnector(AddressBook.this);

        // perform database access
        // runs on the background thread
        @Override
        protected Cursor doInBackground(Object... params) {
            databaseConnector.open();
            return databaseConnector.getAllContacts();
        }
        // use the cursor returned from doInBackground
        // runs on the main UI thread
        @Override
        protected void onPostExecute(Cursor result) {
            contactAdapter.changeCursor(result);
            databaseConnector.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_address_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // create a new intent to launch AddEditContactActivity
        Intent addNewContact =
                new Intent(AddressBook.this, AddEditContact.class);
        startActivity(addNewContact);

        return super.onOptionsItemSelected(item);
    }

    private OnItemClickListener viewContactListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // create an Intent to launch the ViewContact Activity
            Intent viewContact =
                    new Intent(AddressBook.this, ViewContact.class);
            // pass the selected contact's row ID as an extra to the Intent
            viewContact.putExtra(ROW_ID, id);
            startActivity(viewContact);
        }
    };
}
