package world.share.myapplication.neamparser.handler;

import java.util.List;

import world.share.myapplication.neamparser.constant.TalkerID;
import world.share.myapplication.neamparser.data.CoordinateData;
import world.share.myapplication.neamparser.data.SatellitesData;
import world.share.myapplication.neamparser.data.TimeData;


/**
 * NMEA协议不同语句类型回调
 *
 * @author wanxuedong  2021/6/4
 */
public interface NMEAHandler {

    /**
     * 原始数据返回，只是做了最简单的分割处理，里面包含了全部的数据，如果其他的方法参数不够使用，可以从这个方法中获取全部数据并自行处理
     *
     * @param original 原始nmea字符串数组
     **/
    void onOriginal(String[] original);

    /**
     * 包含了卫星时间、位置以及确定数据需要的其他参 数，如可用卫星数等等
     * 范例:
     * $GPGGA,161229.487,3723.2475,N,12158.3416,W,1,07,1.0,9.0,M, , , ,0000*18
     *
     * @param talkerID       卫星类型
     * @param time           UTC时间，单位：时分秒
     * @param coordinateData 经纬度坐标，经度，单位：度度度分分.分分分分,纬度，单位：度度分分.分分分分
     * @param satellites     卫星数量，单位：个
     * @param altitude       海拔，单位：米
     **/
    void onGGA(TalkerID talkerID, TimeData time, CoordinateData coordinateData, int satellites, float altitude);

    /**
     * GPRMC记录了NMEA推荐的最小信息帧，包括了大部分定位导航需要的信息
     * 范例:
     * $GNRMC,063544.00,V,3201.70158,N,11854.94867,E,,,050621,,,N,V*24
     *
     * @param talkerID       卫星类型
     * @param state          定位状态，A-有效定位,V-无效定位
     * @param time           UTC时间，单位：时分秒
     * @param coordinateData 经纬度坐标，经度，单位：度度度分分.分分分分,纬度，单位：度度分分.分分分分
     * @param speed          对地速度，0.0至1851.8节  单位：节，
     * @param course         对地方向，地面航向，以真北为参考基准，沿顺时针方向至航向的角度，单位：度
     **/
    void onRMC(TalkerID talkerID, String state, TimeData time, CoordinateData coordinateData, float speed, float course);

    /**
     * 当前卫星的信息，输出接收机的工作模式，参与定位的卫星数和DOP值
     * 范例:
     * $GNGSA,A,1,,,,,,,,,,,,,99.99,99.99,99.99,1*33
     *
     * @param talkerID 卫星类型
     * @param mode     工作模式，M-手动选择，A-自动选择
     * @param state    定位状态，1-没有定位，2-2D定位，3-3D定位
     * @param numbers  卫星编号集合，展示的是卫星编号，和卫星RPN对应关系如下：
     *                 卫星RPN号：   GPS：1-32，SBAS：120-138，GLONASS：1-24，BDS：1-37，QZSS：193-197
     *                 卫星编号标识符：  GPS：1-32，SBAS：33-51，GLONASS：65-88，BDS：1-37，QZSS：193-197
     *                 卫星编号与其RPN对应关系：  GPS：0+RPN，SBAS：87+RPN，GLONASS：64+RPN，BDS：0+RPN，QZSS：0+RPN
     * @param pDop     空间位置精度因子
     * @param hDop     水平位置精度因子
     * @param vDop     高程位置精度因子
     **/
    void onGSA(TalkerID talkerID, String mode, String state, List<String> numbers, String pDop, String hDop, String vDop);

    /**
     * 当前地理定位信息，包括经度、纬度和UTC时间
     * 范例:
     * $GPGLL,3723.2475,N,12158.3416,W,161229.487,A*2C
     *
     * @param talkerID       卫星类型
     * @param time           UTC时间，单位：时分秒
     * @param coordinateData 经纬度坐标，经度，单位：度度度分分.分分分分,纬度，单位：度度分分.分分分分
     * @param state          信息状态，A信息可用，V信息不可用
     **/
    void onGLL(TalkerID talkerID, TimeData time, CoordinateData coordinateData, String state);

    /**
     * 接收终端的地表矢量速度，由地表角度和地表速度组成
     * 范例:
     * $GPVTG,134.395,T,134.395,M,0.019,N,0.035,K,A*33
     *
     * @param talkerID       卫星类型
     * @param northCourse    地面航向，以真北为参考基准，000-359，单位：度
     * @param magneticCourse 地面航向，以磁北为参考基准，000-359，单位：度
     * @param seaSpeed       对地速度，0.0至999节  单位：节
     * @param landSpeed      对地速度，单位：千米每小时
     **/
    void onVTG(TalkerID talkerID, float northCourse, float magneticCourse, float seaSpeed, float landSpeed);

    /**
     * 当前接收卫星状态以及上空位置的信息帧
     * 范例:
     * $GPGSV,4,3,14,17,64,063,,19,64,358,,28,54,195,,194,26,173,,0*59
     *
     * @param talkerID           卫星类型
     * @param satellites         天空可视卫星数量，00 至 12，单位：个
     * @param satellitesDataList 卫星信息
     **/
    void onGSV(TalkerID talkerID, int satellites, List<SatellitesData> satellitesDataList);

}
