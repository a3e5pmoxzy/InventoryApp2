package com.example.android.inventoryapp1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventoryapp1.R;
import com.example.android.inventoryapp1.data.InventoryContract.InventoryEntry;

public class InventoryProvider extends ContentProvider {
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    private static final int TBL_INVENTORY = 100;
    private static final int PATH_INVENTORY_ASSETS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY_ASSETS, TBL_INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY_ASSETS + "/#", PATH_INVENTORY_ASSETS_ID);
    }

    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case TBL_INVENTORY:
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PATH_INVENTORY_ASSETS_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TBL_INVENTORY:
                return insertInventoryItem(uri, contentValues);
            default:
                throw new IllegalArgumentException(R.string.msg_log_illegalstateexception3 + "" + uri);
        }
    }

    private Uri insertInventoryItem(Uri uri, ContentValues values) {
        String productName = values.getAsString(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_NAME);

        if (productName == null) {
            throw new IllegalArgumentException("Product requires a name.");
        }

        Double productPrice = values.getAsDouble(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_PRICE);

        if (productPrice != null && productPrice < 0) {
            throw new IllegalArgumentException("Product requires valid positive price.");
        }

        Integer productQuantity = values.getAsInteger(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_QUANTITY);

        if (productQuantity == null || productQuantity <= 0) {
            throw new IllegalArgumentException("Product requires valid quantity.");
        }

        String logisticsSupplierName = values.getAsString(InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_NAME);

        if (logisticsSupplierName == null) {
            throw new IllegalArgumentException("Supplier requires a name.");
        }

        Integer logisticsSupplierPhone = values.getAsInteger(InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_PHONE);

        if (logisticsSupplierPhone == null || logisticsSupplierPhone < 0) {
            throw new IllegalArgumentException("Supplier phone requires valid supplier phone (Fill in a positive value.).");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, R.string.msg_log_insert_row_failed1 + " " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TBL_INVENTORY:
                return updateInventoryItem(uri, contentValues, selection, selectionArgs);
            case PATH_INVENTORY_ASSETS_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventoryItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateInventoryItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_NAME)) {
            String productName = values.getAsString(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException("Inventory asset requires a name.");
            }
        }

        if (values.containsKey(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_PRICE)) {
            Double productPrice = values.getAsDouble(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_PRICE);
            if (productPrice == null || productPrice < 0) {
                throw new IllegalArgumentException("Inventory assets requires valid price.");
            }
        }

        if (values.containsKey(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_QUANTITY)) {
            Integer productQuantity = values.getAsInteger(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_QUANTITY);
            if (productQuantity == null || productQuantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity.");
            }
        }

        if (values.containsKey(InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_NAME)) {
            String logisticsSupplierName = values.getAsString(InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_NAME);
            if (logisticsSupplierName == null) {
                throw new IllegalArgumentException("Inventory asset requires a name.");
            }
        }

        if (values.containsKey(InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_PHONE)) {
            Integer logisticsSupplierPhone = values.getAsInteger(InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_PHONE);
            if (logisticsSupplierPhone == null || logisticsSupplierPhone < 0) {
                throw new IllegalArgumentException("Supplier requires valid phone.");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);

        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TBL_INVENTORY:
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PATH_INVENTORY_ASSETS_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TBL_INVENTORY:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case PATH_INVENTORY_ASSETS_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(R.string.msg_log_illegalstateexception1 + " " + uri + " " + R.string.msg_log_illegalstateexception2 + match);
        }
    }
}