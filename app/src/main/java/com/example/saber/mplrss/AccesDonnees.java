package com.example.saber.mplrss;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

public class AccesDonnees extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
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

    private static String authority = "fr.mplrss.bdd";

    private ContentResolver MyContentResolver;
    public AccesDonnees(Context context){
        MyContentResolver = context.getContentResolver();
    }
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
    //TABLE ADRESSES -----------------------------------------------------------------
    public Uri ajoutAdresse(String Adr){
        ContentValues cv = new ContentValues();
        cv.put(ADRESSES_ADRESSE,Adr.replaceAll("\\s+",""));
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("adresse");
        Uri uri = builder.build();
        uri = MyContentResolver.insert(uri,cv);
        return uri;
    }
    public int removeAdresse(int id){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("adresse");
        Uri uri = builder.build();
        return MyContentResolver.delete(uri,"ID = ?",new String[]{String.valueOf(id)});
    }
    public boolean adresseExist(String adr){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("adresse");
        Uri uri = builder.build();
        Cursor c = MyContentResolver.query(uri,null,"Adresse = ?", new String[]{adr.replaceAll("\\s+","")},null);
        if (c.moveToFirst()){
            return true;
        }else {
            return false;
        }
    }
    //TABLE CHANNELS -----------------------------------------------------------------
    public Uri ajoutChannel(String URL,String Title,String Link,String Desc,String date){
        ContentValues cv = new ContentValues();
        cv.put(CHANNEL_XML_LINK,URL);
        cv.put(CHANNEL_TITLE,Title);
        cv.put(CHANNEL_LINK,Link);
        cv.put(CHANNEL_DESC,Desc);
        cv.put(CHANNEL_DATE_TELECH,date);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("channel");
        Uri uri = builder.build();
        uri = MyContentResolver.insert(uri,cv);
        return uri;
    }
    public Cursor getALLChannels(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("channel");
        Uri uri = builder.build();
        return MyContentResolver.query(uri,new String[]{"URL as _id",CHANNEL_TITLE,CHANNEL_LINK,CHANNEL_DESC,CHANNEL_DATE_TELECH},null, null,null);
    }
    public Cursor getChannelByURL(String URL){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("channel");
        Uri uri = builder.build();
        return MyContentResolver.query(uri,new String[]{"URL as _id",CHANNEL_TITLE,CHANNEL_LINK,CHANNEL_DESC},"URL = ?", new String[]{URL},null);
    }
    public int deleteSpeceficChannel(String URL){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("channel");
        Uri uri = builder.build();
        deleteItemByChannel(URL);
        return MyContentResolver.delete(uri,"URL = ?",new String[]{URL});
    }
    public boolean channelExist(String url){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("channel");
        Uri uri = builder.build();
        Cursor c = MyContentResolver.query(uri,null,"URL = ?", new String[]{url},null);
        if (c.moveToFirst()){
            return true;
        }else {
            return false;
        }
    }
    //TABLE ITEMS --------------------------------------------------------------------
    public Uri ajoutItem(String title,String link,String desc,String ChannelURL){
        ContentValues cv = new ContentValues();
        cv.put(ITEM_TITLE,title);
        cv.put(ITEM_LINK,link);
        cv.put(ITEM_DESC,desc);
        cv.put(ITEM_CHANNEL,ChannelURL);
        cv.put(ITEM_FAVORIS,false);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("item");
        Uri uri = builder.build();
        uri = MyContentResolver.insert(uri,cv);
        return uri;
    }
    public int deleteItemByChannel(String URL){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("item");
        Uri uri = builder.build();
        return MyContentResolver.delete(uri,"Channel = ?",new String[]{URL});
    }
    public  Cursor getItemByID(int id){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("item");
        Uri uri = builder.build();
        return MyContentResolver.query(uri,null,"ID = ?", new String[]{String.valueOf(id)},null);
    }
    public int ajouterItemAuFavoris(int Item_ID){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("item");
        ContentValues cv = new ContentValues();
        cv.put(ITEM_FAVORIS,true);
        Uri uri = builder.build();
        return MyContentResolver.update(uri,cv,"ID = ?",new String[]{String.valueOf(Item_ID)});
    }
    public int removeItemFromFavoris(int Item_ID){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("item");
        ContentValues cv = new ContentValues();
        cv.put(ITEM_FAVORIS,false);
        Uri uri = builder.build();
        return MyContentResolver.update(uri,cv,"ID = ?",new String[]{String.valueOf(Item_ID)});
    }
    public boolean isItemFavoris(int Item_ID){
        Cursor c = getItemByID(Item_ID);
        if (c.moveToFirst()){
            if (c.getInt(5) == 1){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }
    public Cursor getFavoriteItemsByChannel(String channel){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("item");
        Uri uri = builder.build();
        return MyContentResolver.query(uri,null,"Channel = ? AND Favoris = ?", new String[]{channel,"1"},null);
    }
}
