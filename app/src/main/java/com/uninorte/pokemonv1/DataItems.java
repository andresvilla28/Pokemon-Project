package com.uninorte.pokemonv1;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Created by Andres Villa on 11/10/2016.
 */

@Table(database = AppDataBase.class)
public class DataItems extends BaseModel implements Serializable {
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    public String tipo;

    @Column
    public int numero;

    @Column
    public int imagen;

    public DataItems(){}

    public DataItems(String data1, int data2, int data3){
        tipo = data1;
        numero = data2;
        imagen = data3;
    }
}
