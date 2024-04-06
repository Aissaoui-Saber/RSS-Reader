package com.example.saber.mplrss;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.AttrRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.jar.Attributes;

public class Article extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    AccesDonnees AD;
    String VoirPlusLink,ItemChannelName;
    int ItemID;
    Button ajouterAuFavoris;
    boolean estEnFavoris;
    android.app.LoaderManager manager;
    public void OuvrirWebView(View v){
        if (VoirPlusLink == ""){
            Toast.makeText(this,"Lien introuvable",Toast.LENGTH_LONG).show();
        }else {
            Intent iii = new Intent(this,ItemWebView.class);
            iii.putExtra("link",VoirPlusLink);
            startActivity(iii);
        }
    }
    public void AjouterAuFavoris(View v){
        Vibrator vibe = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        if (estEnFavoris){
            AD.removeItemFromFavoris(ItemID);
            estEnFavoris = false;
            ajouterAuFavoris.setText("Ajouter au favoris");
            vibe.vibrate(100);
            Toast.makeText(this,"Retiré de favoris",Toast.LENGTH_SHORT).show();
            ajouterAuFavoris.setTextColor(Color.parseColor("#f17a0a"));
        }else{
            AD.ajouterItemAuFavoris(ItemID);
            estEnFavoris = true;
            ajouterAuFavoris.setText("Retirer de favoris");
            vibe.vibrate(100);
            Toast.makeText(this,"Ajouté au favoris",Toast.LENGTH_SHORT).show();
            ajouterAuFavoris.setTextColor(Color.RED);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        AD = new AccesDonnees(this);
        ajouterAuFavoris = (Button)findViewById(R.id.button_ajouter_item_favoris);
        ItemID = getIntent().getIntExtra("ID",-1);
        ItemChannelName = getIntent().getStringExtra("ChannelName");
        if (ItemID > -1){
            estEnFavoris = AD.isItemFavoris(ItemID);
            manager = getLoaderManager();
            manager.initLoader(0,null,this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String authority = "fr.mplrss.bdd";
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(authority).appendPath("item").build();
        return new CursorLoader(this,uri,null,"ID = ?", new String[]{String.valueOf(ItemID)},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        TextView ItemTitle = (TextView)findViewById(R.id.textView_item_title);
        TextView ItemDesc = (TextView)findViewById(R.id.textView_item_desc);
        TextView ItemChannel = (TextView)findViewById(R.id.textView_item_channel);
        if (data.moveToFirst()){
            ItemChannel.setText(ItemChannelName);
            //si le titre est vide
            if (data.getString(1).equals("")){
                ItemTitle.setText("Sans Titre");
            }else{
                ItemTitle.setText(data.getString(1));
            }
            //si la description est vide
            if (data.getString(3) == null){
                ItemDesc.setText("Aucune Description");
            }else {
                ItemDesc.setText(data.getString(3));
            }
            VoirPlusLink = data.getString(2);
        }

        if (estEnFavoris){//quand l'article est en favoris
            ajouterAuFavoris.setText("Retirer de favoris");
            ajouterAuFavoris.setTextColor(Color.RED);
        }else {//quand l'article n'est pas en favoris
            ajouterAuFavoris.setText("Ajouter au favoris");
            ajouterAuFavoris.setTextColor(Color.parseColor("#f17a0a"));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
