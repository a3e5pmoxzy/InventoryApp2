package com.example.android.inventoryapp1.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp1.R;
import com.example.android.inventoryapp1.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {
    private Context mContext;

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.cpromos_inventory_assets_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView productNameEditText = view.findViewById(R.id.edit_inventory_assets_product_name);
        TextView productPriceEditText = view.findViewById(R.id.edit_inventory_assets_product_price);
        final TextView productQuantityEditText = view.findViewById(R.id.edit_inventory_assets_product_quantity);
        int productNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_NAME);
        int productPriceColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_PRICE);
        int productQuantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_QUANTITY);
        String productNameString = cursor.getString(productNameColumnIndex).toString();
        String productPriceString = cursor.getString(productPriceColumnIndex).toString();
        final String productQuantityString = cursor.getString(productQuantityColumnIndex).toString();
        final int currentProductQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_QUANTITY));

        TextView btn_cta_buy_item = view.findViewById(R.id.btn_cta_buy_item);
        productNameEditText.setText(productNameString);
        productPriceEditText.setText(productPriceString);
        productQuantityEditText.setText(productQuantityString);
        final int path_inventory_assets_id = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        btn_cta_buy_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentProductQuantity > 0) {
                    int productQuantity = currentProductQuantity - 1;
                    Uri currentQuantityUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, path_inventory_assets_id);
                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_QUANTITY, productQuantity);
                    mContext.getContentResolver().update(currentQuantityUri, values, null, null);
                }
            }
        });

    }
}

