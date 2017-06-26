package com.uninorte.pokemonv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class BagActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag);
    }

    public void onClickPokemon(View view) {
        Intent int1 = new Intent(this,PokemonMain.class);
        startActivity(int1);

    }

    public void onClickPokeMochila(View view) {
        Intent int2 = new Intent(this,itemActivity.class);
        int2.putExtra("value",0);
        startActivity(int2);
    }
}
