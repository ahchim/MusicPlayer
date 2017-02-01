package com.ahchim.android.musicplayer;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ahchim on 2017-02-01.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder>{
    private ArrayList<Music> datas;
    private Context context;

    public MusicAdapter(ArrayList<Music> datas, Context context){
        this.datas = datas;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final Music music = datas.get(position);

        holder.getTxtTitle().setText(music.getTitle());
        holder.getTxtArtist().setText(music.getArtist());

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        holder.cardView.setAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        private TextView txtTitle, txtArtist;
        private ImageView imgView;
        private CardView cardView;

        public Holder(View view) {
            super(view);

            this.txtTitle = (TextView) view.findViewById(R.id.txtTitle);
            this.txtArtist = (TextView) view.findViewById(R.id.txtArtist);
            this.imgView = (ImageView) view.findViewById(R.id.imgView);
            this.cardView = (CardView) view.findViewById(R.id.cardView);
        }

        public TextView getTxtTitle() {
            return txtTitle;
        }

        public TextView getTxtArtist() {
            return txtArtist;
        }

        public ImageView getImgView() {
            return imgView;
        }

        public CardView getCardView() {
            return cardView;
        }
    }
}
