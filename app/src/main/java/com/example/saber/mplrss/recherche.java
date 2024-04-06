package com.example.saber.mplrss;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class recherche extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    EditText recherche;
    AccesDonnees AD;
    SimpleCursorAdapter sa;
    android.app.LoaderManager manager;
    ListView resultItemsListView;
    TextView nbrResultats;
    public void runQuery(Bundle B){
        manager.restartLoader(0,B,this);
    }
    public void setOnItemClick(final Cursor data){
        resultItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent iii = new Intent(getApplicationContext(),Article.class);
                iii.putExtra("ID",(int)id);
                data.moveToPosition(position);
                Cursor chanelName = AD.getChannelByURL(data.getString(4));
                chanelName.moveToFirst();
                iii.putExtra("ChannelName",chanelName.getString(1));
                startActivity(iii);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);
        AD = new AccesDonnees(this);
        manager = getLoaderManager();
        recherche = (EditText)findViewById(R.id.editText_recherche);
        //Quand l'utilisateur saisie
        recherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                resultItemsListView = (ListView)findViewById(R.id.ListView_items_resultats);
                nbrResultats = (TextView)findViewById(R.id.textView_nombre_resultats);
                if (recherche.getText().length()!=0){
                    Bundle b = new Bundle();
                    b.putString("word","%"+recherche.getText().toString()+"%");
                    runQuery(b);

                }else {
                    resultItemsListView.setVisibility(View.INVISIBLE);
                    nbrResultats.setText("0 Résultat(s)");
                }

            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String authority = "fr.mplrss.bdd";
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(authority).appendPath("item").build();
        String chaine = args.getString("word");
        return new CursorLoader(this,uri,new String[]{"ID as _id","Title","Link","Description","Channel"},"Title like ? or Description like ?", new String[]{chaine,chaine},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        resultItemsListView = (ListView)findViewById(R.id.ListView_items_resultats);
        nbrResultats = (TextView)findViewById(R.id.textView_nombre_resultats);
        resultItemsListView.setVisibility(View.VISIBLE);
        //afficher les items
        nbrResultats.setText(data.getCount()+" Résultat(s)");
        String[] from = new String[]{"Title","Description"};
        int[] to = new int[]{R.id.result_title,R.id.result_desc};
        sa = new SimpleCursorAdapter(getApplicationContext(),R.layout.search_result_list,data,from,to,0);
        resultItemsListView.setAdapter(sa);
        setOnItemClick(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
