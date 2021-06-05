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
        parseData(nmea);
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
