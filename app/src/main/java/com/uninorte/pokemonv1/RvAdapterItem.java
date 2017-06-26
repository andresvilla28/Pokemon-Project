package com.uninorte.pokemonv1;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Andres Villa on 11/10/2016.
 */

public class RvAdapterItem  extends RecyclerView.Adapter<RvAdapterItem.ItemViewHolder>   {
    ArrayList<DataItems> items;
    int act;
    Activity activity;
    public RvAdapterItem(ArrayList<DataItems> items, int act, Activity activity){
        this.items = items;
        this.act = act;
        this.activity = activity;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_item, viewGroup, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(v,activity);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        String nombre = items.get(position).tipo;
        int numero = items.get(position).numero;
        int imagen = items.get(position).imagen;
        holder.tVItem.setText(nombre+"  "+"x"+numero);
        holder.imageView.setImageResource(imagen);
        holder.currentTipo = items.get(position).tipo;
        holder.num = items.get(position).numero;
        holder.act = act;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private Activity mActivity;
        CardView cvItem;
        TextView tVItem;
        ImageView imageView;
        String currentTipo;
        int act;
        int num;

        ItemViewHolder(final View itemView, final Activity mActivity) {
            super(itemView);
            this.mActivity = mActivity;
            imageView = (ImageView) itemView.findViewById(R.id.iVitem);
            cvItem = (CardView)itemView.findViewById(R.id.cvItem);
            tVItem = (TextView)itemView.findViewById(R.id.tVitem);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (act == 1){
                        if(!currentTipo.equals("Pokeball") && num > 0){
                            Intent returnIntent = new Intent(itemView.getContext(), BatallaActivity.class);
                            returnIntent.putExtra("tipo",currentTipo);
                            mActivity.setResult(Activity.RESULT_OK, returnIntent);
                            mActivity.finish();
                        }
                    }
                    if(act == 0){
                        if(!currentTipo.equals("Pokeball") && num > 0){
                            Intent intent = new Intent(itemView.getContext(), PokemonMain.class);
                            intent.putExtra("tipo",currentTipo);
                            itemView.getContext().startActivity(intent);
                        }
                    }
                }
            });
        }
    }
}
