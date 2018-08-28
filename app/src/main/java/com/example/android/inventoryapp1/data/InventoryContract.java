package com.example.android.inventoryapp1.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY_ASSETS = "tbl_warehouse_storage_mng";
    private InventoryContract() {
    }

    public static abstract class InventoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY_ASSETS);
        public static final String TABLE_NAME = "tbl_warehouse_storage_mng";
        public static final String _ID = BaseColumns._ID;
        public static final String COL_INVENTORY_ASSETS_PRODUCT_NAME = "assets_product_name";
        public static final String COL_INVENTORY_ASSETS_PRODUCT_PRICE = "assets_product_price";
        public static final String COL_INVENTORY_ASSETS_PRODUCT_QUANTITY = "assets_product_quantity";
        public static final String COL_INVENTORY_LOGISTICS_SUPPLIER_NAME = "logistics__supplier_name";
        public static final String COL_INVENTORY_LOGISTICS_SUPPLIER_PHONE = "logistics_supplier_phone";
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY_ASSETS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY_ASSETS;
    }
}