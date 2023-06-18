package com.zaroslikov.myconstruction.db;

public class MyConstanta {

    public static final String DB_NAME = "my_dbConst.db"; //База данных
    public static final int DB_VERSION = 1; //Версия базы данных


    public static final String TABLE_NAME = "Project"; // Название таблицы
    public static final String _ID = "Id"; // Индефикатор НУМЕРАЦИЯ СТРОК
    public static final String TITLEPROJECT  = "NameProject"; // Название описание (название Проекта)
    public static final String DATEBEGINPROJECT = "DateBegin"; // Дата создания проекта
    public static final String DATEFINALPROJECT = "DateFinal"; // Дата создания проекта
    public static final String PICTUREROJECT = "Picture"; // Картинка проекта
    public static final String STATUSPROJECT = "Status"; // Дата создания проекта



    public static final String TABLE_NAME_PRODUCT = "Product"; // Название таблицы
    public static final String TITLEPRODUCT = "NameProduct"; // Название описание (название продукта) название проданного товара
    public static final String SUFFIX = "Suffix"; // Название описание (название продукта) название проданного товара


    public static final String TABLE_NAME_PROJECT_PRODUCT = "ProjectProduct"; // Название таблицы
    public static final String IDPRODUCT = "idProduct"; // Айди продукта
    public static final String IDPROJECT = "idProject"; // Айди продукта


    public static final String TABLE_NAME_ADD = "AddProd"; // Название таблицы
    public static final String TABLE_NAME_WRITEOFF = "WriteOffProd"; // Название таблицы
    public static final String QUANTITY = "Quantity"; //Заголовок (кол-во)
    public static final String CATEGORY = "Category"; //Заголовок (Категория)
    public static final String PRICE = "Price"; //Заголовок (Цена)
    public static final String DATE = "Date"; //Заголовок (Дата)
    public static final String IDPP = "idPP"; //Заголовок (Ключ)


    public static final String TABLE_STRUCTURE_PROJECT = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY, "
            + TITLEPROJECT + " TEXT, "
            + DATEBEGINPROJECT + " TEXT, "
            + DATEFINALPROJECT + " TEXT, "
            + PICTUREROJECT + " INTEGER, "
            + STATUSPROJECT + " INTEGER)";


    public static final String TABLE_STRUCTURE_PRODUCT = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME_PRODUCT + " (" + _ID + " INTEGER PRIMARY KEY, "
            + TITLEPRODUCT + " TEXT, "
            + SUFFIX + " TEXT)";


    public static final String TABLE_STRUCTURE_PRODUCTPROJECT = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME_PROJECT_PRODUCT + " (" + _ID + " INTEGER PRIMARY KEY, "
            + TITLEPRODUCT + " TEXT,"
            + IDPROJECT + " INTEGER FOREIGN KEY REFERENSES " + TABLE_NAME + " (" + _ID + "), "
            + IDPRODUCT + " INTEGER FOREIGN KEY REFERENSES " + TABLE_NAME_PRODUCT + " (" + _ID + "))";


    public static final String TABLE_STRUCTURE_ADD = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME_ADD + " (" + _ID + " INTEGER PRIMARY KEY, "
            + QUANTITY + " REAL, "
            + CATEGORY + " TEXT, "
            + PRICE + " REAL, "
            + DATE + " TEXT, "
            + IDPP + " INTEGER FOREIGN KEY REFERENSES " + TABLE_NAME_PROJECT_PRODUCT + " (" + _ID + "))";

    public static final String TABLE_STRUCTURE_WRITEOFF = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME_WRITEOFF + " (" + _ID + " INTEGER PRIMARY KEY, "
            + QUANTITY + " REAL, "
            + CATEGORY + " TEXT, "
            + DATE + " TEXT, "
            + IDPP + " INTEGER FOREIGN KEY REFERENSES " + TABLE_NAME_PROJECT_PRODUCT + " (" + _ID + "))";





    public static final String DROP_TABLE_PROJECT = "DROP TABLE IF EXISTS" + TABLE_NAME; // сброс продаж
    public static final String DROP_TABLE_PRODUCT = "DROP TABLE IF EXISTS" + TABLE_NAME_PRODUCT; // сброс покупок
    public static final String DROP_TABLE_PRODUCTPROJECT= "DROP TABLE IF EXISTS" + TABLE_NAME_PROJECT_PRODUCT; // сброс обычной
    public static final String DROP_TABLE_ADD= "DROP TABLE IF EXISTS" + TABLE_NAME_ADD; // сброс обычной
    public static final String DROP_TABLE_WRITEOFF = "DROP TABLE IF EXISTS" + TABLE_NAME_WRITEOFF; // сброс цен

}
