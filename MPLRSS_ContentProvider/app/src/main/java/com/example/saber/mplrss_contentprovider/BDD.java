package com.example.saber.mplrss_contentprovider;

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

    //Noms des attributs-----------------------------------
    private static final String ADRESSES_ID = "ID";
    private static final String ADRESSES_ADRESSE = "Adresse";

    //SQL Create table------------------------------------
    private static final String CREATE_ADRESSES_TABLE = "CREATE TABLE "+TABLE_ADRESSES+" ("+ADRESSES_ADRESSE
            +" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +ADRESSES_ADRESSE+" TEXT NOT NULL)";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion){
            db.execSQL("DROP TABLE IF EXISTS " + CREATE_ADRESSES_TABLE.toString());
            this.onCreate(db);
        }
    }
}
