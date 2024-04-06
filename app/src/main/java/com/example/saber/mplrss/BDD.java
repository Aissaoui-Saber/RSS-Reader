package com.example.saber.mplrss;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDD extends SQLiteOpenHelper {
    //nom de la base de donnés----------------------------
    private static final String DB_NAME = "Base_RSS";

    //Version de la base----------------------------------
    private static final int DB_VERSION = 1;

    //Noms des tables-------------------------------------
    private static final String TABLE_ADRESSES = "Adresses";
    private static final String TABLE_CHANNELS = "Channels";
    private static final String TABLE_ITEMS = "Items";

    //Noms des attributs-----------------------------------
    //Table Adresses
    private static final String ADRESSES_ID = "ID";
    private static final String ADRESSES_ADRESSE = "Adresse";
    //Table Channels
    private static final String CHANNEL_XML_LINK = "URL";
    private static final String CHANNEL_TITLE = "Title";
    private static final String CHANNEL_LINK = "Link";
    private static final String CHANNEL_DESC = "Description";
    private static final String CHANNEL_DATE_TELECH = "Date_Telechargement";
    //Table Items
    private static final String ITEM_ID = "ID";
    private static final String ITEM_TITLE = "Title";
    private static final String ITEM_LINK = "Link";
    private static final String ITEM_DESC = "Description";
    private static final String ITEM_CHANNEL = "Channel";
    private static final String ITEM_FAVORIS = "Favoris";
    //SQL Create table------------------------------------
    //Table Adresses
    private static final String CREATE_ADRESSES_TABLE = "CREATE TABLE "+TABLE_ADRESSES+" ("+ADRESSES_ID
            +" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +ADRESSES_ADRESSE+" TEXT NOT NULL)";
    //Table Channels
    private static final String CREATE_CHANNELS_TABLE = "CREATE TABLE "+TABLE_CHANNELS+" ("+CHANNEL_XML_LINK
            +" TEXT PRIMARY KEY, "
            +CHANNEL_TITLE+" TEXT,"
            +CHANNEL_LINK+" TEXT,"
            +CHANNEL_DESC+" TEXT,"
            +CHANNEL_DATE_TELECH+" TEXT)";
    //Table Items
    private static final String CREATE_ITEMS_TABLE = "CREATE TABLE "+TABLE_ITEMS+" ("+ITEM_ID
            +" integer PRIMARY KEY AUTOINCREMENT, "
            +ITEM_TITLE+" TEXT,"
            +ITEM_LINK+" TEXT,"
            +ITEM_DESC+" TEXT,"
            +ITEM_CHANNEL+" TEXT,"
            +ITEM_FAVORIS+" BOOLEAN,"
            +"FOREIGN KEY("+ITEM_CHANNEL+") REFERENCES "+TABLE_CHANNELS+"("+CHANNEL_XML_LINK+"))";
    //Constructeur----------------------------------------
    private static BDD BDD_Instance;

    private BDD(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }
    public static BDD getInstance(Context context){
        if (BDD_Instance == null){
            BDD_Instance = new BDD(context);
        }
        return BDD_Instance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ADRESSES_TABLE);
        db.execSQL(CREATE_CHANNELS_TABLE);
        db.execSQL(CREATE_ITEMS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion){
            db.execSQL("DROP TABLE IF EXISTS " + CREATE_ADRESSES_TABLE.toString());
            db.execSQL("DROP TABLE IF EXISTS " + CREATE_CHANNELS_TABLE.toString());
            db.execSQL("DROP TABLE IF EXISTS " + CREATE_ITEMS_TABLE.toString());
            this.onCreate(db);
        }
    }
}

