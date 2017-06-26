package com.uninorte.pokemonv1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Andres Villa on 9/10/2016.
 */

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.PokemonViewHolder>  {
    ArrayList<DataPokeAtrapado> pokeAtra;
    ArrayList<DataImages> images;
    String tipo;
    ArrayList<DataItems> items;
    Activity activity;
    public RvAdapter(ArrayList<DataPokeAtrapado> pokeAtra,ArrayList<DataImages> images,String tipo,Activity activity){
        this.pokeAtra = pokeAtra;
        this.images = images;
        this.tipo = tipo;
        this.items = (ArrayList<DataItems>) new Select().from(DataItems.class).queryList();
        this.activity = activity;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PokemonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_pokemon, viewGroup, false);
        PokemonViewHolder pokemonViewHolder = new PokemonViewHolder(v);
        return pokemonViewHolder;
    }

    public void onBindViewHolder(PokemonViewHolder holder, int position) {
        long saludact = pokeAtra.get(position).saludAct;
        long salud = pokeAtra.get(position).salud;
        holder.pokeName.setText(pokeAtra.get(position).Nombre);
        holder.pokeHealth.setText(saludact+"/"+salud);
        holder.pokeAttack.setText(pokeAtra.get(position).ataque+"/"+pokeAtra.get(position).defensa);
        int progress = (int) ((int)(long)(saludact*100)/salud);
        holder.pokeBar.setMax((int)(long)salud);
        holder.pokeBar.setProgress(progress);
        int k = (int) (long) pokeAtra.get(position).idPoke;
        String img = images.get(k-1).data2;
        byte [] encodeByte= Base64.decode(img,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        float scaleWidth = 7;
        float scaleHeight = 7;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        Bitmap bitmap2 =  Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);

        holder.pokeImage.setImageBitmap(bitmap2);
        holder.tipo = tipo;
        holder.pokemonAct = pokeAtra.get(position);
        holder.items = items;
        holder.act1 = activity;
    }

    @Override
    public int getItemCount() {
        return pokeAtra.size();
    }

    public static class PokemonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView pokeName;
        TextView pokeHealth;
        TextView pokeAttack;
        ImageView pokeImage;
        ProgressBar pokeBar;
        String tipo;
        DataPokeAtrapado pokemonAct;
        ArrayList<DataItems> items;
        Activity act1;

        PokemonViewHolder(final View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            pokeName = (TextView)itemView.findViewById(R.id.tV_Nombre);
            pokeHealth = (TextView)itemView.findViewById(R.id.tV_Salud);
            pokeAttack = (TextView)itemView.findViewById(R.id.tV_Atq);
            pokeImage = (ImageView) itemView.findViewById(R.id.imagePoke);
            pokeBar = (ProgressBar) itemView.findViewById(R.id.progressBar3);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataItems dataItems = null;
                    if(tipo != null && pokemonAct!=null && !tipo.equals("Nada")){
                        long pokesalud = pokemonAct.saludAct;
                        long pokesaludMax = pokemonAct.salud;
                        Iterator<DataItems> iteratorItem = items.iterator();
                        while(iteratorItem.hasNext()){
                            DataItems item = iteratorItem.next();
                            if(item.tipo.equals(tipo)){
                                dataItems = item;
                            }
                        }

                        if(pokesalud < pokesaludMax && dataItems.numero > 0){
                            int aumento = 0;
                            switch(tipo){
                                case "Poción":
                                    aumento = 50;
                                    break;
                                case "Superpoción":
                                    aumento = 100;
                                    break;
                            }
                            long aum = (pokesaludMax*aumento)/100;
                            pokesalud = pokesalud + aum;
                            if (pokesalud > pokesaludMax){
                                pokesalud = pokesaludMax;
                            }
                            pokemonAct.saludAct = pokesalud;
                            pokemonAct.save();

                            dataItems.numero = dataItems.numero - 1;
                            dataItems.save();

                            pokeBar.setProgress((int)(long) pokesalud);
                            pokeHealth.setText(pokesalud+"/"+pokemonAct.salud);
                            Intent intent = new Intent(itemView.getContext(), itemActivity.class);
                            itemView.getContext().startActivity(intent);

                        }

                    }
                    else if(tipo!=null && tipo.equals("Nada") && pokemonAct != null){
                        if(pokemonAct.saludAct>0){
                            Intent returnIntent = new Intent(itemView.getContext(), BatallaActivity.class);
                            returnIntent.putExtra("Poke",pokemonAct.Nombre);
                            act1.setResult(Activity.RESULT_OK, returnIntent);
                            act1.finish();
                        }
                        else{
                            Toast.makeText(act1, "Seleccione un Pokemon que pueda luchar", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

}
