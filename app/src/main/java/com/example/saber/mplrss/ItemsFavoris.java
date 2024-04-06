package com.example.saber.mplrss;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ItemsFavoris extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    AccesDonnees AD;
    SimpleCursorAdapter sa;
    ListView lv;
    android.app.LoaderManager manager;
    public void setOnClickItem(final Cursor data){
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent iii = new Intent(getApplicationContext(),Article.class);
                iii.putExtra("ID",(int)id);
                Cursor c = AD.getChannelByURL(data.getString(4));
                c.moveToFirst();
                iii.putExtra("ChannelName",c.getString(1));
                startActivity(iii);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        manager.restartLoader(0,null,this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_favoris);
        AD = new AccesDonnees(this);
        manager = getLoaderManager();
        manager.initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String authority = "fr.mplrss.bdd";
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(authority).appendPath("item").build();
        return new CursorLoader(this,uri,new String[]{"ID as _id","Title","Link","Description","Channel","Favoris"},"Favoris = ?", new String[]{"1"},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        lv = (ListView)findViewById(R.id.listView_ItemsFavoris);
        String[] from = new String[]{"Title"};
        int[] to = new int[]{R.id.channel_Title};
        sa = new SimpleCursorAdapter(this,R.layout.items_list,data,from,to,0);
        lv.setAdapter(sa);
        setOnClickItem(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
