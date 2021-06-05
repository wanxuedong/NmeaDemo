package world.share.myapplication.neamparser.constant;

/**
 * NMEA协议语句标识
 * 列出了常见的几种，实际上有20多种，可参考NMEA协议格式说明.pdf文档
 *
 * @author wanxuedong  2021/6/4
 */
public class NMEAMessageID {

    /**
     * GPS定位信息帧，包含了GPS卫星时间、位置以及确定数据需要的其他参 数，如可用卫星数等等
     * 输出范例:
     * $GPGGA,161229.487,3723.2475,N,12158.3416,W,1,07,1.0,9.0,M, , , ,0000*18
     **/
    public static final String GGA = "GGA";

    /**
     * GLL信息帧记录了当前地理定位信息，包括经度、纬度和UTC时间
     * 输出范例:
     * $GPGLL,3723.2475,N,12158.3416,W,161229.487,A*2C
     **/
    public static final String GLL = "GLL";

    /**
     * GSA信息帧记录了当前卫星的信息
     * 输出范例:
     * $GNGSA,A,1,,,,,,,,,,,,,99.99,99.99,99.99,1*33
     **/
    public static final String GSA = "GSA";

    /**
     * GSV是记录当前接收卫星状态以及上空位置的信息帧
     * 输出范例:
     * $GPGSV,4,3,14,17,64,063,,19,64,358,,28,54,195,,194,26,173,,0*59
     **/
    public static final String GSV = "GSV";

    /**
     * RMC记录了NMEA推荐的最小信息帧，包括了大部分定位导航需要的信息
     * 输出范例:
     * $GNRMC,063544.00,V,3201.70158,N,11854.94867,E,,,050621,,,N,V*24
     **/
    public static final String RMC = "RMC";

    /**
     * VTG记录了接收终端的地表矢量速度，由地表角度和地表速度组成
     * 输出范例:
     * $GPVTG,134.395,T,134.395,M,0.019,N,0.035,K,A*33
     **/
    public static final String VTG = "VTG";

}
