package world.share.myapplication.neamparser;

import android.text.TextUtils;

import world.share.myapplication.neamparser.constant.NMEAMessageID;
import world.share.myapplication.neamparser.constant.TalkerID;
import world.share.myapplication.neamparser.formparse.NMEAFieldParser;
import world.share.myapplication.neamparser.formparse.NMEAMessageIdParser;
import world.share.myapplication.neamparser.formparse.NMEATalkIdParser;
import world.share.myapplication.neamparser.handler.NMEAAbstractParser;

/**
 * NMEA格式数据解析工具
 *
 * @author wanxuedong  2021/6/4
 */
public class NMEAParser {

    /**
     * 解析工具单例对象
     **/
    public static NMEAParser instance;

    /**
     * 用于解析NMEA协议的卫星类型字段
     **/
    private NMEATalkIdParser talkIdParser;

    /**
     * 用于解析NMEA协议的卫星类型字段
     **/
    private NMEAMessageIdParser messageIdParser;

    /**
     * 用于解析NMEA协议的内容数据字段
     **/
    private NMEAFieldParser fieldParser;

    /**
     * 双重锁检查单例实现
     **/
    public static NMEAParser getInstance() {
        if (instance == null) {
            synchronized (NMEAParser.class) {
                if (instance == null) {
                    instance = new NMEAParser();
                }
            }
        }
        return instance;
    }

    /**
     * 解析nmea数据
     *
     * @param nmea 一段完整nmea数据
     **/
    public void parse(String nmea) {
        if (TextUtils.isEmpty(nmea)) {
            return;
        }
        //进行数据校验
        if (dataVerification(nmea)) {
            parseData(nmea);
        }
    }

    /**
     * 校验数据是否合法
     * 通过判断最后俩位16进制是否与$和*之间全部字符的异或结果之和相同，包括,
     *
     * @return true：校验合法，false：校验不合法
     **/
    private boolean dataVerification(String nmea) {
        if (TextUtils.isEmpty(nmea)) {
            return false;
        }
        //第一个字符是$，不需要参与运算,最后一个是*，也不需要
        char xor = nmea.charAt(1);
        for (int i = 2; i < nmea.length(); i++) {
            if (nmea.charAt(i) == '*') {
                break;

            } else {
                xor ^= nmea.charAt(i);
            }
        }
        //char类型是16位的，占俩个字节，其范围是0~65536
        //为了防止运算结果超出char的数值范围，取余数，范围0~65536
        int result = xor % 65536;
        //把result转成16进制
        String str = Integer.toString(result, 16);
        if (nmea.endsWith(str)) {
            return true;
        }
        return false;
    }

    /**
     * 根据语句标识解析nmea报文内容
     *
     * @param nmea 一段完整nmea数据
     **/
    private void parseData(String nmea) {
        TalkerID talkId = talkIdParser.parseTalkId(nmea);
        String messageID = messageIdParser.parseMessageId(nmea);
        String[] original = nmea.split(",");
        fieldParser.parseOriginal(original);
        switch (messageID) {
            case NMEAMessageID.GGA:
                fieldParser.parseGGA(talkId, original);
                break;
            case NMEAMessageID.GLL:
                fieldParser.parseGLL(talkId, original);
                break;
            case NMEAMessageID.GSA:
                fieldParser.parseGSA(talkId, original);
                break;
            case NMEAMessageID.GSV:
                fieldParser.parseGSV(talkId, original);
                break;
            case NMEAMessageID.RMC:
                fieldParser.parseRMC(talkId, original);
                break;
            case NMEAMessageID.VTG:
                fieldParser.parseVTG(talkId, original);
                break;
            default:
        }
    }

    /**
     * 设置NMEA协议数据解析回调
     **/
    public void setNmeaHandler(NMEAAbstractParser nmeaAbstractParser) {
        fieldParser.setNmeaHandler(nmeaAbstractParser);
    }

    /**
     * 防止反射破解单例
     **/
    private NMEAParser() {
        if (instance != null) {
            throw new RuntimeException();
        }
        talkIdParser = new NMEATalkIdParser();
        messageIdParser = new NMEAMessageIdParser();
        fieldParser = new NMEAFieldParser() {
        };
    }

    /**
     * 防止序列化破解单例
     **/
    private Object readResolve() {
        return instance;
    }

}
