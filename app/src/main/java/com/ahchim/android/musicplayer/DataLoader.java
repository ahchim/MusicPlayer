package com.ahchim.android.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by Ahchim on 2017-02-01.
 */

public class DataLoader {
    private ArrayList<Music> datas = new ArrayList<>();
    private Context context;

    public DataLoader(Context context){
        this.context = context;
    }


    public void load(){
        // 1. 데이터에 접근하기 위해 ContentResolver를 불러온다.
        ContentResolver resolver = context.getContentResolver();

        // 2. 데이터 컨텐츠 URI 정의
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        // 3. 데이터에서 가져올 데이터 컬럼명을 String 배열에 담는다
        //    데이터컬럼명은 Content Uri의 패키지에 들어 있다.
        String proj[] = {   // 아이디, 앨범 아이디, 타이틀, 아티스트
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST
        };

        // 커서 : 데이터가 처리되는 단위 (안드로이드에서)
        // 4. Content Resolver로 질의(쿼리)한 데이터를 Cursor에 담는다.
        // 데이터 URI : MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        Cursor cursor = resolver.query(uri  // 데이터의 주소
                        , proj              // 가져올 데이터 컬럼명 배열
                        , null              // 조건절
                        , null              // 지정된 컬럼명과 매핑되는 실제 조건 값
                        , null              // 정렬
        );

        // 5. Cursor에 넘어온 데이터가 있다면 반복문을 돌면서 datas에 담아준다.
        if(cursor!=null){
            while(cursor.moveToNext()){
                Music music = new Music();

                // 데이터
                // 5. 커서의 컬럼 인덱스를 가져온 후
                int idx = cursor.getColumnIndex(proj[0]);
                // 5.1 컬럼인덱스에 해당하는 타입에 맞춰 값을 꺼내서 세팅한다.
                music.setId(cursor.getString(idx));

                idx = cursor.getColumnIndex(proj[1]);
                music.setAlbum_id(cursor.getString(idx));

                idx = cursor.getColumnIndex(proj[2]);
                music.setTitle(cursor.getString(idx));

                idx = cursor.getColumnIndex(proj[3]);
                music.setArtist(cursor.getString(idx));

                // datas
                datas.add(music);
            }

            // ******중요 : cursor 사용 후 close를 호출하지 않으면 메모리 누수가 발생할 수 있다.
            // 6. 처리 후 커서를 닫아준다.
            cursor.close();
        }
    }

    public ArrayList<Music> get() {
        return datas;
    }
}
