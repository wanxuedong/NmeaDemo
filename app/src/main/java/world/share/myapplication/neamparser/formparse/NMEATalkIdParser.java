package world.share.myapplication.neamparser.formparse;

import android.text.TextUtils;

import world.share.myapplication.neamparser.constant.TalkerID;

/**
 * NMEA协议解析卫星类型
 *
 * @author wanxuedong  2021/6/5
 */
public class NMEATalkIdParser {

    /**
     * 获取卫星系统类别
     *
     * @param nmea 一段完整nmea数据
     * @return 返回卫星系统类别
     **/
    public TalkerID parseTalkId(String nmea) {
        TalkerID talkerID = TalkerID.GN;
        if (TextUtils.isEmpty(nmea)) {
            return talkerID;
        }
        if (nmea.length() > 2) {
            String head = nmea.substring(0, 3);
            String type = head.substring(1);
            switch (type) {
                case "GP":
                    //北斗导航
                    talkerID = TalkerID.GP;
                    break;
                case "BD":
                    //GPS导航
                    talkerID = TalkerID.BD;
                    break;
                case "GB":
                    //GPS+BDS 的双模模式
                    talkerID = TalkerID.GB;
                    break;
                case "GL":
                    //格洛纳斯卫星导航系统
                    talkerID = TalkerID.GL;
                    break;
                case "GA":
                    //伽利略卫星导航系统
                    talkerID = TalkerID.GA;
                    break;
                default:
                    //其他导航
                    talkerID = TalkerID.GN;
            }
        }
        return talkerID;
    }

}
