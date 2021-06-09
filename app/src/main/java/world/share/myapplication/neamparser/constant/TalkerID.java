package world.share.myapplication.neamparser.constant;

/**
 * NMEA协议NMEA标识卫星系统类别
 *
 * @author wanxuedong  2021/6/4
 */
public enum TalkerID {

    /**
     * GPS定位系统，SBAS，QZSS，美国
     **/
    GP,

    /**
     * BDS，北斗系统，中国
     **/
    BD,

    /**
     * 表示GPS+BDS 的双模模式
     **/
    GB,

    /**
     * GLONASS,格洛纳斯卫星导航系统，俄罗斯
     **/
    GL,

    /**
     * Galileo,伽利略卫星导航系统，欧洲
     **/
    GA,

    /**
     * GNSS，全称：Global Navigation Satellite System，全球导航卫星系统，泛指所有的卫星导航系统，包括其他全部枚举标识
     **/
    GN,


}
