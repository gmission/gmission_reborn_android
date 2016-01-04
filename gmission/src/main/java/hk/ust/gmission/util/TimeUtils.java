package hk.ust.gmission.util;

import android.content.Context;

import java.util.Date;

import hk.ust.gmission.R;

/**
 * Created by bigstone on 3/1/2016.
 */
public class TimeUtils {
    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

    /**
     * 返回文字描述的日期
     *
     * @param date
     * @return
     */
    public static String getTimeFormatText(Context context, Date date) {
        if (date == null) {
            return "";
        }
        long diff = new Date().getTime() - date.getTime();
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + context.getString(R.string.years_before);
        }
        if (diff > month) {
            r = (diff / month);
            return r + context.getString(R.string.month_before);
        }
        if (diff > day) {
            r = (diff / day);
            return r + context.getString(R.string.days_before);
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + context.getString(R.string.hours_before);
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + context.getString(R.string.min_before);
        }
        return context.getString(R.string.just_now);
    }
}
