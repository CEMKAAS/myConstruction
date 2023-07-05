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
        db.execSQL(MyConstanta.TABLE_STRUCTURE_PRODUCTPROJECT);
        db.execSQL(MyConstanta.TABLE_STRUCTURE_ADD);
        db.execSQL(MyConstanta.TABLE_STRUCTURE_WRITEOFF);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MyDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_PROJECT");
        db.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_PRODUCT");
        db.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_PRODUCTPROJECT");
        db.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_ADD");
        db.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_WRITEOFF");
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
        cv.put(MyConstanta.DATEBEGINPROJECT, date);
        cv.put(MyConstanta.DATEFINALPROJECT, date);
        cv.put(MyConstanta.PICTUREROJECT, 0);
        cv.put(MyConstanta.STATUSPROJECT,status);
        db.insert(MyConstanta.TABLE_NAME, null, cv);
    }

    public long insertToDbProduct(String name, String suffix) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyConstanta.TITLEPRODUCT, name);
        cv.put(MyConstanta.SUFFIX, suffix);
        long id = db.insert(MyConstanta.TABLE_NAME_PRODUCT, null, cv);
        return id;
    }

    public long insertToDbProjectProduct( int idProject, int idProduct) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyConstanta.IDPROJECT, idProject);
        cv.put(MyConstanta.IDPRODUCT, idProduct);
        long id = db.insert(MyConstanta.TABLE_NAME_PROJECT_PRODUCT, null, cv);
        return id;
    }

    public void insertToDbProductAdd(double count, String category, double price, String date, int idPP) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyConstanta.QUANTITY,count);
        cv.put(MyConstanta.CATEGORY, category);
        cv.put(MyConstanta.PRICE, price);
        cv.put(MyConstanta.DATE, date);
        cv.put(MyConstanta.IDPP, idPP);
        db.insert(MyConstanta.TABLE_NAME_ADD, null, cv);
    }

    public void insertToDbProductWriteOff(double count, String category, String date, int idPP) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyConstanta.QUANTITY,count);
        cv.put(MyConstanta.CATEGORY, category);
        cv.put(MyConstanta.DATE, date);
        cv.put(MyConstanta.IDPP, idPP);
        db.insert(MyConstanta.TABLE_NAME_WRITEOFF, null, cv);
    }
//    public Cursor readProductJoin(int propertyId){
//        String query = "SELECT s."+ MyConstanta.TITLEPROJECT+
//                ", p." + MyConstanta.TITLEPRODUCT + ", p."+ MyConstanta.TITLEPRODUCT +
//                " FROM " + MyConstanta.TABLE_NAME + " s " +
//                "JOIN " + MyConstanta.TABLE_NAME_PRODUCT + " p " +
//                "ON s." + MyConstanta._ID  + " = " + " p." + MyConstanta._ID +
//                " WHERE s." + MyConstanta._ID + "=?";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = null;
//        if (db != null) {
//
//            cursor =  db.rawQuery(query, new String[]{String.valueOf(propertyId)});
//        }
//
//        return cursor;
//    }

    public Cursor selectProductJoin(int propertyId, String productName, String tableName){
        String query = "SELECT " + MyConstanta.TITLEPRODUCT+
                ", sum(" + MyConstanta.QUANTITY + "), " + MyConstanta.SUFFIX +
                " FROM " + tableName + " ad " +
                "JOIN " + MyConstanta.TABLE_NAME_PROJECT_PRODUCT + " pp " +
                "ON pp." + MyConstanta._ID  + " = " + "ad." + MyConstanta.IDPP +

                " JOIN " + MyConstanta.TABLE_NAME_PRODUCT + " prod " +
                "ON prod." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPRODUCT +

                " JOIN " + MyConstanta.TABLE_NAME + " proj " +
                "ON proj." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPROJECT +

                " WHERE proj." + MyConstanta._ID + "=? and " + MyConstanta.TITLEPRODUCT + "=?" +
                "group by " + MyConstanta.TITLEPRODUCT + ", " + MyConstanta.SUFFIX;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {

            cursor =  db.rawQuery(query, new String[]{String.valueOf(propertyId),productName});
        }

        return cursor;
    }

//    public Cursor seachProduct(String productName){
//        String query = "SELECT s." + MyConstanta.TITLEPRODUCT +
//                " FROM " + MyConstanta.TABLE_NAME_PRODUCT + " s "+
//                "Where s." + MyConstanta.TABLE_NAME_PRODUCT + "=?";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = null;
//        if (db != null) {
//            cursor =  db.rawQuery(query, new String[]{productName});
//        }
//
//        return cursor;
//    }


    public Cursor seachProduct(String productName){
        String query = "SELECT * FROM " + MyConstanta.TABLE_NAME_PRODUCT +
                " Where " + MyConstanta.TITLEPRODUCT + "=?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor =  db.rawQuery(query, new String[]{productName});
        }

        return cursor;
    }

    public Cursor seachPP(int idProject, int idProduct){
        String query = "SELECT * FROM " + MyConstanta.TABLE_NAME_PROJECT_PRODUCT +
                " Where " + MyConstanta.IDPROJECT + "=? and " + MyConstanta.IDPRODUCT + "=?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor =  db.rawQuery(query, new String[]{String.valueOf(idProject), String.valueOf(idProduct)});
        }

        return cursor;
    }

    public Cursor seachCategory(int idProject){
        String query = "SELECT " + MyConstanta.CATEGORY +
                " FROM " + MyConstanta.TABLE_NAME_ADD + " ad " +
                "JOIN " + MyConstanta.TABLE_NAME_PROJECT_PRODUCT + " pp " +
                "ON pp." + MyConstanta._ID  + " = " + "ad." + MyConstanta.IDPP +

                " JOIN " + MyConstanta.TABLE_NAME_PRODUCT + " prod " +
                "ON prod." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPRODUCT +

                " JOIN " + MyConstanta.TABLE_NAME + " proj " +
                "ON proj." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPROJECT +

                " WHERE proj." + MyConstanta._ID + "=? " ;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor =  db.rawQuery(query, new String[]{String.valueOf(idProject)});
        }

        return cursor;
    }

    public Cursor seachProductToProject(int idProject){
        String query = "SELECT " + MyConstanta.TITLEPRODUCT + ", " + MyConstanta.CATEGORY +
                " FROM " + MyConstanta.TABLE_NAME_ADD + " ad " +
                "JOIN " + MyConstanta.TABLE_NAME_PROJECT_PRODUCT + " pp " +
                "ON pp." + MyConstanta._ID  + " = " + "ad." + MyConstanta.IDPP +

                " JOIN " + MyConstanta.TABLE_NAME_PRODUCT + " prod " +
                "ON prod." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPRODUCT +

                " JOIN " + MyConstanta.TABLE_NAME + " proj " +
                "ON proj." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPROJECT +

                " WHERE proj." + MyConstanta._ID + "=?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor =  db.rawQuery(query, new String[]{String.valueOf(idProject)});
        }

        return cursor;
    }
    public Cursor readAddMagazine(int idProject){
        String query = "SELECT " + MyConstanta.TITLEPRODUCT + ", " + MyConstanta.CATEGORY +", " +
                MyConstanta.QUANTITY + ", " + MyConstanta.PRICE + ", " + MyConstanta.DATE +
                " FROM " + MyConstanta.TABLE_NAME_ADD + " ad " +
                "JOIN " + MyConstanta.TABLE_NAME_PROJECT_PRODUCT + " pp " +
                "ON pp." + MyConstanta._ID  + " = " + "ad." + MyConstanta.IDPP +

                " JOIN " + MyConstanta.TABLE_NAME_PRODUCT + " prod " +
                "ON prod." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPRODUCT +

                " JOIN " + MyConstanta.TABLE_NAME + " proj " +
                "ON proj." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPROJECT +

                " WHERE proj." + MyConstanta._ID + "=?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor =  db.rawQuery(query, new String[]{String.valueOf(idProject)});
        }

        return cursor;
    }

    public Cursor readWriteOffMagazine(int idProject){
        String query = "SELECT " + MyConstanta.TITLEPRODUCT + ", " + MyConstanta.CATEGORY +", " +
                MyConstanta.QUANTITY + ", " + MyConstanta.DATE +
                " FROM " + MyConstanta.TABLE_NAME_WRITEOFF + " ad " +
                "JOIN " + MyConstanta.TABLE_NAME_PROJECT_PRODUCT + " pp " +
                "ON pp." + MyConstanta._ID  + " = " + "ad." + MyConstanta.IDPP +

                " JOIN " + MyConstanta.TABLE_NAME_PRODUCT + " prod " +
                "ON prod." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPRODUCT +

                " JOIN " + MyConstanta.TABLE_NAME + " proj " +
                "ON proj." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPROJECT +

                " WHERE proj." + MyConstanta._ID + "=?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor =  db.rawQuery(query, new String[]{String.valueOf(idProject)});
        }

        return cursor;
    }

}
