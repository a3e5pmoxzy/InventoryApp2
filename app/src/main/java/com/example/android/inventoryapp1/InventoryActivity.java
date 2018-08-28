package com.example.android.inventoryapp1;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp1.data.InventoryContract.InventoryEntry;
import com.example.android.inventoryapp1.data.InventoryCursorAdapter;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int INVENTORY_LOADER = 0;
    InventoryCursorAdapter mCursorAdpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView inventoryItemListView = findViewById(R.id.sec_cpromos_inventory_assets_list);
        View emptyView = findViewById(R.id.empty_view);
        inventoryItemListView.setEmptyView(emptyView);
        mCursorAdpater = new InventoryCursorAdapter(this, null);
        inventoryItemListView.setAdapter(mCursorAdpater);
        inventoryItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                Uri currentInventoryItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                intent.setData(currentInventoryItemUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    public void insertInventoryItem() {
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_NAME, "Headphones");
        values.put(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_PRICE, 9.99);
        values.put(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_QUANTITY, 350);
        values.put(InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_NAME, "Sony");
        values.put(InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_PHONE, 386590122);
        Log.v(getString(R.string.msg_log_insert_row_successful1), getString(R.string.msg_log_insert_row_successful2));
        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(this, getString(R.string.inventory_insert_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.inventory_insert_product_succesful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAllInventoryItems() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Log.v(getString(R.string.msg_log_delete_row_successful1), rowsDeleted + getString(R.string.msg_log_delete_row_successful2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertInventoryItem();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllInventoryItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_NAME,
                InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_PRICE,
                InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_QUANTITY,
                InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_NAME,
                InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_PHONE};

        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdpater.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdpater.swapCursor(null);
    }
}