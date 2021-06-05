package world.share.myapplication.neamparser.formparse;

import android.text.TextUtils;

/**
 * NMEA协议解析语句标识部分
 *
 * @author wanxuedong  2021/6/5
 */
public class NMEAMessageIdParser {

    /**
     * 获取语句标示
     *
     * @param nmea 一段完整nmea数据
     * @return 返回GPS语句标识
     **/
    public String parseMessageId(String nmea) {
        if (TextUtils.isEmpty(nmea)) {
            return "";
        }
        String messageID = "";
        if (nmea.length() > 5) {
            String head = nmea.substring(0, 6);
            messageID = head.substring(3);
            return messageID;
        }
        return "";
    }

}
