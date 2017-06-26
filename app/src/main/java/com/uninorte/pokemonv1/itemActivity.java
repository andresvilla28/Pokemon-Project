package com.uninorte.pokemonv1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;

public class itemActivity extends AppCompatActivity {

    private ArrayList<DataItems> dataItems;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        int actividad = getIntent().getIntExtra("value",0);
        recyclerView = (RecyclerView) findViewById(R.id.rvItem);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        dataItems = (ArrayList<DataItems>) new Select().from(DataItems.class).queryList();
        RvAdapterItem rvAdapterItem = new RvAdapterItem(dataItems,actividad,this);
        recyclerView.setAdapter(rvAdapterItem);

    }
}
