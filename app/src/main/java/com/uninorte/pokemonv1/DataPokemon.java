package com.uninorte.pokemonv1;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Created by Andres Villa on 16/09/2016.
 */
@Table(database = AppDataBase.class)
public class DataPokemon extends BaseModel implements Serializable{

    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    public int idPo;

    @Column
    public String name;

    @Column
    public String type;

    @Column
    public String strength;

    @Column
    public String weakness;

    @Column
    public int hp_max;

    @Column
    public int attack_max;

    @Column
    public int defense_max;

    @Column
    public String imgfront;

    @Column
    public String imgback;

    @Column
    public int evol;


    public DataPokemon(){}

    public DataPokemon(int data1,String data2,String data3,String data4, String data5, int data6, int data7, int data8,
                       String data9,String data10, int data11){
        idPo=data1; name = data2; type=data3; strength = data4; weakness = data5; hp_max=data6; attack_max=data7; defense_max=data8;
        imgfront=data9; imgback=data10; evol = data11;
    }


}
