package com.ahchim.android.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ahchim on 2017-02-01.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder>{
    private ArrayList<Music> datas;
    private Context context;
    private Intent intent = null;

    public MusicAdapter(ArrayList<Music> datas, Context context){
        this.datas = datas;
        this.context = context;
        // 인텐트를 매번 부르지 않도록 해본다.
        this.intent = new Intent(context, PlayerActivity.class);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        Holder holder = new Holder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final Music music = datas.get(position);

        holder.getTxtTitle().setText(music.getTitle());
        holder.getTxtArtist().setText(music.getArtist());

        holder.setPosition(position);

        // 1. URI 직접 넣기 (가장 간단한 방법)
        //holder.getImgView().setImageURI(music.getAlbum_image());

        // 2. 앨범아트 BitMap으로 생성해서 넣기
        //if(music.getBitmap_image() != null) holder.getImgView().setImageBitmap(music.getBitmap_image());

        // 3. Glide 사용하기
        Glide.with(context).load(music.getAlbum_image()).into(holder.getImgView());
        //                       1. 로드할 대상 URI            2. 입력될 이미지뷰



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

        private int position;

        public Holder(View view) {
            super(view);

            this.txtTitle = (TextView) view.findViewById(R.id.txtTitle);
            this.txtArtist = (TextView) view.findViewById(R.id.txtArtist);
            this.imgView = (ImageView) view.findViewById(R.id.imgView);
            this.cardView = (CardView) view.findViewById(R.id.cardView);

            // Holder에 클릭리스너 코드를 넣는 것, 정말 최고임.
            cardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            });
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

        public void setPosition(int position) { this.position = position; }
    }
}
