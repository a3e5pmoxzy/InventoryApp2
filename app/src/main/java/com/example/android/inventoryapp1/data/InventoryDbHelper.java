package com.example.android.inventoryapp1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp1.data.InventoryContract.InventoryEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "db_warehouse_storage_mng";
    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTORY_ASSETS_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + "("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_PRICE + " DECIMAL NOT NULL, "
                + InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_NAME + " TEXT, "
                + InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_PHONE + " INTEGER);";
        db.execSQL(SQL_CREATE_INVENTORY_ASSETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
