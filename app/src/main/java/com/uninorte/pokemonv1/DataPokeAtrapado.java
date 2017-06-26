package com.uninorte.pokemonv1;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Created by Andres Villa on 9/10/2016.
 */
@Table(database = AppDataBase.class)
public class DataPokeAtrapado extends BaseModel implements Serializable {

    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    public long idPoke;

    @Column
    public String Nombre;

    @Column
    public String type;

    @Column
    public String strength;

    @Column
    public String weakness;

    @Column
    public long salud;

    @Column
    public long saludAct;

    @Column
    public long ataque;

    @Column
    public long defensa;

    public DataPokeAtrapado(){};

    public DataPokeAtrapado(long data1, String data2, long data3, long data4, long data5, long data6, String data7, String data8, String data9){
        this.idPoke = data1;
        this.Nombre = data2;
        this.salud = data3;
        this.saludAct = data4;
        this.ataque = data5;
        this.defensa = data6;
        this.type = data7;
        this.strength = data8;
        this.weakness = data9;
    }

}
