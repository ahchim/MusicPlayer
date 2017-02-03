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
    private static final int PLAY = 0, PAUSE = 1, STOP = 2;

    // 현재 플레이어 상태
    private static int playStatus = STOP;

    int position = 0;  // 현재 음악 위치

    // 핸들러 상태 플래그
    public static final int PROGRESS_SET = 101;

    // 핸들러
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
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

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

        // 리스너만 연결하고 init에 플레이어 초기화만 매번 해주면 페이저 넘어가면서 노래 바뀐다.
        // 4. 뷰페이저 리스너 연결
        viewPager.addOnPageChangeListener(viewPagerListener);

        // 5. 특정 페이지 호출
        Intent intent = getIntent();
        if(intent != null){
            Bundle bundle = intent.getExtras();
            position = bundle.getInt("position");

            // 음원길이 등의 음악 기본정보를 설정해 준다.
            // 첫페이지일 경우만 init 호출
            // 이유 : 첫페이지가 아닐경우 위의 setCurrentItem에 의해서 ViewPager의 onPageSelected가 호출된다.
            // 0번이 아닌 n페이지일 경우 0페이지 들어갔다가 n페이지가 호출됨.
            // 0페이지에서 0페이지로 갈 경우 리스너가 동작을 안해서 이짓함.
            if(position == 0) init();
            // 실제 페이지 값 계산 처리 - 페이지 이동
            else viewPager.setCurrentItem(position);
        }
    }

    // 씤바seekbar 체인지 리스너
    SeekBar.OnSeekBarChangeListener seekBarChangeListener= new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // User한테 터치받았을 때만 플레이어가 seekBar 움직임 동작을 한다.
            if(player!=null && fromUser) player.seekTo(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    };

    // 뷰페이저 체인지 리스너
    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
        @Override
        public void onPageSelected(int position) {
            PlayerActivity.this.position = position;
            init();
        }
        @Override
        public void onPageScrollStateChanged(int state) { }
    };

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

    private void init(){
        // 뷰페이저로 이동할 경우 플레이어에 세팅된 값을 해제한후 로직을 실행한다.
        if(player != null) {
            // 플레이 상태를 STOP으로 변경
            playStatus = STOP;
            // 아이콘을 플레이 버튼으로 변경
            btnPlay.setImageResource(android.R.drawable.ic_media_play);
            player.release();
        }

        Uri musicUri = datas.get(position).getUri();

        // 플레이어에 음원 세팅
        player = MediaPlayer.create(this, musicUri);          // 미디어플레이어도 싱글톤이당
        player.setLooping(false);  // 반복여부

        // seekBar 길이
        seekBar.setMax(player.getDuration());

        // seekBar 초기화
        seekBar.setProgress(0);

        // 전체 플레이시간 설정
        txtDuration.setText(Util.milliSecToTime(player.getDuration()));

        // 현재 플레이시간을 0으로 설정
        txtCurrent.setText("00:00");

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
            }
        });
        // Log.i("전체음악길이", Util.milliSecToTime(player.getDuration()));
        play();
    }

    private void play(){
        switch(playStatus){
            case STOP:
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
//                                // 핸들러!
//                                handler.sendEmptyMessage(PROGRESS_SET);
//                                // 핸들러어어어
//                            }
//                            // 너무 상세하게 체크가 돌아가면 렉을 유발하기 때문에 1초텀줌
//                            try { Thread.sleep(1); } catch (InterruptedException e) { }
//                        }
//                    }
//                }.start();

                // sub thread를 생성해서 mediaplayer의 현재 포지션 값으로 seekbar를 변경해준다. 매 1초마다
                // runOnUiThread를 사용하면 핸들러 없이 직접 쓰레드 동작 가능하다.
                // while문을 바깥에 씌워주지 않으면 런메소드 사용 후 메시지큐에 들어가지 못하고 헛돈다.
                new Thread() {
                    @Override
                    public void run () {
                        while (playStatus < STOP) {
                            if (player != null) {
                                // 이 부분은 핸들러랑 같은동작이다!! 뿌에에에엥!
                                // 메인쓰레드에서 동작하도록 Runnable instance를 메인쓰레드에 던져주는 역할을 한다.
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{ // 플레이어가 도중에 종료되면 예외가 발생한다.
                                            seekBar.setProgress(player.getCurrentPosition());
                                            txtCurrent.setText(Util.milliSecToTime(player.getCurrentPosition()));
                                        } catch (IllegalStateException e) { e.printStackTrace(); }
                                    }
                                });
                                // 여기까지 핸들러했다! 쁏
                            }
                            // 너무 상세하게 체크가 돌아가면 렉을 유발하기 때문에 1초텀줌
                            try { Thread.sleep(1); } catch (InterruptedException e) { }
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
        if(position > 0) viewPager.setCurrentItem(position - 1);
    }
    private void next(){
        if(position < datas.size()) viewPager.setCurrentItem(position + 1);
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
