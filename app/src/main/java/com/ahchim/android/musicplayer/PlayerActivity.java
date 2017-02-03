package com.ahchim.android.musicplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    ViewPager viewPager;
    ImageButton btnPlay, btnRew, btnFf;

    ArrayList<Music> datas;
    PlayerAdapter adapter;

    MediaPlayer player;
    SeekBar seekBar;
    TextView txtCurrent, txtDuration;

    // 플레이어 상태 플래그
    private static final int PLAY = 0;
    private static final int PAUSE = 1;
    private static final int STOP = 2;

    // 현재 플레이어 상태
    private static int playStatus = STOP;

    int position = 0;  // 현재 음악 위치

    // 핸들러 상태 플래그
//    public static final int PROGRESS_SET = 101;
//
//    // 핸들러
//    Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg){
//            switch(msg.what){
//                case PROGRESS_SET:
//                    if(player!=null) {
//                        seekBar.setProgress(player.getCurrentPosition());
//                        txtCurrent.setText(Util.milliSecToTime(player.getCurrentPosition()));
//                    }
//                    break;
//            }
//        }
//    };

    // onCreate 안에선 선언 안하는게 좋다.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // 이 어플에서는 미디어로 볼륨을 조절하겠다.
        // 볼륨 조절 버튼으로 미디어 음량만 조절하기 위한 설정
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        txtCurrent = (TextView) findViewById(R.id.txtCurrent);
        txtDuration = (TextView) findViewById(R.id.txtDuration);

        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnRew = (ImageButton) findViewById(R.id.btnRew);
        btnFf = (ImageButton) findViewById(R.id.btnFf);

        btnPlay.setOnClickListener(click);
        btnRew.setOnClickListener(click);
        btnFf.setOnClickListener(click);


        // 0. 데이터 가져오기
//        DataLoader loader = new DataLoader(this);
//        loader.load();
//        ArrayList<Music> datas = loader.get();
        // 싱글톤 패턴으로 바꿔보기
        // 이미 한번 세팅되었으면 다시 안 부르게 되었다!
        datas = DataLoader.get(this);

        // 1. 뷰페이저 가져오기
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        // 2. 뷰페이저용 아답터 생성
        adapter = new PlayerAdapter(datas, this);

        // 아답터는 위젯이 달라지면 사용할 수 없다. (리사이클러뷰 상속 아답터 -> 뷰페이저 이런거 안됨.)
        // 3. 뷰페이저 아답터 연결
        viewPager.setAdapter(adapter);

        // 4. 특정 페이지 호출
        Intent intent = getIntent();
        if(intent != null){
            Bundle bundle = intent.getExtras();
            position = bundle.getInt("position");

            // 실제 페이지 값 계산 처리

            // 페이지 이동
            viewPager.setCurrentItem(position);
        }
    }

    View.OnClickListener click = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btnPlay:
                    play();
                    break;
                case R.id.btnRew:
                    prev();
                    break;
                case R.id.btnFf:
                    next();
                    break;
            }
        }
    };

    private void play(){
        switch(playStatus){
            case STOP:
                Uri musicUri = datas.get(position).getUri();

                // 플레이어에 음원 세팅
                player = MediaPlayer.create(this, musicUri);          // 미디어플레이어도 싱글톤이당

                player.setLooping(false);  // 반복여부

                // seekBar 길이
                seekBar.setMax(player.getDuration());
                txtDuration.setText(Util.milliSecToTime(player.getDuration()));
                Log.i("전체음악길이", Util.milliSecToTime(player.getDuration()));

                player.start();

                playStatus = PLAY;
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);

                // sub thread를 생성해서 mediaplayer의 현재 포지션 값으로 seekbar를 변경해준다. 매 1초마다
                // PLAY 상태 아래에서 해 주자..
//                new Thread(){
//                    @Override
//                    public void run () {
//                        while (playStatus < STOP) {
//                            if (player != null) {
//                                handler.sendEmptyMessage(PROGRESS_SET);
//                            }
//                            // 너무 상세하게 체크가 돌아가면 렉을 유발하기 때문에 1초텀줌
//                            try { Thread.sleep(1000); } catch (InterruptedException e) { }
//                        }
//                    }
//                }.start();

                // sub thread를 생성해서 mediaplayer의 현재 포지션 값으로 seekbar를 변경해준다. 매 1초마다
                // runOnUiThread를 사용하면 핸들러 없이 직접 쓰레드 동작 가능하다.
                // while문을 바깥에 씌워주지 않으면 런메소드 사용 후 메시지큐에 들어가지 못하고 헛돈다.
                new Thread(){
                    @Override
                    public void run () {
                        while (playStatus < STOP) {
                            if (player != null) {
                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            seekBar.setProgress(player.getCurrentPosition());
                                            txtCurrent.setText(Util.milliSecToTime(player.getCurrentPosition()));
                                        }
                                    });
                                    // 너무 상세하게 체크가 돌아가면 렉을 유발하기 때문에 1초텀줌
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }.start();

                break;
            // 플레이중이면 멈춤
            case PLAY:
                player.pause();
                playStatus = PAUSE;
                btnPlay.setImageResource(android.R.drawable.ic_media_play);
                break;
            // 멈춤상태이면 거기서 부터 재생
            case PAUSE:
                //player.seekTo(player.getCurrentPosition());
                player.start();
                playStatus = PLAY;
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                break;

        }
    }

    private void prev(){

    }
    private void next(){

    }

    // 함수 오버라이드해서 player 해제한다.
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(player!=null){
            player.release();  // 사용이 끝나면 해제해야만 한다.
        }

        // 죽어 재생상태야!!
        playStatus = STOP;
    }
}
