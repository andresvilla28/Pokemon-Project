package com.uninorte.pokemonv1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int[] pokemoniniciales = {0,2,4};
    private int kp = (int) (Math.random()*3);
    private static final String TAG = "Etiquetas";
    private static final int MY_PERMISSIONSLOCATION = 1;
    private static final int MINUTOS = 5;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private AlertDialog alert;
    private GoogleMap googleMap;
    private CameraUpdate mCamera;
    private boolean estadoRed;
    private ArrayList<LatLng> posiciones;
    private ArrayList<DataPokemon> pokemon;
    private ArrayList<DataPokeAtrapado> pokemonAtrapados;
    private ArrayList<DataItems> dataIte;
    //private ArrayList<Bitmap> imagenPunto;
    private ArrayList<DataImages> dataImages;
    private boolean estado = false;
    private boolean consulta = false;
    private Map<String, Integer> ImgPos = new HashMap<String, Integer>();
    private int numPoke;
    private boolean imagDes = false;
    private boolean batalla;
    private final String[] pokeParadas = {"11.018842;-74.850575","11.017965;-74.850202"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        batalla = false;  //hay que cambiarlo a global y recogerlo cuando termine la batalla o la persona precione run

        FlowManager.init(new FlowConfig.Builder(this).build());
        estadoRed = verificarEstadoRed();

        dataImages = (ArrayList<DataImages>) new Select().from(DataImages.class).queryList();
        pokemon = (ArrayList<DataPokemon>) new Select().from(DataPokemon.class).queryList();
        pokemonAtrapados = (ArrayList<DataPokeAtrapado>) new Select().from(DataPokeAtrapado.class).queryList();
        dataIte = (ArrayList<DataItems>) new Select().from(DataItems.class).queryList();

        if(dataIte.isEmpty()){
            DataItems pokeballit = new DataItems("Pokeball",4,R.drawable.ic_launcher_pokeball);
            pokeballit.save();
            DataItems pocionit = new DataItems("Poción",4,R.drawable.ic_launcher_pocion);
            pocionit.save();
            DataItems superpocionit = new DataItems("Superpoción",4,R.drawable.ic_launcher_superpocion);
            superpocionit.save();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        TimerTask timerTask = new timer();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, MINUTOS * 60 * 1000);

        if(pokemon.isEmpty()){
            new GetPokemon().execute();
            if(dataImages.isEmpty()){
                imagDes = true;
            }
        }
        else{
            if(pokemonAtrapados.isEmpty()){
                int idP = pokemoniniciales[kp];
                DataPokeAtrapado pAtrapado = getPokemonAtrapado(pokemon.get(idP));
                pAtrapado.save();
                pokemonAtrapados = (ArrayList<DataPokeAtrapado>) new Select().from(DataPokeAtrapado.class).queryList();
            }
            numPoke = pokemon.size();
            if(dataImages.isEmpty()){
                new CargarImagenes().execute();
                imagDes = false;
            }
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            Log.d(TAG, "Connect");
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONSLOCATION);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended" + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed" + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng posicionP = new LatLng(location.getLatitude(), location.getLongitude());
        mCamera = CameraUpdateFactory.newLatLngZoom(posicionP, 18);
        googleMap.animateCamera(mCamera);
        if(!estado){
            if (!consulta){
                posiciones = null;
               new GetData().execute(location.getLatitude(),location.getLongitude());
                consulta = true;
                //imagenPunto = new ArrayList<Bitmap>();
            }
            if (posiciones != null && pokemon!= null){
                if(dataImages.isEmpty() && imagDes){
                    new CargarImagenes().execute();
                    imagDes = false;
                }
                else{
                    if(dataImages.size()==pokemon.size()){
                        Log.d(TAG,"Se va ejecutar el metodo puntoAleatorios");
                        puntosAleatorios();
                        estado = true;
                    }
                }

            }
        }
        else{
            if(!batalla){
                for (int a=0; a<posiciones.size();a++){
                    float dis = (float) Math.pow(Math.pow(posiciones.get(a).latitude-posicionP.latitude,2) + Math.pow(posiciones.get(a).longitude-posicionP.longitude,2),0.5)*10000;
                    Log.d(TAG,"DISTANCIAPUNTO: "+dis);
                    if(dis < 1.5){
                        Iterator<DataPokeAtrapado> ij = pokemonAtrapados.iterator();
                        int numVivos = 0;
                        while (ij.hasNext()){
                            DataPokeAtrapado act = ij.next();
                            if (act.saludAct>0){
                                numVivos++;
                            }
                        }
                        if (numVivos > 0){
                            batalla = true;
                            int idPoke = ImgPos.get(posiciones.get(a).toString());
                            Intent intent = new Intent(this, BatallaActivity.class);
                            intent.putExtra("idPokemon", idPoke);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(this,"No tienes pokemones para luchar", Toast.LENGTH_SHORT).show();
                        }
                    /*for(int b = 0; b<pokemon.size();b++){
                        if (pokemon.get(b).idPo == idPoke){
                            Toast.makeText(this, "Tiene cerca al pokemon: "+pokemon.get(b).name,Toast.LENGTH_LONG).show();
                        }
                    }*/

                    }
                }
                for(int ca = 0; ca<pokeParadas.length; ca++){
                    String temp = pokeParadas[ca];
                    String[] res = temp.split(";");
                    float dis2 = (float) Math.pow(Math.pow(Double.parseDouble(res[0])-posicionP.latitude,2) + Math.pow(Double.parseDouble(res[1])-posicionP.longitude,2),0.5)*10000;
                    Log.d("PokeParada","distancia: "+dis2);
                    dataIte = (ArrayList<DataItems>) new Select().from(DataItems.class).queryList();
                    if (dis2<1.5){
                        Iterator<DataItems> iteratorItem = dataIte.iterator();
                        boolean findO = false;
                        while(iteratorItem.hasNext() && !findO){
                            if(iteratorItem.next().numero<4){
                                findO = true;
                            }
                        }
                        if(findO){
                            Iterator<DataItems> iteratorItem2 = dataIte.iterator();
                            while(iteratorItem2.hasNext()){
                                DataItems tempItem = iteratorItem2.next();
                                int num = tempItem.numero;
                                num = num + 4;
                                if (num > 4){
                                    num = 4;
                                }
                                tempItem.numero = num;
                                tempItem.save();
                            }
                            Toast.makeText(this, "Su mochila se ha actualizado", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONSLOCATION: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        ActivarGps();
                    } else {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                        googleMap.getUiSettings().setZoomGesturesEnabled(false);
                        googleMap.getUiSettings().setScrollGesturesEnabled(false);
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);


                    }
                } else {

                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onConnected(Bundle.EMPTY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alert != null)
            alert.dismiss();
    }

    private void ActivarGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public boolean verificarEstadoRed() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean res = true;
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(this, "La red no esta disponible", Toast.LENGTH_LONG).show();
            res = false;
        }
        return res;
    }

    public void puntosAleatorios() {
        if (posiciones != null) {
            googleMap.clear();
            ImgPos.clear();
            int num = numPoke - 1;
            for (int j = 0; j < posiciones.size(); j++) {
                int k = (int) (Math.random()*num);
                String img = dataImages.get(k).data2;
                byte [] encodeByte= Base64.decode(img,Base64.DEFAULT);
                Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

                float scaleWidth = 2;
                float scaleHeight = 2;
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth,scaleHeight);
                Bitmap bitmap2 =  Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);

                googleMap.addMarker(new MarkerOptions().position(posiciones.get(j)).icon(BitmapDescriptorFactory.fromBitmap(bitmap2)));
                ImgPos.put(posiciones.get(j).toString(),pokemon.get(k).idPo);
                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        View ventInfo = getLayoutInflater().inflate(R.layout.information_cas,null);
                        TextView tvInfo = (TextView) ventInfo.findViewById(R.id.titleDialog);
                        Button bRute = (Button) ventInfo.findViewById(R.id.buttonDialog);
                        ImageView ivPokeInfo = (ImageView) ventInfo.findViewById(R.id.iconPoke);
                        int idPoke = ImgPos.get(marker.getPosition().toString());
                        tvInfo.setText(pokemon.get(idPoke-1).name);
                        return ventInfo;
                    }
                });
                /*googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        LatLng posM = marker.getPosition();
                        Toast.makeText(MainActivity.this,"Latitud: "+posM.latitude+" Longitud: "+posM.longitude,Toast.LENGTH_LONG).show();
                        return false;
                    }
                });*/
            }

            for(int c = 0; c<pokeParadas.length; c++){
                String temp = pokeParadas[c];
                String[] res = temp.split(";");
                LatLng posPokeP = new LatLng(Double.parseDouble(res[0]),Double.parseDouble(res[1]));
                googleMap.addMarker(new MarkerOptions().position(posPokeP).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_parada)));
            }
        }
    }

    public Bitmap obtImagen(String direccionWeb) {

        try {
            URL imageUrl = new URL(direccionWeb);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            Bitmap imagen = BitmapFactory.decodeStream(conn.getInputStream());
            return imagen;
        } catch (IOException e) {
            return null;
        }
    }

    protected String getData(String web) {
        try {
            String direccion = web;
            URL url = new URL(direccion);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputText = "";
            boolean st = true;
            while (st) {
                String val = reader.readLine();
                if (val == null) {
                    st = false;
                } else {
                    inputText = inputText + val;
                }
            }
            reader.close();
            return inputText;
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

    }

    public void onClickMochila(View view) {

        Intent intent = new Intent(this,BagActivity.class);
        startActivity(intent);
    }

    public DataPokeAtrapado getPokemonAtrapado(DataPokemon pokemon){
        int k1 = (int) (Math.random()*80+10);
        int salud  = (pokemon.hp_max*k1)/100;
        int k2 = (int) (Math.random()*80+10);
        int ataque = (pokemon.attack_max*k2)/100;
        int k3 = (int) (Math.random()*80+10);
        int defensa = (pokemon.defense_max*k3)/100;
        DataPokeAtrapado pokemonAtra = new DataPokeAtrapado(pokemon.idPo,pokemon.name,salud,salud,ataque,defensa,pokemon.type,pokemon.strength,pokemon.weakness);
        return pokemonAtra;
    }

    private class GetData extends AsyncTask<Double, Void, Void> {

        @Override
        protected Void doInBackground(Double... doubles) {
            posiciones = new ArrayList<LatLng>();
            String dirWeb = "http://190.144.171.172/function3.php?lat=" + doubles[0] + "&lng=" + doubles[1];
            String response = getData(dirWeb);
            if (response != null) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        posiciones.add(new LatLng(object.getDouble("lt"), object.getDouble("lng")));
                    }
                    //puntosAleatorios(posiciones);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class GetPokemon extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //pokemon = new ArrayList<Pokemon>();
            String dirWebPoke = "http://190.144.171.172/proyectoMovil/pokemonlist17.php";
            String responsePoke = getData(dirWebPoke);
            if (responsePoke != null) {
                try {
                    JSONArray jsonArray1 = new JSONArray(responsePoke);
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject object1 = jsonArray1.getJSONObject(j);
                        DataPokemon poke = new DataPokemon(object1.getInt("id"), object1.getString("name"), object1.getString("type"), object1.getString("strength"), object1.getString("weakness"),
                                object1.getInt("hp_max"), object1.getInt("ataque_max"), object1.getInt("defensa_max"), object1.getString("ImgFront"), object1.getString("ImgBack"), object1.getInt("ev_id"));
                        if (poke.idPo > 0) {
                            poke.save();
                        }
                        //pokemon.add(poke);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pokemon = (ArrayList<DataPokemon>) new Select().from(DataPokemon.class).queryList();
                numPoke = pokemon.size();
                int idP = pokemoniniciales[kp];
                DataPokeAtrapado pAtrapado = getPokemonAtrapado(pokemon.get(idP));
                pAtrapado.save();
                pokemonAtrapados = (ArrayList<DataPokeAtrapado>) new Select().from(DataPokeAtrapado.class).queryList();
                Log.d(TAG,pokemonAtrapados.get(0).Nombre);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    private class CargarImagenes extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Bitmap imagenDes;
            Bitmap imagenDesBack;
            int ind = 0;
            DataPokemon poke;
            while(dataImages.size()<pokemon.size()){
                poke = pokemon.get(ind);
                imagenDes = obtImagen(poke.imgfront);
                String imagenDescod = codificarImagen(imagenDes);
                imagenDesBack = obtImagen(poke.imgback);
                String imagenDesBackcod = codificarImagen(imagenDesBack);
                new DataImages(poke.idPo,imagenDescod,imagenDesBackcod).save();
                dataImages = (ArrayList<DataImages>) new Select().from(DataImages.class).queryList();
                ind++;
            }
            Log.d(TAG,"Se Han descargado todas las imagenes");
            return null;
        }
    }

    public String codificarImagen(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private class timer extends TimerTask {

        @Override
        public void run() {
            estado = false;
            consulta = false;
            Log.d(TAG,"Por cambiar puntos");

        }
    }

}

