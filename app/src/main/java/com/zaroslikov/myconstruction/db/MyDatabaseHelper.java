package com.zaroslikov.myconstruction.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context context;

    //TODO Включаем внешний ключ

    public MyDatabaseHelper(Context context) {
        super(context, MyConstanta.DB_NAME, null, MyConstanta.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON");
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
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + MyConstanta.TABLE_NAME);
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

//      public Cursor readProductJoin(int propertyId){
//        String query = "SELECT * FROM " +  MyConstanta.TABLE_NAME + " INNER JOIN " + MyConstanta.TABLE_NAME_PRODUCT +
//                " ON  MyProject.productID=МyProduct._id WHERE MyProduct._id=?";
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = null;
//
//
//        db.rawQuery(query, new String[]{String.valueOf(propertyId)});
//
//        return cursor;
//    }

//    public Cursor readProductJoin(int propertyId){
//        String query = "SELECT * FROM " +  MyConstanta.TABLE_NAME + " INNER JOIN " + MyConstanta.TABLE_NAME_PRODUCT +
//                " ON  MyProduct.productID=MyProject._id";
//        SQLiteDatabase db = this.getReadableDatabase();
//
//
//        Cursor cursor = null;
//        if (db != null) {
//            cursor = db.rawQuery(query, null);
//        }
//
//        return cursor;
//    }

//    public Cursor readProductJoin(int propertyId){
//        String query = "SELECT * FROM "+ MyConstanta.TABLE_NAME + " s "
//                + " JOIN " +  MyConstanta.TABLE_NAME_PRODUCT + " p "
//                + "ON s." + MyConstanta._ID + " =p." + MyConstanta.FOREIGNKEY + " WHERE "
//                +  "p." + MyConstanta.FOREIGNKEY  + "=?";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = null;
//        if (db != null) {
////            db.rawQuery(query,null);
//            db.rawQuery(query, new String[]{String.valueOf(propertyId)});
//        }
//
//        return cursor;
//    }
//    public Cursor readProductJoin(int propertyId){
//        String query = "SELECT p." + MyConstanta.TITLEPRODUCT + ", p." + MyConstanta.SUFFIXPRODUCT  + " FROM " + MyConstanta.TABLE_NAME + " s "
//                + " JOIN " +  MyConstanta.TABLE_NAME_PRODUCT + " p "
//                + "ON p." + MyConstanta.FOREIGNKEY + " =s." + MyConstanta._ID;
//
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = null;
//        if (db != null) {
//            db.rawQuery(query,null);
////            db.rawQuery(query, new String[]{String.valueOf(propertyId)});
//        }
//
//        return cursor;
//    }

//    public Cursor productKey (String nameTable,String nameCount, String nameProduct) {
//
//        String query = "SELECT * FROM MyProject";
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = null;
//        if (db != null) {
//            cursor = db.rawQuery(query, null);
//        }
//        return cursor;
//    }


    public Cursor product (String nameTable,String nameCount, String nameProduct) {

        String query = "SELECT * FROM " + MyConstanta.TABLE_NAME_PRODUCT;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void insertToDbProject(String title, String date, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyConstanta.TITLEPROJECT, title);
        cv.put(MyConstanta.DATEPROJECT, date);
        cv.put(MyConstanta.PICTUREROJECT, 0);
        cv.put(MyConstanta.STATUSPROJECT,status);
        db.insert(MyConstanta.TABLE_NAME, null, cv);
    }

    public void insertToDbProduct() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyConstanta.TITLEPRODUCT, "Гвозди");
        cv.put(MyConstanta.SUFFIXPRODUCT, "sd");
        cv.put(MyConstanta.FOREIGNKEY, 1);
        db.insert(MyConstanta.TABLE_NAME_PRODUCT, null, cv);
    }

    public Cursor readProductJoin(int propertyId){
        String query = "SELECT s."+ MyConstanta.TITLEPROJECT+
                ", p." + MyConstanta.TITLEPRODUCT + ", p."+ MyConstanta.TITLEPRODUCT +
                " FROM " + MyConstanta.TABLE_NAME + " s " +
                "JOIN " + MyConstanta.TABLE_NAME_PRODUCT + " p " +
                "ON s." + MyConstanta._ID  + " = " + " p." + MyConstanta.FOREIGNKEY +
                " WHERE s." + MyConstanta._ID + "=?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {

            cursor =  db.rawQuery(query, new String[]{String.valueOf(propertyId)});
        }

        return cursor;
    }
    public Cursor seachProduct(String productName){
        String query = "SELECT s." + MyConstanta.TITLEPRODUCT +
                " FROM " + MyConstanta.TABLE_NAME_PRODUCT + " s "+
                "Where s." + MyConstanta.TABLE_NAME_PRODUCT + "=?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor =  db.rawQuery(query, new String[]{productName});
        }

        return cursor;
    }

    public void insertToDbProduct2() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyConstanta.TITLEPRODUCT, "Гвозди");
        cv.put(MyConstanta.SUFFIXPRODUCT, "sd");
        cv.put(MyConstanta.FOREIGNKEY ,2);
        db.insert(MyConstanta.TABLE_NAME_PRODUCT, null, cv);
    }

}
