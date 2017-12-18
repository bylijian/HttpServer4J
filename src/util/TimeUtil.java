package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lijian on 2017/12/15.
 */
public class TimeUtil {

    public static String getGMT(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(
                "EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        return sdf.format(date);
    }

    public static String getGMT() {
        return getGMT(System.currentTimeMillis());
    }

}
