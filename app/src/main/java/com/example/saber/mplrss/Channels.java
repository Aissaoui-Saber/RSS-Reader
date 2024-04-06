package com.example.saber.mplrss;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.nio.channels.Channel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Channels extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    Cursor C1,C2;
    ListView lv;
    SimpleCursorAdapter sa;
    AccesDonnees db;
    android.app.LoaderManager manager;
    String channelUrl;
    public void openDownloadActivity(View v){
        Intent iii = new Intent(this,Download.class);
        startActivity(iii);
    }
    public void openSearchActivity(View v){
        Intent iii = new Intent(this,recherche.class);
        startActivity(iii);
    }
    public void openFavorisActivity(View v){
        Intent iii = new Intent(this,ItemsFavoris.class);
        startActivity(iii);
    }
    public void setOnClickItem(final Cursor data){
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                data.moveToPosition(position);
                Intent iii = new Intent(getApplicationContext(),Channel_items.class);
                iii.putExtra("URL",data.getString(0));
                startActivity(iii);
            }
        });
    }
    public void netoyer(){
        //recuperer tout les channels
        C1 = db.getALLChannels();
        //récuperer la date d'aujourd'hui en String
        Date day = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(day);

        while (C1.moveToNext()){
            //pour chaque Channel Verifier si la date de teleh dépasse 1 mois
            if (isOldThanOneMounth(today,C1.getString(4))){
                //Verifier si ce channel ne contient aucun item au favoris
                channelUrl = C1.getString(0);
                C2 = db.getFavoriteItemsByChannel(channelUrl);
                if (C2.getCount() == 0) {
                    //supprimer le channel
                    db.deleteSpeceficChannel(channelUrl);
                }
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        manager.restartLoader(0, null, this);
    }
    //comparer une date avec la date d'aujourd'hui et vérifier si elle dépasse un mois
    public static boolean isOldThanOneMounth(String Today ,String date) {
        int nYear = Integer.valueOf(Today.substring(0,4));
        int nMounth = Integer.valueOf(Today.substring(5,7));
        int nDay = Integer.valueOf(Today.substring(8,10));
        int oYear = Integer.valueOf(date.substring(0,4));
        int oMounth = Integer.valueOf(date.substring(5,7));
        int oDay = Integer.valueOf(date.substring(8,10));
        if (nYear - oYear == 0){//meme année
            if (nMounth - oMounth > 1){//plus que 2 mois
                return true;
            }else if(nMounth - oMounth == 0){//le meme mois
                return false;
            }else{//le mois suivant
                if (nDay - oDay > 0){
                    return false;
                }else{
                    return true;
                }
            }
        }else if(nYear - oYear == 1){//l'année suivante
            if ((nMounth > 1)&&(oMounth < 12)){
                return true;
            }else{//janvier et decembre
                if (nDay - oDay >= 0){
                    return true;
                }else{
                    return false;
                }
            }
        }else{//plusieurs années
            return false;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);
        lv = (ListView)findViewById(R.id.ListView_Channels);
        //netoyage
        db = new AccesDonnees(this);
        manager = getLoaderManager();
        netoyer();
        manager.initLoader(0, null, this);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int i, @Nullable Bundle bundle) {
        String authority = "fr.mplrss.bdd";
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(authority).appendPath("channel").build();
        return new CursorLoader(this,uri,new String[]{"URL as _id","Title","Link","Description","Date_Telechargement"},null, null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Cursor data) {
        lv = (ListView)findViewById(R.id.ListView_Channels);
        String[] from = new String[]{"Title"};
        int[] to = new int[]{R.id.channel_Title};
        sa = new SimpleCursorAdapter(this,R.layout.channels_list,data,from,to,0);
        lv.setAdapter(sa);
        setOnClickItem(data);
    }
    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}
