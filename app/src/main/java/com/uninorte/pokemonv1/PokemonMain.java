package com.uninorte.pokemonv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;

public class PokemonMain extends AppCompatActivity {

    private ArrayList<DataImages> images;
    private ArrayList<DataPokeAtrapado> pokeAtrapados;
    private RecyclerView rv;
    protected String tipo;
    private String TAG1 = "Pokemochila";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_main);

        rv=(RecyclerView)findViewById(R.id.rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);

        Intent i = getIntent();
        tipo = i.getStringExtra("tipo");

        images = (ArrayList<DataImages>) new Select().from(DataImages.class).queryList();
        pokeAtrapados = (ArrayList<DataPokeAtrapado>) new Select().from(DataPokeAtrapado.class).queryList();
        RvAdapter adapter = new RvAdapter(pokeAtrapados,images,tipo,this);
        rv.setAdapter(adapter);
    }

}
