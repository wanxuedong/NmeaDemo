package world.share.myapplication.neamparser.constant;

/**
 * 不同NMEA格式数据应该具备的长度
 * 这里的长度指一段NMEA数据被逗号切割后的数组长度，并不是全部的字段的个数，因为存在不同字段间没有逗号分割的情况
 *
 * @author wanxuedong  2021/6/5
 */
public class FieldLength {

    /**
     * GGA数据规范长度
     **/
    public static final int GGA_LENGTH = 15;

    /**
     * GLL数据规范长度
     **/
    public static final int GLL_LENGTH = 7;

    /**
     * RMC数据规范长度
     **/
    public static final int RMC_LENGTH = 14;

    /**
     * VTG数据规范长度
     **/
    public static final int VTG_LENGTH = 10;

    /**
     * GSV数据规范长度
     **/
    public static final int GSV_LENGTH = 21;

    /**
     * GSA数据规范长度
     **/
    public static final int GSA_LENGTH = 19;

}
