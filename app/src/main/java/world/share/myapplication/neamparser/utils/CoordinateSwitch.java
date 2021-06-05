package world.share.myapplication.neamparser.utils;

import android.text.TextUtils;

/**
 * 经纬度转换工具
 * 在数学中，表示角度的度、分、秒分别使用°、′、″符号进行表示
 * 1°=60′，1′=60″ ，1°=3600″
 *
 * @author wanxuedong  2021/6/5
 */
public class CoordinateSwitch {

    /**
     * 度符号
     **/
    private static final String DEGREE = "°";

    /**
     * 第一种分符号
     **/
    private static final String BRANCH_ONE = "′";

    /**
     * 第二种分符号
     **/
    private static final String BRANCH_TWO = "'";

    /**
     * 第一种秒符号
     **/
    private static final String SECOND_ONE = "″";

    /**
     * 第二种秒符号
     **/
    private static final String SECOND_TWO = "''";


    /**
     * 度分秒转度
     * 如：113°30'10.25" 转成 113.50284722222223
     *
     * @param jwd 传入的经纬度数据
     */
    public static String Dms2D(String jwd) {
        //如果不为空并且存在度单位
        if (!TextUtils.isEmpty(jwd) && (jwd.contains(DEGREE))) {
            //计算前进行数据处理
            jwd = jwd.replace("E", "").replace("N", "").replace(":", "").replace("：", "");
            double d = 0, m = 0, s = 0;
            d = Double.parseDouble(jwd.split(DEGREE)[0]);
            //不同单位的分，可扩展
            if (jwd.contains(BRANCH_ONE)) {
                //正常的′
                m = Double.parseDouble(jwd.split(DEGREE)[1].split(BRANCH_ONE)[0]);
            } else if (jwd.contains(BRANCH_TWO)) {
                //特殊的'
                m = Double.parseDouble(jwd.split(DEGREE)[1].split(BRANCH_TWO)[0]);
            }
            //不同单位的秒，可扩展
            if (jwd.contains(SECOND_ONE)) {
                //正常的″
                //有时候没有分 如：112°10.25″
                s = jwd.contains(BRANCH_ONE) ? Double.parseDouble(jwd.split(BRANCH_ONE)[1].split(SECOND_ONE)[0]) : Double.parseDouble(jwd.split(DEGREE)[1].split(SECOND_ONE)[0]);
            } else if (jwd.contains(SECOND_TWO)) {
                //特殊的''
                //有时候没有分 如：112°10.25''
                s = jwd.contains(BRANCH_TWO) ? Double.parseDouble(jwd.split(BRANCH_TWO)[1].split(SECOND_TWO)[0]) : Double.parseDouble(jwd.split(DEGREE)[1].split(SECOND_TWO)[0]);
            }
            //计算并转换为string
            jwd = String.valueOf(d + m / 60 + s / 60 / 60);
        }
        return jwd;
    }

    /**
     * 度分转度
     * 如：112°30.4128 = 112.50688，格式如：dd°mm.mmmmm
     *
     * @param jwd
     */
    public static String DmTurnD(String jwd) {
        //如果不为空并且存在度单位
        if (!TextUtils.isEmpty(jwd) && (jwd.contains(DEGREE))) {
            double d = 0, m = 0;
            d = Double.parseDouble(jwd.split(DEGREE)[0]);
            m = Double.parseDouble(jwd.split(DEGREE)[1]) / 60;
            jwd = String.valueOf(d + m);
        }
        return jwd;
    }


}
