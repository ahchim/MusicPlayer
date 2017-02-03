package com.ahchim.android.musicplayer;

import java.util.Calendar;
import java.util.regex.Pattern;

public class Util {
    // 유틸성에 들어가는 함수는 죄다 Static으로 만들어놓자. (이곳저곳에서 참조해서 써야 하므로)
    public static String getDatetime(){
        // 디자인패턴 중 하나였음. 싱글톤..
        // new로 쓰지 않고 이미 정의된 Calendar를 같은 참조값으로 가져오는 것.
        Calendar cal = Calendar.getInstance();

        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);

        String ap;
        if(cal.get(Calendar.AM_PM)==Calendar.AM){
            ap = "오전";
        } else ap = "오후";

        return y + "-" + m + "-" + d + ", "
                + hour + ":" + min + ":" + sec + " " + ap;
    }

    /**
     *
     * @param value
     * @return integer value
     */
    // 수업때 했던 try catch문을 날려버리고 새로 작성
    public static int getNumber(String value){
        if(Pattern.matches("^[0-9]+$", value)){
            // 정수
            return Integer.parseInt(value);
        }else{
            // 정수 아님
            return -1;
        }
    }

    /**
     * 밀리세컨드를 시간()으로 바꾸는 함수
     * @param now_Msec
     * @return
     */
    public static String milliSecToTime(long now_Msec) {
        long now_Sec = (long) (now_Msec / 1000);

        long hour = (long) (now_Sec / 3600);
        long min = (long) ((now_Sec % 3600) / 60);
        long sec = (long) (now_Sec % 60);

        long msec = (long) (now_Msec - now_Sec*1000);

        // 시간이 있을 때만 시간 표시.
        if(hour > 0) {
            return String.format("%02d", hour) + ":"
                    + String.format("%02d", min) + ":"
                    + String.format("%02d", sec) + ":"
                    + String.format("%02d", msec);
        } else{
            return String.format("%02d", min) + ":"
                    + String.format("%02d", sec) + ":"
                    + String.format("%02d", msec);
        }
    }
}
