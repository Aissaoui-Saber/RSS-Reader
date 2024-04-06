package com.example.saber.mplrss;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class RSS_ContentProvider extends ContentProvider {
    private static String authority = "fr.mplrss.bdd";
    private BDD Base_RSS;
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


    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int CODE_ADRESSE = 1;
    private static final int CODE_CHANNEL = 2;
    private static final int CODE_ITEM = 3;
    static {
        matcher.addURI(authority,"adresse",CODE_ADRESSE);
        matcher.addURI(authority,"channel",CODE_CHANNEL);
        matcher.addURI(authority,"item",CODE_ITEM);
    }
    public RSS_ContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int code = matcher.match(uri);
        switch (code){
            case CODE_ADRESSE:
                return Base_RSS.getWritableDatabase().delete(TABLE_ADRESSES,selection,selectionArgs);
            case CODE_CHANNEL:
                return Base_RSS.getWritableDatabase().delete(TABLE_CHANNELS,selection,selectionArgs);
            case CODE_ITEM:
                return Base_RSS.getWritableDatabase().delete(TABLE_ITEMS,selection,selectionArgs);
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id;
        int code = matcher.match(uri);
        String path;
        switch (code){
            case CODE_ADRESSE:
                id = Base_RSS.getWritableDatabase().insertOrThrow(TABLE_ADRESSES,null,values);
                path = "adresse";
                break;
            case CODE_CHANNEL:
                id = Base_RSS.getWritableDatabase().insertOrThrow(TABLE_CHANNELS,null,values);
                path = "channel";
                break;
            case CODE_ITEM:
                id = Base_RSS.getWritableDatabase().insertOrThrow(TABLE_ITEMS,null,values);
                path = "item";
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        Uri.Builder builder = new Uri.Builder().authority(authority).appendPath(path);
        return ContentUris.appendId(builder,id).build();
    }
    @Override
    public boolean onCreate() {
        Base_RSS = BDD.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int code = matcher.match(uri);
        switch (code){
            case CODE_ADRESSE:
                return Base_RSS.getReadableDatabase().query(TABLE_ADRESSES,projection,selection,selectionArgs,null,null,sortOrder);
            case CODE_CHANNEL:
                return Base_RSS.getReadableDatabase().query(TABLE_CHANNELS,projection,selection,selectionArgs,null,null,sortOrder);
            case CODE_ITEM:
                return Base_RSS.getReadableDatabase().query(TABLE_ITEMS,projection,selection,selectionArgs,null,null,sortOrder);
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int code = matcher.match(uri);
        switch (code) {
            case CODE_ADRESSE:
                return Base_RSS.getWritableDatabase().update(TABLE_ADRESSES,values,selection,selectionArgs);
            case CODE_CHANNEL:
                return Base_RSS.getWritableDatabase().update(TABLE_CHANNELS,values, selection, selectionArgs);
            case CODE_ITEM:
                return Base_RSS.getWritableDatabase().update(TABLE_ITEMS,values, selection, selectionArgs);
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}
