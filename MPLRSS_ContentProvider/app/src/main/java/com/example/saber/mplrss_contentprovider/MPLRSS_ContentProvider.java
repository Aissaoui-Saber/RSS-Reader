package com.example.saber.mplrss_contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class MPLRSS_ContentProvider extends ContentProvider {
    private static String authority = "fr.mplrss.bdd";
    private BDD Base_RSS;
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int CODE_ADRESSE = 1;
    static {
        matcher.addURI(authority,"adresse",CODE_ADRESSE);
    }
    public MPLRSS_ContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
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
                id = Base_RSS.getWritableDatabase().insertOrThrow("Adresses",null,values);
                path = "adresse";
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
                return Base_RSS.getReadableDatabase().query("Adresses",null,null,null,null,null,null);
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
