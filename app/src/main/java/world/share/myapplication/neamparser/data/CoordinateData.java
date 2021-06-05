package world.share.myapplication.neamparser.data;

import android.text.TextUtils;

import world.share.myapplication.neamparser.utils.CoordinateSwitch;

/**
 * 经纬度坐标
 *
 * @author wanxuedong  2021/6/5
 */
public class CoordinateData {

    /**
     * 度符号
     **/
    private static final String DEGREE = "°";

    /**
     * 经度-度
     **/
    private double longDegree;

    /**
     * 经度-分
     **/
    private double longBranch;

    /**
     * 经度字符串，格式:ddd°mm.mmmmm
     **/
    private String longString = "";

    /**
     * 纬度-度
     **/
    private double laDegree;

    /**
     * 纬度-分
     **/
    private double laBranch;

    /**
     * 纬度字符串，格式:dd°mm.mmmmm
     **/
    private String latitudeString = "";

    public CoordinateData(double longitude, double latitude) {

        //获取经度的度，分
        String lgString = longitude + "";
        if (lgString.length() > 3) {
            String str = String.valueOf(lgString.charAt(0)) + lgString.charAt(1) + lgString.charAt(2);
            if (!TextUtils.isEmpty(str)) {
                longDegree = Double.parseDouble(str);
            }
            if (!TextUtils.isEmpty(lgString.substring(3))) {
                longBranch = Double.parseDouble(lgString.substring(3));
            }
        }
        longString = longDegree + DEGREE + longBranch;

        //获取纬度的度，分
        String laString = latitude + "";
        if (laString.length() > 2) {
            String str = String.valueOf(laString.charAt(0)) + laString.charAt(1);
            if (!TextUtils.isEmpty(str)) {
                laDegree = Double.parseDouble(str);
            }
            if (!TextUtils.isEmpty(laString.substring(2))) {
                laBranch = Double.parseDouble(laString.substring(2));
            }
        }
        latitudeString = laDegree + DEGREE + laBranch;
    }

    /**
     * 获取经度，格式：ddd.ddddddd
     **/
    public String getLongitude() {
        return CoordinateSwitch.DmTurnD(longString);
    }

    /**
     * 获取纬度，格式：dd.ddddddd
     **/
    public String getLatitude() {
        return CoordinateSwitch.DmTurnD(latitudeString);
    }

    /**
     * 获取经度，格式：ddd°mm.mmmmm
     **/
    public String getOriginalLongitude() {
        return longString;
    }

    /**
     * 获取纬度，格式：dd°mm.mmmmm
     **/
    public String getOriginalLatitude() {
        return latitudeString;
    }

    @Override
    public String toString() {
        return "(" + CoordinateSwitch.DmTurnD(longString) + "," + CoordinateSwitch.DmTurnD(latitudeString) + ")";
    }


}
