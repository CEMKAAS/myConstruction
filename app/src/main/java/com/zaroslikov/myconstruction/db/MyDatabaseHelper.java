package com.zaroslikov.myconstruction.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context context;

    //TODO Включаем внешний ключ

    public MyDatabaseHelper(Context context) {
        super(context, MyConstanta.DB_NAME, null, MyConstanta.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MyConstanta.TABLE_STRUCTURE_PROJECT);
        db.execSQL(MyConstanta.TABLE_STRUCTURE_PRODUCT);
        db.execSQL(MyConstanta.TABLE_STRUCTURE_ADD);
        db.execSQL(MyConstanta.TABLE_STRUCTURE_SALE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MyDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_PROJECT");
        db.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_PRODUCT");
        db.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_ADD");
        db.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_SALE");
        onCreate(db);
    }

    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(MyConstanta.DB_NAME);
    }


    public Cursor readProject() {
        String query = "SELECT * FROM " + MyConstanta.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readProduct() {
        String query = "SELECT * FROM " + MyConstanta.TABLE_NAME_PRODUCT;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor product (String nameTable,String nameCount, String nameProduct) {

        String query = "SELECT * FROM " + MyConstanta.TABLE_NAME_PRODUCT;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;


        SELECT *
                FROM
        KEY_COLUMN_USAGE
                WHERE
        REFERENCED_TABLE_NAME = 'X'
        AND REFERENCED_COLUMN_NAME = 'X_id';
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = null;
//        if (db != null) {
//            cursor = db.query(nameTable,
//                    null,
//                    null, null,
//                    null, null, null);
//        }
//        return cursor;
    }

}
