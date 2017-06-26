package com.uninorte.pokemonv1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BatallaActivity extends AppCompatActivity {

    private ArrayList<DataImages> imagenes;
    private ArrayList<DataPokeAtrapado> pokeAtrapados;
    private ArrayList<DataPokemon> pokemon;
    private ArrayList<DataItems> items;
    ImageView imageRival;
    ImageView imagePropio;
    ProgressBar saludRival;
    ProgressBar saludPropio;
    TextView nameRival;
    TextView namePropio;
    TextView tvSaludRival;
    TextView tvSaludPropio;
    TextView batalla;

    String imgRival = "";
    String imgPokepro = "";

    DataPokemon pokerival;
    int idPokerival;
    int saludactRival;
    int ataquerival;
    int defensarival;
    int saludMaxrival;

    int saludpro;
    int saludproMax;
    String nombrePokePro;
    int idPokepro;
    DataPokeAtrapado pokepro;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batalla);
        imageRival = (ImageView) findViewById(R.id.imageView2);
        imagePropio = (ImageView) findViewById(R.id.imageView3);
        saludRival = (ProgressBar) findViewById(R.id.progressBar7);
        saludPropio = (ProgressBar) findViewById(R.id.progressBar8);
        nameRival = (TextView) findViewById(R.id.textView6);
        namePropio = (TextView) findViewById(R.id.textView7);
        tvSaludRival = (TextView) findViewById(R.id.textView8);
        tvSaludPropio = (TextView) findViewById(R.id.textView9);
        batalla = (TextView) findViewById(R.id.textView5);

        imagenes = (ArrayList<DataImages>) new Select().from(DataImages.class).queryList();
        pokeAtrapados = (ArrayList<DataPokeAtrapado>) new Select().from(DataPokeAtrapado.class).queryList();
        pokemon = (ArrayList<DataPokemon>) new Select().from(DataPokemon.class).queryList();
        items = (ArrayList<DataItems>) new Select().from(DataItems.class).queryList();

        if(savedInstanceState == null){

            idPokerival = getIntent().getExtras().getInt("idPokemon",0);
            //pokepro = pokeAtrapados.get(0);

            Iterator<DataPokeAtrapado> iN = pokeAtrapados.iterator();
            Boolean res = true;
            while (iN.hasNext() && res){
                DataPokeAtrapado act = iN.next();
                if (act.saludAct>0){
                    pokepro = act;
                    res = false;
                }
            }
            idPokepro =  (int)(long) pokepro.idPoke;

            pokerival = pokemon.get(idPokerival-1);

            for(int i = 0; i<imagenes.size(); i++){
                DataImages img = imagenes.get(i);
                int idimg = (int) (long) img.data1;
                if (idimg == idPokerival){
                    imgRival = img.data2;
                }
                if (idimg == idPokepro){
                    imgPokepro = img.data3;
                }
            }

            saludpro = (int) (long) pokepro.saludAct;

            int kr1 = (int) (Math.random()*80+20);
            saludMaxrival  = (pokerival.hp_max*kr1)/100;
            int kr2 = (int) (Math.random()*80+10);
            ataquerival = (pokerival.attack_max*kr2)/100;
            int kr3 = (int) (Math.random()*80+10);
            defensarival = (pokerival.defense_max*kr3)/100;

            saludactRival = saludMaxrival;
        }
        else{

            idPokerival = savedInstanceState.getInt("idPokerival");
            idPokepro = savedInstanceState.getInt("idPokepro");
            for(int iP=0; iP<pokeAtrapados.size(); iP++){
                int idPo = (int) (long) pokeAtrapados.get(iP).idPoke;
                if( idPo == idPokepro){
                    pokepro = pokeAtrapados.get(iP);
                }
            }
            pokerival = pokemon.get(idPokerival-1);
            imgPokepro = savedInstanceState.getString("imgPokepro");
            imgRival = savedInstanceState.getString("imgRival");

            saludpro = savedInstanceState.getInt("saludpro");

            saludMaxrival = savedInstanceState.getInt("saludMaxrival");
            ataquerival = savedInstanceState.getInt("ataquerival");
            defensarival = savedInstanceState.getInt("defensarival");
            saludactRival = savedInstanceState.getInt("saludactRival");
        }

        byte [] encodeByte= Base64.decode(imgRival,Base64.DEFAULT);
        Bitmap ImagenRiv= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        byte [] encodeByte2= Base64.decode(imgPokepro,Base64.DEFAULT);
        Bitmap ImagenPoke= BitmapFactory.decodeByteArray(encodeByte2, 0, encodeByte2.length);

        float scaleWidth = 22;
        float scaleHeight = 22;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        Bitmap ImagenRiv2 =  Bitmap.createBitmap(ImagenRiv,0,0,ImagenRiv.getWidth(),ImagenRiv.getHeight(),matrix,false);
        Bitmap ImagenPoke2 =  Bitmap.createBitmap(ImagenPoke,0,0,ImagenPoke.getWidth(),ImagenPoke.getHeight(),matrix,false);

        imageRival.setImageBitmap(ImagenRiv2);
        imagePropio.setImageBitmap(ImagenPoke2);

        nombrePokePro = pokepro.Nombre;
        namePropio.setText(nombrePokePro);
        saludproMax = (int) (long) pokepro.salud;

        saludPropio.setMax(saludproMax);
        saludPropio.setProgress(saludpro);
        tvSaludPropio.setText(saludpro+"/"+saludproMax);

        nameRival.setText(pokerival.name);

        saludRival.setMax(saludMaxrival);
        saludRival.setProgress(saludactRival);
        tvSaludRival.setText(saludactRival+"/"+saludMaxrival);

        batalla.setText("What will"+" "+pokepro.Nombre+" "+"do?");

    }

    public void onSaveInstanceState(Bundle outState){
        outState.putInt("idPokepro",idPokepro);
        outState.putInt("saludpro",saludpro);
        outState.putInt("saludactRival",saludactRival);
        outState.putInt("saludMaxrival",saludMaxrival);
        outState.putInt("ataquerival",ataquerival);
        outState.putInt("defensarival",defensarival);
        outState.putString("imgPokepro",imgPokepro);
        outState.putString("imgRival",imgRival);
        outState.putInt("idPokerival",idPokerival);
        super.onSaveInstanceState(outState);
    }


    public void onClickFight(View view) {
        String typePro = pokepro.type;
        String typeRival = pokerival.type;
        double factor = 0;
        double factor2 = 0;
        if (typePro == typeRival){
            factor = 0.5;
            factor2 = 0.5;
        }
        else if (typeRival == pokepro.strength){
            factor = 1;
        }
        else if (typeRival == pokepro.weakness){
            factor = 1/(pokepro.ataque - defensarival);
        }
        else{
            factor = 0.75;
        }
        double daño = (pokepro.ataque - defensarival)*factor;
        if (daño < 1){
            daño = 1;
        }
        if (typePro == pokerival.strength){
            factor2 = 1;
        }
        else if (typePro == pokerival.weakness){
            factor2 = 1/(ataquerival - pokepro.defensa);
        }
        else {
            factor2 = 0.75;
        }
        double daño2 = (ataquerival - pokepro.defensa)*factor2;
        if (daño2<1){
            daño2 = 1;
        }

        saludactRival = saludactRival - (int) daño;
        if (saludactRival < 0){
            saludactRival = 0;
        }
        saludRival.setProgress(saludactRival);
        tvSaludRival.setText(saludactRival+"/"+saludMaxrival);

        saludpro = saludpro - (int) daño2;
        if(saludpro < 0){
            saludpro = 0;
        }
        saludPropio.setProgress(saludpro);
        tvSaludPropio.setText(saludpro+"/"+saludproMax);

        if(saludpro == 0){
            Iterator<DataPokeAtrapado> i = pokeAtrapados.iterator();
            while (i.hasNext()){
                DataPokeAtrapado act = i.next();
                if (act.idPoke == pokepro.idPoke){
                    act.saludAct = saludpro;
                }
                act.save();
            }

            pokeAtrapados = (ArrayList<DataPokeAtrapado>) new Select().from(DataPokeAtrapado.class).queryList();
            Iterator<DataPokeAtrapado> ij = pokeAtrapados.iterator();
            int numVivos = 0;
            while (ij.hasNext()){
                DataPokeAtrapado act = ij.next();
                if (act.saludAct>0){
                    numVivos++;
                }
            }
            if (numVivos > 0){
                Intent iChange = new Intent(this, PokemonMain.class);
                iChange.putExtra("tipo","Nada");
                startActivityForResult(iChange,2);
            }
            else{
                Toast.makeText(this,"No tienes pokemones para luchar", Toast.LENGTH_SHORT).show();
                Intent intentLose = new Intent(this,MainActivity.class);
                startActivity(intentLose);
            }
        }

        if(saludactRival == 0){
            Intent i = new Intent(this,MainActivity.class);

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(BatallaActivity.this,MainActivity.class);
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            DataPokeAtrapado pokeRivalPro = new DataPokeAtrapado(pokerival.idPo,pokerival.name,pokerival.hp_max,saludactRival,ataquerival,defensarival,pokerival.type,pokerival.strength,pokerival.weakness);
                            Iterator<DataItems> itemIt = items.iterator();
                            while(itemIt.hasNext()){
                                DataItems item = itemIt.next();
                                if (item.tipo.equals("Pokeball")){
                                    if (item.numero > 0){
                                        item.numero = item.numero - 1;
                                        item.save();
                                        pokeRivalPro.save();
                                    }
                                    else{
                                        Toast.makeText(BatallaActivity.this, "No hay pokebalas", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            Iterator<DataPokemon> iterator = pokemon.iterator();
                            DataPokemon pokeProGe = null;
                            while (iterator.hasNext()){
                                DataPokemon dataPokemon = iterator.next();
                                if(dataPokemon.name.equals(pokepro.Nombre)){
                                    pokeProGe = dataPokemon;
                                }
                            }
                            Iterator<DataPokeAtrapado> i = pokeAtrapados.iterator();
                            while (i.hasNext()){
                                DataPokeAtrapado act = i.next();
                                if (act.idPoke == pokepro.idPoke){
                                    act.saludAct = saludpro;
                                    int atqP = (int) (long) act.ataque;
                                    int defP = (int) (long) act.defensa;
                                    atqP++;
                                    defP++;
                                    if (atqP > pokeProGe.attack_max){
                                        atqP = pokeProGe.attack_max;
                                    }
                                    if (defP > pokeProGe.defense_max){
                                        defP = pokeProGe.defense_max;
                                    }
                                    act.defensa = defP;
                                    act.ataque = atqP;
                                    if (defP == pokeProGe.defense_max && atqP == pokeProGe.attack_max){
                                        int idEvol = pokeProGe.evol;
                                        Iterator<DataPokemon> iteratorE = pokemon.iterator();
                                        DataPokemon pokeEvol = null;
                                        while (iteratorE.hasNext()){
                                            DataPokemon datapokeEvol = iteratorE.next();
                                            if(datapokeEvol.idPo == idEvol){
                                                pokeEvol = datapokeEvol;
                                            }
                                        }
                                        act.salud = pokeEvol.hp_max;
                                        act.Nombre = pokeEvol.name;
                                        act.idPoke = pokeEvol.idPo;
                                        act.type = pokeEvol.type;
                                        act.strength = pokeEvol.strength;
                                        act.weakness = pokeEvol.weakness;
                                    }
                                }
                                act.save();
                            }
                            dialog.dismiss();
                            startActivity(intent);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            Iterator<DataPokemon> iterator2 = pokemon.iterator();
                            DataPokemon pokeProGe2 = null;
                            while (iterator2.hasNext()){
                                DataPokemon dataPokemon = iterator2.next();
                                if(dataPokemon.name.equals(pokepro.Nombre)){
                                    pokeProGe2 = dataPokemon;
                                }
                            }
                            Iterator<DataPokeAtrapado> i2 = pokeAtrapados.iterator();
                            while (i2.hasNext()){
                                DataPokeAtrapado act = i2.next();
                                if (act.idPoke == pokepro.idPoke){
                                    act.saludAct = saludpro;
                                    int atqP = (int) (long) act.ataque;
                                    int defP = (int) (long) act.defensa;
                                    atqP++;
                                    defP++;
                                    if (atqP > pokeProGe2.attack_max){
                                        atqP = pokeProGe2.attack_max;
                                    }
                                    if (defP > pokeProGe2.defense_max){
                                        defP = pokeProGe2.defense_max;
                                    }
                                    act.defensa = defP;
                                    act.ataque = atqP;
                                    if (defP == pokeProGe2.defense_max && atqP == pokeProGe2.attack_max){
                                        int idEvol = pokeProGe2.evol;
                                        Iterator<DataPokemon> iteratorE = pokemon.iterator();
                                        DataPokemon pokeEvol = null;
                                        while (iteratorE.hasNext()){
                                            DataPokemon datapokeEvol = iteratorE.next();
                                            if(datapokeEvol.idPo == idEvol){
                                                pokeEvol = datapokeEvol;
                                            }
                                        }
                                        act.salud = pokeEvol.hp_max;
                                        act.Nombre = pokeEvol.name;
                                        act.idPoke = pokeEvol.idPo;
                                        act.type = pokeEvol.type;
                                        act.strength = pokeEvol.strength;
                                        act.weakness = pokeEvol.weakness;
                                    }
                                }
                                act.save();
                            }
                            dialog.dismiss();
                            startActivity(intent);
                            break;

                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Desea atrapar a "+pokerival.name).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();


        }
        Log.d("ataque","Daño: "+daño);
        Log.d("ataque","Tipo rival: "+typeRival);
        Log.d("ataque","Tipo propio: "+typePro);


    }

    public void onClickBag(View view) {
        Intent i = new Intent(this, itemActivity.class);
        i.putExtra("value",1);
        startActivityForResult(i,1);
    }

    public void onClickRun(View view) {
        Iterator<DataPokeAtrapado> i = pokeAtrapados.iterator();
        while (i.hasNext()){
            DataPokeAtrapado act = i.next();
            if (act.idPoke == pokepro.idPoke){
                act.saludAct = saludpro;
            }
            act.save();
        }
        Intent intentrun = new Intent(this,MainActivity.class);
        startActivity(intentrun);
    }

    public void onClickPokemon(View view) {
        Intent i2 = new Intent(this, PokemonMain.class);
        i2.putExtra("tipo","Nada");
        startActivityForResult(i2,2);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data1) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String tipo = data1.getStringExtra("tipo");
                DataItems itemsel = null;
                for(int j = 0; j<items.size(); j++){
                    if(items.get(j).tipo.equals(tipo)){
                        itemsel = items.get(j);
                    }
                }
                if (saludpro < saludproMax){
                    int aumento = 0;
                    switch(tipo){
                        case "Poción":
                            aumento = 50;
                            break;
                        case "Superpoción":
                            aumento = 100;
                            break;
                    }
                    int aum = (saludproMax*aumento)/100;
                    saludpro = saludpro + aum;
                    if (saludpro > saludproMax){
                        saludpro = saludproMax;
                    }
                    int numero = itemsel.numero;
                    itemsel.numero = numero - 1;
                    itemsel.save();
                    saludPropio.setProgress(saludpro);
                    tvSaludPropio.setText(saludpro+"/"+saludproMax);
                }
                else{
                    Toast.makeText(this,"La salud de "+nombrePokePro+" está al máximo",Toast.LENGTH_SHORT).show();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 2) {
            DataPokeAtrapado pokeNuevo = null;
            if (resultCode == Activity.RESULT_OK) {
                String NamePoke = data1.getStringExtra("Poke");
                Iterator<DataPokeAtrapado> i = pokeAtrapados.iterator();
                while (i.hasNext()){
                    DataPokeAtrapado act = i.next();
                    if (act.idPoke == pokepro.idPoke){
                        act.saludAct = saludpro;
                    }
                    if (act.Nombre.equals(NamePoke)){
                        pokeNuevo = act;
                    }
                    act.save();
                }
                pokepro = pokeNuevo;
                idPokepro = (int) (long) pokepro.idPoke;
                for(int iP = 0; iP<imagenes.size(); iP++){
                    DataImages img = imagenes.get(iP);
                    int idimg = (int) (long) img.data1;
                    if (idimg == idPokepro){
                        imgPokepro = img.data3;
                    }
                }
                saludpro = (int) (long) pokepro.saludAct;

                byte [] encodeByte2= Base64.decode(imgPokepro,Base64.DEFAULT);
                Bitmap ImagenPoke= BitmapFactory.decodeByteArray(encodeByte2, 0, encodeByte2.length);

                float scaleWidth = 22;
                float scaleHeight = 22;
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth,scaleHeight);
                Bitmap ImagenPoke2 =  Bitmap.createBitmap(ImagenPoke,0,0,ImagenPoke.getWidth(),ImagenPoke.getHeight(),matrix,false);

                imagePropio.setImageBitmap(ImagenPoke2);

                nombrePokePro = pokepro.Nombre;
                namePropio.setText(nombrePokePro);
                saludproMax = (int) (long) pokepro.salud;

                saludPropio.setMax(saludproMax);
                saludPropio.setProgress(saludpro);
                tvSaludPropio.setText(saludpro+"/"+saludproMax);


            }
        }

    }
}
