package com.ahchim.android.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Ahchim on 2017-02-01.
 */

public class DataLoader {
    // datas를 두개의 activity에서 공유하기 위해 static 형태로 변경
    private static ArrayList<Music> datas = new ArrayList<>();

    // 헐 우와 얠 static으로 해주면 private 지키고 static 장점은 가져갈 수 있겠네.
    // static 변수인 datas를 체크해서 null이면 load를 실행
    public static ArrayList<Music> get(Context context) {
        if(datas == null || datas.size() == 0){
            load(context);
        }
        return datas;
    }

    // load도 private하고 static하게 바꿔준다. get 통해서만 부르기때문에
    // load 함수는 get 함수를 통해서만 접근한다.
    private static void load(Context context){
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

                music.setAlbum_image(getAlbumImageSimple(music.getAlbum_id()));
                //music.setBitmap_image(getAlbumImageBitmap(music.getAlbum_id()));

                music.setUri(getMusicUri(music.getId()));

                // datas
                datas.add(music);
            }

            // ******중요 : cursor 사용 후 close를 호출하지 않으면 메모리 누수가 발생할 수 있다.
            // 6. 처리 후 커서를 닫아준다.
            cursor.close();
        }
    }

    // 음악 id로 uri를 가져오는 함수
    private static Uri getMusicUri(String music_id){
        Uri content_uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        return Uri.withAppendedPath(content_uri, music_id); // 쉼표로 여러개의 Uri를 넣으면 다 합쳐서 1개의 Uri로 만들어줌
    }


    // 가장 간단하게 앨범이미지를 가져오는 방법
    // 문제점 : 실제 앨범데이터만 있어서 이미지를 불러오지 못하는 경우가 있다.
    // load 안에 있으니까 얘도 static하게 바꿔준다.
    private static Uri getAlbumImageSimple(String album_id){
        return Uri.parse("content://media/external/audio/albumart/"+ album_id);
    }
    // @Deprecated 써서 가로줄쓰려고 했으나... 간단해져서 안지움'ㅅ'

    // 안써 안쓴다고!
    @Deprecated
    private static Bitmap getAlbumImageBitmap(Context context, String album_id){
        // 1. 앨범아이디로 Uri 생성
        Uri uri = getAlbumImageSimple(album_id);
        // 2. 컨텐트 리졸버 가져오기
        ContentResolver resolver = context.getContentResolver();

        try {
            // 3. 리졸버에서 스트림열기
            InputStream is = resolver.openInputStream(uri);

            // 4. BitmapFactory를 통해 이미지데이터를 가져온다. 디코딩(복호화)
            Bitmap image = BitmapFactory.decodeStream(is);

            return image;
        } catch (FileNotFoundException e) {
            Log.e("getAlbumImageBitmap()", "비트맵 파일을 가져올 수 없습니다.");
            // 로거클래스에 file append해서 로그내용을 찍어놓을 수 있다. 꿀팁..
            e.printStackTrace();
        }

        return null;
    }
}
