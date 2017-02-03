package com.ahchim.android.musicplayer;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Ahchim on 2017-02-02.
 */

public class PlayerAdapter extends PagerAdapter{
    ArrayList<Music> datas;
    Context context;

    public PlayerAdapter(ArrayList<Music> datas, Context context){
        this.datas = datas;
        this.context = context;
    }

    // 데이터 총 개수
    @Override
    public int getCount() {
        return datas.size();
    }

    // instantiateItem 함수를 오버라이드한다.
    // listView의 getView와 같은 역할
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.player_card_item, null);

        ImageView imgView = (ImageView) view.findViewById(R.id.imgView);
        TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtArtist = (TextView) view.findViewById(R.id.txtArtist);

        // 실제 음악 데이터 가져오기
        Music music = datas.get(position);

        txtTitle.setText(music.getTitle());
        txtArtist.setText(music.getArtist());
        Glide.with(context).load(music.getAlbum_image()).placeholder(android.R.drawable.stat_sys_headset).into(imgView);

        // 생성한 뷰를 컨테이너에 담아준다. 컨테이너 = 뷰페이저를 생성한 최외곽 레이아웃 개념 (페이저 배열? 전체를 묶은것)
        container.addView(view);

        //return super.instantiateItem(container, position);
        // 컨테이너 중 화면에 보여지는 한개만 리턴함.
        return view;
    }

    // 변수명:타입 << kotlin 표현식
    // 시스템에서 자동으로
    // 화면에서 사라진 뷰를 메모리에서 제거하기 위한 함수.
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 기본형. 알아서 해줌
        //super.destroyItem(container, position, object);
        // 커스텀
        container.removeView((View)object);
    }

    // instantiateItem에서 리턴된 Object가 View가 맞는지를 확인하는 함수.
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
