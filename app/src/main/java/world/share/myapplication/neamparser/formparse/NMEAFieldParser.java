package world.share.myapplication.neamparser.formparse;

import java.util.ArrayList;
import java.util.List;

import world.share.myapplication.neamparser.constant.Direction;
import world.share.myapplication.neamparser.constant.TalkerID;
import world.share.myapplication.neamparser.data.CoordinateData;
import world.share.myapplication.neamparser.data.SatellitesData;
import world.share.myapplication.neamparser.data.TimeData;
import world.share.myapplication.neamparser.handler.NMEAHandler;
import world.share.myapplication.neamparser.utils.DataParser;

import static world.share.myapplication.neamparser.constant.FieldLength.*;


/**
 * NMEA协议解析数据字段部分
 * 暂时只支持格式：GGA，GLL，RMC，GSA，VTG，GSV六种
 *
 * @author wanxuedong  2021/6/5
 */
public abstract class NMEAFieldParser {

    /**
     * 数据解析回调
     **/
    public NMEAHandler nmeaHandler;

    public void setNmeaHandler(NMEAHandler nmeaHandler) {
        this.nmeaHandler = nmeaHandler;
    }

    /**
     * 原始数据返回，只是做了最简单的分割处理，里面包含了全部的数据，如果其他的方法参数不够使用，可以从这个方法中获取全部数据并自行处理
     *
     * @param original 原始nmea字符串数组
     **/
    public void parseOriginal(String[] original) {
        if (nmeaHandler != null) {
            nmeaHandler.onOriginal(original);
        }
    }

    /**
     * 解析GGA格式数据
     *
     * @param talkId  卫星类型
     * @param content 报文信息
     **/
    public void parseGGA(TalkerID talkId, String[] content) {
        if (content.length != GGA_LENGTH) {
            return;
        }
        TimeData time = DataParser.getTime(content[1]);
        double longitude = DataParser.getPosition(content[4]);
        double latitude = DataParser.getPosition(content[2]);
        if (content[5].length() == 0 || content[3].length() == 0) {
            longitude = -1;
            latitude = -1;
        } else {
            longitude = Direction.E == content[5].charAt(0) ? longitude : -longitude;
            latitude = Direction.N == content[3].charAt(0) ? latitude : -latitude;
        }
        int satellites = DataParser.getSatellites(content[7]);
        float altitude = DataParser.getHeight(content[9]);
        if (nmeaHandler != null) {
            nmeaHandler.onGGA(talkId, time, new CoordinateData(longitude, latitude), satellites, altitude);
        }
    }

    /**
     * 解析GLL格式数据
     *
     * @param talkId  卫星类型
     * @param content 报文信息
     **/
    public void parseGLL(TalkerID talkId, String[] content) {
        if (content.length != GLL_LENGTH) {
            return;
        }
        TimeData time = DataParser.getTime(content[5]);
        double longitude = DataParser.getPosition(content[3]);
        double latitude = DataParser.getPosition(content[1]);
        if (content[4].length() == 0 || content[2].length() == 0) {
            longitude = -1;
            latitude = -1;
        } else {
            longitude = Direction.E == content[4].charAt(0) ? longitude : -longitude;
            latitude = Direction.N == content[2].charAt(0) ? latitude : -latitude;
        }
        String state = content[6];
        if (nmeaHandler != null) {
            nmeaHandler.onGLL(talkId, time, new CoordinateData(longitude, latitude), state);
        }
    }

    /**
     * 解析RMC格式数据
     *
     * @param talkId  卫星类型
     * @param content 报文信息
     **/
    public void parseRMC(TalkerID talkId, String[] content) {
        if (content.length != RMC_LENGTH) {
            return;
        }
        TimeData time = DataParser.getTime(content[1]);
        double longitude = DataParser.getPosition(content[5]);
        double latitude = DataParser.getPosition(content[3]);
        if (content[6].length() == 0 || content[4].length() == 0) {
            longitude = -1;
            latitude = -1;
        } else {
            longitude = Direction.E == content[6].charAt(0) ? longitude : -longitude;
            latitude = Direction.N == content[4].charAt(0) ? latitude : -latitude;
        }
        float speed = DataParser.getSpeed(content[7]);
        float course = DataParser.getDirection(content[8]);
        if (nmeaHandler != null) {
            nmeaHandler.onRMC(talkId, time, new CoordinateData(longitude, latitude), speed, course);
        }
    }

    /**
     * 解析GSA格式数据
     *
     * @param talkId  卫星类型
     * @param content 报文信息
     **/
    public void parseGSA(TalkerID talkId, String[] content) {
        if (content.length != GSA_LENGTH) {
            return;
        }
        List<String> numbers = new ArrayList<>();
        for (int i = 3; i < 15; i++) {
            numbers.add(content[i]);
        }
        if (nmeaHandler != null) {
            nmeaHandler.onGSA(talkId, content[1], content[2], numbers, content[15], content[16], content[17]);
        }
    }

    /**
     * 解析VTG格式数据
     *
     * @param talkId  卫星类型
     * @param content 报文信息
     **/
    public void parseVTG(TalkerID talkId, String[] content) {
        if (content.length != VTG_LENGTH) {
            return;
        }
        float northCourse = DataParser.getDirection(content[1]);
        float magneticCourse = DataParser.getDirection(content[3]);
        float seaSpeed = DataParser.getSpeed(content[3]);
        float landSpeed = DataParser.getSpeed(content[7]);
        if (nmeaHandler != null) {
            nmeaHandler.onVTG(talkId, northCourse, magneticCourse, seaSpeed, landSpeed);
        }
    }

    /**
     * 解析GSV格式数据
     *
     * @param talkId  卫星类型
     * @param content 报文信息
     **/
    public void parseGSV(TalkerID talkId, String[] content) {
        if (content.length != GSV_LENGTH) {
            return;
        }
        int satellites = DataParser.getSatellites(content[3]);
        List<SatellitesData> satellitesDataList = new ArrayList<>();
        satellitesDataList.add(new SatellitesData(content[4], content[5], content[6], content[7]));
        satellitesDataList.add(new SatellitesData(content[8], content[9], content[10], content[11]));
        satellitesDataList.add(new SatellitesData(content[12], content[13], content[14], content[15]));
        satellitesDataList.add(new SatellitesData(content[16], content[17], content[18], content[19]));
        if (nmeaHandler != null) {
            nmeaHandler.onGSV(talkId, satellites, satellitesDataList);
        }
    }

}
