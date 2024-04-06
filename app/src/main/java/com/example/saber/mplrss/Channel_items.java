package com.example.saber.mplrss;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Channel_items extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    SimpleCursorAdapter sa;
    AccesDonnees db;
    String Channel_URL;
    TextView channel_title,channel_desc;
    ListView lv;
    android.app.LoaderManager manager;
    public void supprimerChannel(View v){
        if (db.deleteSpeceficChannel(Channel_URL) == 1){
            final Intent iii = new Intent(this,Channels.class);
            final Vibrator vibe = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
            Toast.makeText(this,"Supprimé",Toast.LENGTH_LONG).show();
            vibe.vibrate(100);
            finish();
            startActivity(iii);
        }
    }
    public void setOnItemClick(){
        channel_title = (TextView)findViewById(R.id.textView_channel_title);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent iii = new Intent(getApplicationContext(),Article.class);
                iii.putExtra("ID",(int)id);
                iii.putExtra("ChannelName",channel_title.getText().toString());
                startActivity(iii);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_items);

        db = new AccesDonnees(this);
        Channel_URL = getIntent().getExtras().getString("URL");//récuperer l'URL de channel
        manager = getLoaderManager();
        Bundle b = new Bundle();
        b.putString("url",Channel_URL);
        manager.initLoader(0,b,this);
        manager.restartLoader(1,b,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String authority = "fr.mplrss.bdd";
        Uri.Builder builder = new Uri.Builder();
        String URL = args.getString("url");
        switch (id){
            case 0://récuperer title description et link du channel
                Uri uri = builder.scheme("content").authority(authority).appendPath("channel").build();
                return new CursorLoader(this,uri,new String[]{"URL as _id","Title","Link","Description"},"URL = ?", new String[]{URL},null);
            case 1://récuperer la liste d'items de ce channel
                Uri uri2 = builder.scheme("content").authority(authority).appendPath("item").build();
                return new CursorLoader(this,uri2,new String[]{"ID as _id","Title","Link","Description","Channel"},"Channel = ?", new String[]{URL},null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        channel_title = (TextView)findViewById(R.id.textView_channel_title);
        channel_desc = (TextView)findViewById(R.id.textView_channel_desc);
        lv = (ListView)findViewById(R.id.ListView_items);
        switch (loader.getId()){
            case 0:
                //afficher le nom et la desc de channel
                if (data.moveToFirst()){
                    //si le titre est vide
                    if (data.getString(1).equals("")){
                        channel_title.setText("Sans titre");
                    }else {
                        channel_title.setText(data.getString(1));
                    }
                    //si la description est vide
                    if (data.getString(3).equals("")){
                        channel_desc.setText("Aucune description");
                    }else {
                        channel_desc.setText(data.getString(3));
                    }
                }
                break;
            case 1:
                //afficher les items
                //itemsCursor = db.getALLitems(Channel_URL);
                String[] from = new String[]{"Title"};
                int[] to = new int[]{R.id.channel_Title};
                sa = new SimpleCursorAdapter(this,R.layout.items_list,data,from,to,0);
                lv.setAdapter(sa);
                setOnItemClick();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
