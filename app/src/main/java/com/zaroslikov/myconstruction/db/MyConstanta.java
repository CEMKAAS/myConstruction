package com.zaroslikov.myconstruction.db;

public class MyConstanta {

    public static final String DB_NAME = "my_dbConst.db"; //База данных
    public static final int DB_VERSION = 1; //Версия базы данных

    public static final String TABLE_NAME = "МyProject"; // Название таблицы
    public static final String _ID = "_id"; // Индефикатор НУМЕРАЦИЯ СТРОК
    public static final String TITLEPROJECT  = "nameProject"; // Название описание (название Проекта)
    public static final String DATEPROJECT = "dateProject"; // Дата создания проекта
    public static final String PICTUREROJECT = "pictureProject"; // Картинка проекта
    public static final String STATUSPROJECT = "statusProject"; // Дата создания проекта

    public static final String FOREIGNKEY = "productID";

    public static final String TABLE_NAME_PRODUCT = "МyProduct"; // Название таблицы
    public static final String TITLEPRODUCT = "nameProduct"; // Название описание (название продукта) название проданного товара
    public static final String SUFFIXPRODUCT = "suffixProduct"; //Заголовок (Суффикс)
    public static final String FOREIGNKEYADD = "addId";
    public static final String FOREIGNKEYSALE = "saleID";

    public static final String TABLE_NAME_ADD = "AddProduct"; // Название таблицы
    public static final String COUNTADD = "countAdd"; //Заголовок (кол-во)
    public static final String CATEGORYADD = "CategoryADD"; //Заголовок (Категория)
    public static final String PRICEADD = "priceADD"; //Заголовок (Цена)
    public static final String DATEADD = "dateADD"; //Заголовок (Дата)

    public static final String TABLE_NAME_Sale = "SaleProduct"; // Название таблицы
    public static final String COUNTSALE = "countSale"; //Заголовок (кол-во)
    public static final String CATEGORYSALE = "CategorySale"; //Заголовок (Категория)
    public static final String DATESALE = "dateSale"; //Заголовок (Дата)

//    public static final String TABLE_STRUCTURE_PROJECT = "CREATE TABLE IF NOT EXISTS " +
//            TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY,"
//            + TITLEPROJECT + " TEXT,"
//            + DATEPROJECT + " TEXT,"
//            + PICTUREROJECT + " INTEGER,"
//            + STATUSPROJECT + " INTEGER,"
//            + FOREIGNKEY + " INTEGER,"
//            + "FOREIGN KEY (" + FOREIGNKEY + ") REFERENCES "
//            + TABLE_NAME_PRODUCT + "(" + _ID + "))";
//
//    public static final String TABLE_STRUCTURE_PRODUCT = "CREATE TABLE IF NOT EXISTS " +
//            TABLE_NAME_PRODUCT + " (" + _ID + " INTEGER PRIMARY KEY,"
//            + TITLEPRODUCT + " TEXT,"
//            + SUFFIXPRODUCT + " TEXT,"
//            + FOREIGNKEYADD + " INTEGER,"
//            + FOREIGNKEYSALE + " INTEGER,"
//            + "FOREIGN KEY (" + FOREIGNKEYADD + ") REFERENCES "
//            + TABLE_NAME_PRODUCT + "(" + _ID + ")"
//            + "FOREIGN KEY (" + FOREIGNKEYSALE + ") REFERENCES "
//            + TABLE_NAME_PRODUCT + "(" + _ID + ")"
//            +")";
//
//    public static final String TABLE_STRUCTURE_ADD = "CREATE TABLE IF NOT EXISTS " +
//            TABLE_NAME_ADD + " (" + _ID + " INTEGER PRIMARY KEY,"
//            + COUNTADD + " REAL,"
//            + CATEGORYADD + " TEXT,"
//            + PRICEADD + " REAL,"
//            + DATEADD + " TEXT)";
//
//    public static final String TABLE_STRUCTURE_SALE = "CREATE TABLE IF NOT EXISTS " +
//            TABLE_NAME_Sale + " (" + _ID + " INTEGER PRIMARY KEY,"
//            + COUNTSALE + " REAL,"
//            + CATEGORYSALE + " TEXT,"
//            + DATESALE + " TEXT)";


    public static final String TABLE_STRUCTURE_PROJECT = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY,"
            + TITLEPROJECT + " TEXT,"
            + DATEPROJECT + " TEXT,"
            + PICTUREROJECT + " INTEGER,"
            + STATUSPROJECT + " INTEGER)";


    public static final String TABLE_STRUCTURE_PRODUCT = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME_PRODUCT + " (" + _ID + " INTEGER PRIMARY KEY,"
            + TITLEPRODUCT + " TEXT,"
            + SUFFIXPRODUCT + " TEXT,"
            + FOREIGNKEY + " INTEGER,"
            + "FOREIGN KEY (" + FOREIGNKEY + ") REFERENCES "
            + TABLE_NAME + "(" + _ID + "))";

    public static final String TABLE_STRUCTURE_ADD = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME_ADD + " (" + _ID + " INTEGER PRIMARY KEY,"
            + COUNTADD + " REAL,"
            + CATEGORYADD + " TEXT,"
            + PRICEADD + " REAL,"
            + DATEADD + " TEXT)";

    public static final String TABLE_STRUCTURE_SALE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME_Sale + " (" + _ID + " INTEGER PRIMARY KEY,"
            + COUNTSALE + " REAL,"
            + CATEGORYSALE + " TEXT,"
            + DATESALE + " TEXT)";





    public static final String DROP_TABLE_PROJECT = "DROP TABLE IF EXISTS" + TABLE_NAME; // сброс продаж
    public static final String DROP_TABLE_PRODUCT = "DROP TABLE IF EXISTS" + TABLE_NAME_PRODUCT; // сброс покупок
    public static final String DROP_TABLE_ADD= "DROP TABLE IF EXISTS" + TABLE_NAME_ADD; // сброс обычной
    public static final String DROP_TABLE_SALE = "DROP TABLE IF EXISTS" + TABLE_NAME_Sale; // сброс цен

}
