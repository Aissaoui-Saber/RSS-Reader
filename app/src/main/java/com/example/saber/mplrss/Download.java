package com.example.saber.mplrss;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Download extends AppCompatActivity {
    AccesDonnees AD;
    DownloadManager dm;
    ProgressBar mProgressBar;
    TextView telechargement;
    String downloadFilePath;
    Button Annuler;
    long reference;//réference de telechargement
    LinearLayout downloadLayout;
    RSSDocument myRSSdocument;


    public static final int REQUEST_CODE = 1;//code pour communiquer avec l'activité adresses favoris
    void afficherToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
    public void Annuler(View v){
        dm.remove(reference);
        Annuler.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        telechargement.setText("Telechargement Annulé");
    }
    public void ouvrirAdressesFavoris(View v){
        Intent i = new Intent(this,Adresses_Favories.class);
        startActivityForResult(i,REQUEST_CODE);
    }
    //Supprimer le fichier telechargé
    public void delete(){
        Uri u = Uri.parse(downloadFilePath);
        File file = new File(u.getPath());
        file.delete();
        if(file.exists()){
            try {
                file.getCanonicalFile().delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(file.exists()){
                getApplicationContext().deleteFile(file.getName());
            }
        }
    }

    void afficherTelechargementEnCours(){
        downloadLayout.setVisibility(View.VISIBLE);
        Annuler.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        telechargement.setText("Telechargement en cours");
    }
    void afficherTelechargementTerminee(){
        downloadLayout.setVisibility(View.VISIBLE);
        Annuler.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        telechargement.setText("Telechargement Terminé");
    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    //telecharger un ficher xml
    void telecharger(String adresse){
        adresse = adresse.replaceAll("\\s+","");
        Uri uri = Uri.parse(adresse);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalFilesDir(getApplicationContext(),Environment.DIRECTORY_DOWNLOADS,uri.getLastPathSegment());
        reference = dm.enqueue(request);
    }

    //verifier si le lien saisie commence par 'http:// ou https://'
    boolean LienValide(String L){
        if (L.startsWith("http://")||L.startsWith("https://")){
            return true;
        }else {
            return false;
        }
    }
    //verifier la connexion internet
    boolean testConnexion(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    //lancer le telechargement
    public void Go(View v){
        EditText search = (EditText)findViewById(R.id.EditText_Recherche);

        //verifier le lien saisie
        if (LienValide(search.getText().toString())){
            //tester la connexion internet
            if (testConnexion()){
                //verifier si le document exist deja
                if (AD.channelExist(search.getText().toString())){
                    afficherToast("Ce document est deja téléchargé");
                }else {
                    //Telecharger le fichier xml
                    afficherTelechargementEnCours();
                    telecharger(search.getText().toString());
                    registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                }
            }else {
                afficherToast("Verifier votre connexion internet");
            }
        }else {
            afficherToast("Lien Invalide");
        }
    }
    //ajouter une adresse au favoris
    public void ajouterAdresseAuFavoris(View v){
        EditText search = (EditText)findViewById(R.id.EditText_Recherche);
        //verifier le lien saisie
        if (LienValide(search.getText().toString())){
            //verifier si l'adresse existe deja
            if (AD.adresseExist(search.getText().toString())){
                afficherToast("Cette adresse est deja en favoris");
            }else {
                AD.ajoutAdresse(search.getText().toString());
                afficherToast("Ajouté !!!");
            }
        }else {
            afficherToast("Lien Invalide");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        AD = new AccesDonnees(this);

        dm = (DownloadManager) getSystemService(this.DOWNLOAD_SERVICE);
        mProgressBar = findViewById(R.id.progressBar2);
        telechargement = (TextView)findViewById(R.id.TextView_Telechargement);
        Annuler = (Button)findViewById(R.id.button_annuler_ouvrir);
        downloadLayout = (LinearLayout)findViewById(R.id.Download_Layout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dm.remove(reference);
    }

    //mettre l'adresse retourné dans EditText
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                EditText search = (EditText)findViewById(R.id.EditText_Recherche);
                search.setText(data.getStringExtra("Adr"));
            }
        }
    }

    //Quand le telechargement est fini
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // récupérer la référence du téléchargement
            long ref = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (reference == ref) {
                // si OK alors plus besoin de BroadcastReceiver
                unregisterReceiver(this);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(reference);
                Cursor cursor = dm.query(query);
                if (cursor.moveToFirst()) {
                    String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (path == null) {
                        Toast.makeText(getApplicationContext(), "path est vide", Toast.LENGTH_LONG).show();
                        afficherTelechargementTerminee();

                        return;
                    }
                    EditText search = (EditText)findViewById(R.id.EditText_Recherche);
                    //récuperer le chemin de fichier xml telechargé
                    downloadFilePath = path;
                    //récuperer l'URL de fichier xml telechargé
                    String downloadLink = search.getText().toString();


                    //créer un objet de type RSSDocument
                    myRSSdocument = new RSSDocument(downloadFilePath,downloadLink);
                    //supprimer le fichier xml telechargé apres l'extraction des données
                    delete();
                    afficherTelechargementTerminee();

                    //date d'aujourd'hui
                    Date date = Calendar.getInstance().getTime();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String CurrentDate = format.format(date);

                    //si l'extraction des données est réussi
                    if (myRSSdocument.dataExtractionState.equals("OK")){
                        //inserer les données extracté dans la base de données
                        //inserer le chanel
                        AD.ajoutChannel(myRSSdocument.getDownloadLink(),myRSSdocument.getChannelTitle(),myRSSdocument.getChannelLink(),myRSSdocument.getChannelDescription(),CurrentDate);
                        //inserer les items
                        for(int i=0;i<myRSSdocument.getNumberOfitems();i++){
                            AD.ajoutItem(myRSSdocument.getChannelItems().get(i).getItemTitle(),
                                    myRSSdocument.getChannelItems().get(i).getItemLink(),
                                    myRSSdocument.getChannelItems().get(i).getItemDescription()
                                    ,myRSSdocument.getDownloadLink());
                        }
                        afficherToast("Extraction des données réussi");
                    }else{
                        afficherToast("Erreur d'extraction: "+myRSSdocument.dataExtractionState);
                    }
                }
            }
        }
    };
}
