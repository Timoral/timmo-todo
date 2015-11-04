package xyz.timmo.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class ItemDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "todo";
    private static final String TABLE_ITEMS = "items";

    private static final String KEY_ID = "id";
    private static final String ITEM = "item";
    private static final String CHECKED = "checked";

//    private Context sContext;

    public ItemDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        sContext = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CHECKLISTS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                ITEM + " TEXT," +
                CHECKED + " INTEGER" +
                ")";
        db.execSQL(CREATE_CHECKLISTS_TABLE);
    }

    // Upgrading Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    // Add New checklistItem
    void addItem(Item checklistItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, checklistItem.getID());
        values.put(ITEM, checklistItem.getItem());
        values.put(CHECKED, checklistItem.getChecked());

        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    // Getting single checklist
    Item getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ITEMS, new String[]{KEY_ID, ITEM, CHECKED}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Item checklistItem = new Item
                    (Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)));
            cursor.close();
            db.close();
            // return checklistItem
            return checklistItem;
        } else {
            return null;
        }
    }

    // Getting All Checklists
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        // Select All Query
        //String selectQuery = "SELECT  * FROM " + TABLE_ITEMS;

//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(sContext);
//
//        String order;
//        // TODO Add Sorting to preference
//        switch (sharedPreferences.getString("item_sorting", "0")) {
//            case "0":
//                order = KEY_ID + " DESC";
//                break;
//            case "1":
//                order = KEY_ID + " ASC";
//                break;
//            case "2":
//                order = ITEM + " COLLATE NOCASE DESC";
//                break;
//            case "3":
//                order = ITEM + " COLLATE NOCASE ASC";
//                break;
//            default:
//                order = KEY_ID + " DESC";
//                break;
//        }


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS,
                new String[]{KEY_ID, ITEM, CHECKED},
                null, null, null, null, null); //order (end)

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                Log.d("cursor", cursor.getString(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2));
                item.setID(Integer.parseInt(cursor.getString(0)));
                item.setItem(cursor.getString(1));
                item.setChecked(Integer.parseInt(cursor.getString(2)));
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }

    public void updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ITEM, item.getItem());
        values.put(CHECKED, item.getChecked());
        db.update(TABLE_ITEMS, values, KEY_ID + " = ?", new String[]{String.valueOf(item.getID())});
        db.close();
    }

    // Deleting single item
    public void deleteItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, KEY_ID + " = ?", new String[]{String.valueOf(item.getID())});
        db.close();
    }

    // Getting item count
    public int getItemCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

}