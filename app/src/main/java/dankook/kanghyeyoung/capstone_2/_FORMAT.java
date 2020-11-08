package dankook.kanghyeyoung.capstone_2;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class _FORMAT {
    static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###");
    static SimpleDateFormat YEAR_MONTH_FORMAT=new SimpleDateFormat("dd일 EEE요일");
    static SimpleDateFormat DATE_FORMAT=new SimpleDateFormat("yyyy/MM/dd(EEE)");
    static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy년 MM월 dd일 (EEE) HH:mm");
    static SimpleDateFormat DATE_DB_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
