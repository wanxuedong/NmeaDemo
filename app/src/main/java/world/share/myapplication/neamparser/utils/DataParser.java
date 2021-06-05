package world.share.myapplication.neamparser.utils;

import android.text.TextUtils;

import world.share.myapplication.neamparser.data.TimeData;


/**
 * NMEA协议字段数据格式化处理
 *
 * @author wanxuedong  2021/6/4
 */
public class DataParser {

    /**
     * 获取时间
     *
     * @param time 字符串时间，格式：时时分分秒秒.秒秒秒
     * @return 返回时间，格式：时分秒
     **/
    public static TimeData getTime(String time) {
        if (TextUtils.isEmpty(time)) {
            return new TimeData();
        }
        int hour = Integer.parseInt(String.valueOf(time.charAt(0)) + time.charAt(1));
        int minute = Integer.parseInt(String.valueOf(time.charAt(2)) + time.charAt(3));
        int second = Integer.parseInt(String.valueOf(time.charAt(4)) + time.charAt(5));
        return new TimeData(hour, minute, second);
    }

    /**
     * 获取经纬度
     *
     * @param position 经度或者纬度,经度格式：度度度分分.分分分分，纬度格式：度度分分.分分分分
     * @return 返回经度或者纬度，经度格式：度度度分分.分分分分，纬度格式：度度分分.分分分分
     **/
    public static double getPosition(String position) {
        if (TextUtils.isEmpty(position)) {
            return 0;
        }
        return Double.parseDouble(position);
    }

    /**
     * 获取航速
     *
     * @param speed 航速，单位：节或千米每小时
     * @return 返回航速，单位：节或千米每小时
     **/
    public static float getSpeed(String speed) {
        if (TextUtils.isEmpty(speed)) {
            return 0;
        }
        return Float.parseFloat(speed);
    }

    /**
     * 获取海拔
     *
     * @param height 高度，单位：米
     * @return 返回高度，单位：米
     **/
    public static float getHeight(String height) {
        if (TextUtils.isEmpty(height)) {
            return 0;
        }
        return Float.parseFloat(height);
    }

    /**
     * 获取方向
     *
     * @param direction 偏移角度，单位：度
     * @return 返回角度，单位：度
     **/
    public static float getDirection(String direction) {
        if (TextUtils.isEmpty(direction)) {
            return 0;
        }
        return Float.parseFloat(direction);
    }

    /**
     * 获取卫星数量
     *
     * @param satellites 卫星数量字符串
     * @return 返回卫星数量
     **/
    public static int getSatellites(String satellites) {
        if (TextUtils.isEmpty(satellites)) {
            return 0;
        }
        return Integer.parseInt(satellites);
    }

}
