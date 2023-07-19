package com.zaroslikov.myconstruction.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

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


    public Cursor selectProductJoin(int propertyId, String productName, String tableName, String suffix){
        String query = "SELECT " + MyConstanta.TITLEPRODUCT+
                ", sum(" + MyConstanta.QUANTITY + "), " + MyConstanta.SUFFIX +
                " FROM " + tableName + " ad " +
                "JOIN " + MyConstanta.TABLE_NAME_PROJECT_PRODUCT + " pp " +
                "ON pp." + MyConstanta._ID  + " = " + "ad." + MyConstanta.IDPP +

                " JOIN " + MyConstanta.TABLE_NAME_PRODUCT + " prod " +
                "ON prod." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPRODUCT +

                " JOIN " + MyConstanta.TABLE_NAME + " proj " +
                "ON proj." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPROJECT +

                " WHERE proj." + MyConstanta._ID + "=? and " + MyConstanta.TITLEPRODUCT + "=? and " + MyConstanta.SUFFIX + "=? " +
                "group by " + MyConstanta.TITLEPRODUCT + ", " + MyConstanta.SUFFIX;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {

            cursor =  db.rawQuery(query, new String[]{String.valueOf(propertyId),productName, suffix});
        }

        return cursor;
    }


    public Cursor selectProjectAllSum (int propertyId){
        String query = "SELECT " +
                "sum(" + MyConstanta.PRICE + ")" +
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

            cursor =  db.rawQuery(query, new String[]{String.valueOf(propertyId)});
        }

        return cursor;
    }


    public Cursor selectProjectAllSumCategory(int propertyId, String category){
        String query = "SELECT " + MyConstanta.CATEGORY +
                ", sum(" + MyConstanta.PRICE + "), " + MyConstanta.DATE +
                " FROM " + MyConstanta.TABLE_NAME_ADD + " ad " +
                "JOIN " + MyConstanta.TABLE_NAME_PROJECT_PRODUCT + " pp " +
                "ON pp." + MyConstanta._ID  + " = " + "ad." + MyConstanta.IDPP +

                " JOIN " + MyConstanta.TABLE_NAME_PRODUCT + " prod " +
                "ON prod." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPRODUCT +

                " JOIN " + MyConstanta.TABLE_NAME + " proj " +
                "ON proj." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPROJECT +

                " WHERE proj." + MyConstanta._ID + "=? and " + MyConstanta.CATEGORY + "=?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {

            cursor =  db.rawQuery(query, new String[]{String.valueOf(propertyId),category});
        }

        return cursor;
    }


    public Cursor selectProjectAllSumProduct(int propertyId, String product){
        String query = "SELECT " + MyConstanta.TITLEPRODUCT+ ", "+
                MyConstanta.SUFFIX +
                ", sum(" + MyConstanta.PRICE + "), " + MyConstanta.DATE +
                " FROM " + MyConstanta.TABLE_NAME_ADD + " ad " +
                "JOIN " + MyConstanta.TABLE_NAME_PROJECT_PRODUCT + " pp " +
                "ON pp." + MyConstanta._ID  + " = " + "ad." + MyConstanta.IDPP +

                " JOIN " + MyConstanta.TABLE_NAME_PRODUCT + " prod " +
                "ON prod." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPRODUCT +

                " JOIN " + MyConstanta.TABLE_NAME + " proj " +
                "ON proj." + MyConstanta._ID  + " = " + " pp." + MyConstanta.IDPROJECT +

                " WHERE proj." + MyConstanta._ID + "=? and " + MyConstanta.TITLEPRODUCT + "=?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {

            cursor =  db.rawQuery(query, new String[]{String.valueOf(propertyId),product});
        }

        return cursor;
    }

    public Cursor selectProjectAllProductAndCategoryAdd(int propertyId){
        String query = "SELECT " + MyConstanta.TITLEPRODUCT+ ", "+
                MyConstanta.SUFFIX + ", " + MyConstanta.CATEGORY +
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
            cursor =  db.rawQuery(query, new String[]{String.valueOf(propertyId)});
        }

        return cursor;
    }


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

    public Cursor seachProductAndSuffix(String productName, String suffix){
        String query = "SELECT * FROM " + MyConstanta.TABLE_NAME_PRODUCT +
                " Where " + MyConstanta.TITLEPRODUCT + "=? and " + MyConstanta.SUFFIX + "=?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor =  db.rawQuery(query, new String[]{productName, suffix});
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
        String query = "SELECT ad."+ MyConstanta._ID + ", " + MyConstanta.TITLEPRODUCT + ", " + MyConstanta.CATEGORY +", " +
                MyConstanta.QUANTITY + ", " + MyConstanta.PRICE + ", " + MyConstanta.DATE + ", " + MyConstanta.SUFFIX +
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
        String query = "SELECT ad."+ MyConstanta._ID + ", "+ MyConstanta.TITLEPRODUCT + ", " + MyConstanta.CATEGORY +", " +
                MyConstanta.QUANTITY + ", " + MyConstanta.DATE + ", " + MyConstanta.SUFFIX +
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

    public long updateToDbProjectProduct( int idPP, int idProject, int idProduct) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyConstanta.IDPROJECT, idProject);
        cv.put(MyConstanta.IDPRODUCT, idProduct);
        long id = db.update(MyConstanta.TABLE_NAME_PROJECT_PRODUCT,cv,"id=?", new String[]{String.valueOf(idPP)});

        if (id == -1) {
            Toast.makeText(context, "Ошибка!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Успешно обновлено!", Toast.LENGTH_SHORT).show();
        }
        return id;
    }

    public Cursor seachAdd(double count, String category, double price, String date, int idPP){
        String query = "SELECT * FROM " + MyConstanta.TABLE_NAME_ADD +
                " Where " + MyConstanta.QUANTITY + "=? and " + MyConstanta.CATEGORY + "=? and " +
                MyConstanta.PRICE + "=? and " + MyConstanta.DATE + "=? and " +
                MyConstanta.IDPP + "=?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor =  db.rawQuery(query, new String[]{String.valueOf(count), category, String.valueOf(price),date,String.valueOf(idPP)});
        }

        return cursor;
    }

    public Cursor seachWriteOff(double count, String category, String date, int idPP){
        String query = "SELECT * FROM " + MyConstanta.TABLE_NAME_ADD +
                " Where " + MyConstanta.QUANTITY + "=? and " + MyConstanta.CATEGORY + "=? and "
                + MyConstanta.DATE + "=? and " + MyConstanta.IDPP + "=?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor =  db.rawQuery(query, new String[]{String.valueOf(count), category,date,String.valueOf(idPP)});
        }

        return cursor;
    }


    public void updateToDbAdd(double count, String category, double price, String date, int idPP, int idAdd) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyConstanta.QUANTITY,count);
        cv.put(MyConstanta.CATEGORY, category);
        cv.put(MyConstanta.PRICE, price);
        cv.put(MyConstanta.DATE, date);
        cv.put(MyConstanta.IDPP, idPP);
        long id = db.update(MyConstanta.TABLE_NAME_ADD,cv,"id=?", new String[]{String.valueOf(idAdd)});

    }


    public void updateToDbWriteOff(double count, String category, String date, int idPP, int idWO) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyConstanta.QUANTITY,count);
        cv.put(MyConstanta.CATEGORY, category);
        cv.put(MyConstanta.DATE, date);
        cv.put(MyConstanta.IDPP, idPP);
        long id = db.update(MyConstanta.TABLE_NAME_WRITEOFF,cv,"id=?", new String[]{String.valueOf(idWO)});

    }

    public long updateToDbProduct(String oldName, String name, String suffix) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyConstanta.TITLEPRODUCT,name);
        cv.put(MyConstanta.SUFFIX, suffix);
        long id = db.update(MyConstanta.TABLE_NAME_PRODUCT,cv,"NameProduct=?", new String[]{oldName});
        return id;
    }

    public void updateToDbProject(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyConstanta.STATUSPROJECT,status);
        db.update(MyConstanta.TABLE_NAME,cv, MyConstanta._ID + "= ?", new String[]{String.valueOf(id)});
    }

    public void deleteOneRowAdd(int row_id, String nameTable) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(nameTable, "id=?", new String[]{String.valueOf(row_id)});
        if (result == -1) {
            Toast.makeText(context, "Ошибка.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Успешно удаленно.", Toast.LENGTH_SHORT).show();
        }
    }

}
