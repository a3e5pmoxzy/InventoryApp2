package com.example.android.inventoryapp1;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp1.data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;
    private static final int REQUEST_PHONE_CALL = 0;
    private Uri mCurrentInventoryItemUri;
    private EditText mProductNameEditText;
    private EditText mProductPriceEditText;
    private EditText mProductQuantityEditText;
    private EditText mLogisticsSupplierNameEditText;
    private EditText mLogisticsSupplierPhoneEditText;
    private boolean mInventoryItemHasChanged = false;
    private ImageView btnCtaRequestCall;
    private ImageView btnCtaIncQuantity;
    private ImageView btnCtaDecQuantity;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventoryItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        btnCtaRequestCall = findViewById(R.id.btn_cta_inventory_logistics_supplier_phone);
        btnCtaIncQuantity = findViewById(R.id.btn_cta_inventory_assets_plus_quantity);
        btnCtaDecQuantity = findViewById(R.id.btn_cta_inventory_assets_minus_quantity);
        btnCtaRequestCall = new ImageView(this);
        btnCtaDecQuantity = new ImageView(this);
        btnCtaDecQuantity = new ImageView(this);
        btnCtaRequestCall.setOnClickListener(new BtnMgmClick());
        btnCtaIncQuantity.setOnClickListener(new BtnMgmClick());
        btnCtaDecQuantity.setOnClickListener(new BtnMgmClick());

        //      ImageView btnCtaIncQuantity = findViewById(R.id.btn_cta_inventory_assets_plus_quantity);
//      ImageView btnCtaDecQuantity = findViewById(R.id.btn_cta_inventory_assets_minus_quantity);
//      btnCtaRequestCall.setOnClickListener(R.layout.activity_editor);
//      btnCtaIncQuanatity.setOnClickListener(R.layout.activity_editor);
//      btnCtaDecQuantity.setOnClickListener(R.layout.activity_editor);
        Intent intent = getIntent();
        mCurrentInventoryItemUri = intent.getData();

        if (mCurrentInventoryItemUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getSupportLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        mProductNameEditText = findViewById(R.id.edit_inventory_assets_product_name);
        mProductPriceEditText = findViewById(R.id.edit_inventory_assets_product_price);
        mProductQuantityEditText = findViewById(R.id.edit_inventory_assets_product_quantity);
        mLogisticsSupplierNameEditText = findViewById(R.id.edit_inventory_logistics_supplier_name);
        mLogisticsSupplierPhoneEditText = findViewById(R.id.edit_inventory_logistics_supplier_phone);
        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mLogisticsSupplierNameEditText.setOnTouchListener(mTouchListener);
        mLogisticsSupplierPhoneEditText.setOnTouchListener(mTouchListener);
    }


    private void saveInventoryItem() {
        String productNameString = mProductNameEditText.getText().toString().trim();
        String productPriceString = mProductPriceEditText.getText().toString().trim();
        String productQuantityString = mProductQuantityEditText.getText().toString().trim();
        String logisticsSupplierNameString = mLogisticsSupplierNameEditText.getText().toString().trim();
        String logisticsSupplierPhoneString = mLogisticsSupplierPhoneEditText.getText().toString().trim();
        double productPrice = 0;
        int productQuantity = 1;

        if (mCurrentInventoryItemUri == null &&
                TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(productPriceString) &&
                TextUtils.isEmpty(productQuantityString) && TextUtils.isEmpty(logisticsSupplierNameString) && TextUtils.isEmpty(logisticsSupplierPhoneString)) {
            return;
        }

        if (productNameString.equals("")) {
            Toast.makeText(this, getString(R.string.editor_insert_product_name_requested),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (mProductPriceEditText.getText().toString().equals("") || productPrice <= 0) {
            Toast.makeText(this, getString(R.string.editor_insert_product_price_requested),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (mProductQuantityEditText.getText().toString().equals("") || productQuantity < 0) {
            Toast.makeText(this, getString(R.string.editor_insert_product_quantity_requested),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (logisticsSupplierNameString.equals("")) {
            Toast.makeText(this, getString(R.string.editor_insert_logistics_supplier_name_requested),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        int logisticsSupplierPhone = 0;
        if (mLogisticsSupplierPhoneEditText.getText().toString().equals("") || logisticsSupplierPhone <= 0) {
            Toast.makeText(this, getString(R.string.editor_insert_logistics_supplier_phone_requested),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_NAME, productNameString);
        values.put(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_PRICE, productPriceString);
        values.put(InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_NAME, logisticsSupplierNameString);
        values.put(InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_PHONE, logisticsSupplierPhoneString);

        if (!TextUtils.isEmpty(productQuantityString)) {
            productQuantity = Integer.parseInt(productQuantityString);
        }
        values.put(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_QUANTITY, productQuantity);

        if (!TextUtils.isEmpty(logisticsSupplierPhoneString)) {
            logisticsSupplierPhone = Integer.parseInt(logisticsSupplierPhoneString);
        }
        values.put(InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_PHONE, logisticsSupplierPhone);

        if (mCurrentInventoryItemUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentInventoryItemUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentInventoryItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveInventoryItem();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mInventoryItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mInventoryItemHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
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
                mCurrentInventoryItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int productNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_INVENTORY_ASSETS_PRODUCT_QUANTITY);
            int logisticsSupplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_NAME);
            int logisticsSupplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_INVENTORY_LOGISTICS_SUPPLIER_PHONE);
            String productNameString = cursor.getString(productNameColumnIndex);
            double productPriceString = cursor.getDouble(productPriceColumnIndex);
            int productQuantityString = cursor.getInt(productQuantityColumnIndex);
            String logisticsSupplierNameString = cursor.getString(logisticsSupplierNameColumnIndex);
            int logisticsSupplierPhoneString = cursor.getInt(logisticsSupplierPhoneColumnIndex);
            mProductNameEditText.setText(productNameString);
            mProductPriceEditText.setText(Double.toString(productPriceString));
            mProductQuantityEditText.setText(Integer.toString(productQuantityString));
            mLogisticsSupplierNameEditText.setText(logisticsSupplierNameString);
            mLogisticsSupplierPhoneEditText.setText(Integer.toString(logisticsSupplierPhoneString));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mProductPriceEditText.setText("");
        mProductQuantityEditText.setText("");
        mLogisticsSupplierNameEditText.setText("");
        mLogisticsSupplierPhoneEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteInventoryItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteInventoryItem() {
        if (mCurrentInventoryItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryItemUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private class BtnMgmClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_cta_inventory_logistics_supplier_phone:
                    String logisticsSupplierPhone = mLogisticsSupplierPhoneEditText.getText().toString();
                    if (!logisticsSupplierPhone.equals("")) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", logisticsSupplierPhone, null));
                        if (ContextCompat.checkSelfPermission(EditorActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(EditorActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                        } else {
                            startActivity(intent);
                        }
                    } else
                        Toast.makeText(EditorActivity.this, getString(R.string.editor_insert_logistics_supplier_phone_requested),
                                Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_cta_inventory_assets_plus_quantity:
                    if (!mProductQuantityEditText.getText().toString().equals("")) {
                        Integer productQuantity = Integer.parseInt(mProductQuantityEditText.getText().toString());
                        productQuantity++;
                        mProductQuantityEditText.setText(String.valueOf(productQuantity));
                    }
                case R.id.btn_cta_inventory_assets_minus_quantity:
                    if (!mProductQuantityEditText.getText().toString().equals("")) {
                        Integer productQuantity = Integer.parseInt(mProductQuantityEditText.getText().toString());
                        if (productQuantity > 0) productQuantity--;
                        mProductQuantityEditText.setText(String.valueOf(productQuantity));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}