package com.example.saber.mplrss;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Adresses_Favories extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    SimpleCursorAdapter sa;
    AccesDonnees AD;
    int op;
    Spinner sp;
    android.app.LoaderManager manager;
    public void Supprimer(View v){
        AD.ajoutAdresse("http://www.haha.com/file.xml");
        /*Button supp = (Button)findViewById(R.id.btn_suppr);
        TextView tv = (TextView)findViewById(R.id.textView3);
        if (op == 0){
            op = 1;//suppression
            supp.setText("Annuler");
            tv.setVisibility(View.VISIBLE);
        }else{
            op = 0;//selection
            supp.setText("Supprimer");
            tv.setVisibility(View.GONE);
        }*/
        manager.restartLoader(0,null,this);
    }
    public void restartLoader(){
        manager.restartLoader(0,null,this);
    }
    public void setOnItemClick(final Cursor data){
        ListView lv = (ListView)findViewById(R.id.ListView);
        //Quand l'utilisateur click sur une adresse
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                data.moveToPosition(position);
                switch (op){
                    case 0://selection
                        Intent back = new Intent(getApplicationContext(),Download.class);
                        back.putExtra("Adr",data.getString(1));
                        setResult(RESULT_OK, back);
                        finish();
                        break;
                    case 1://suppression
                        AD.removeAdresse((int)id);
                        Vibrator vibe = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
                        Toast.makeText(getApplicationContext(),"Adresse supprimé",Toast.LENGTH_LONG).show();
                        vibe.vibrate(100);
                        restartLoader();
                        break;
                }

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adresses__favoris);
        AD = new AccesDonnees(this);
        op = 0;
        sp = (Spinner)findViewById(R.id.spinnerSaber);
        manager = getLoaderManager();
        manager.initLoader(0,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String authority = "fr.mplrss.bdd";
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(authority).appendPath("adresse").build();
        return new CursorLoader(this,uri,new String[]{"ID as _id","Adresse"},null, null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ListView lv = (ListView)findViewById(R.id.ListView);
        String[] from = new String[]{"Adresse"};
        int[] to = new int[]{R.id.List_Item1};
        sa = new SimpleCursorAdapter(this,R.layout.simple_list_view,data,from,to,0);
        sa.setDropDownViewResource(R.layout.simple_list_view);
        sp.setAdapter(sa);
        //lv.setAdapter(sa);
        //setOnItemClick(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
