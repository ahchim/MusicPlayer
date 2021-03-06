package com.ahchim.android.musicplayer;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Player 액티비티와 adapter를 공유하기 위해 static으로 선언했다.
    // 이게 최선인지 일단 고민해보자..
    public static MusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 버전체크해서 마시멜로보다 낮으면 런타임권한 체크를 하지 않는다.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkPermission();
        } else{
            init();
        }
    }

    private final int REQ_CODE = 100;

    // 1. 권한체크
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission(){
        // 1.1 런타임 권한체크
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // 1.2 요청할 권한 목록 작성
            String permArr[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
            // 1.3 시스템에 권한 요청
            requestPermissions(permArr, REQ_CODE);
        } else{
            init();
        }
    }

    // 2. 권한체크 후 콜백 < 사용자가 확인후 시스템이 호출하는 함수
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult){
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        // 2.1 배열에 넘긴 런타임권한을 체크해서 승인이 되면
        if(requestCode == REQ_CODE){
            if(grantResult[0] == PackageManager.PERMISSION_GRANTED){
                // 2.2 프로그램 실행
                init();
            } else{
                Toast.makeText(this, "권한을 허용하지 않으시면 프로그램을 실행할 수 없습니다", Toast.LENGTH_SHORT).show();
                //checkPermission();
                finish();
            }
        }
    }


    // 데이터를 로드할 함수
    private void init(){
        Toast.makeText(this, "프로그램을 실행합니다.", Toast.LENGTH_SHORT).show();

        // 3.1 데이터를 불러온다
//        DataLoader loader = new DataLoader(this);
//        loader.load();
//        ArrayList<Music> datas = loader.get();
        // 싱글톤 패턴으로 바꿔보기
        ArrayList<Music> datas = DataLoader.get(this);

        // 리사이클러뷰 세팅
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new MusicAdapter(datas, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
