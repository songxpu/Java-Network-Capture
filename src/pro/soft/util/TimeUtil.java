package pro.soft.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    public static String getLocalTime(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }
}
