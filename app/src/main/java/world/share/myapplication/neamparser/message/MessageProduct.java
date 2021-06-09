package world.share.myapplication.neamparser.message;

import world.share.myapplication.neamparser.utils.CheckUtil;

import static world.share.myapplication.neamparser.message.MessageHeader.CAS00;
import static world.share.myapplication.neamparser.message.MessageHeader.CAS01;
import static world.share.myapplication.neamparser.message.MessageHeader.CAS02;
import static world.share.myapplication.neamparser.message.MessageHeader.CAS03;
import static world.share.myapplication.neamparser.message.MessageHeader.CAS04;
import static world.share.myapplication.neamparser.message.MessageHeader.CAS05;
import static world.share.myapplication.neamparser.message.MessageHeader.CAS06;
import static world.share.myapplication.neamparser.message.MessageHeader.CAS10;
import static world.share.myapplication.neamparser.message.MessageHeader.CAS12;
import static world.share.myapplication.neamparser.message.MessageHeader.COMMA;
import static world.share.myapplication.neamparser.message.MessageHeader.ENDER;
import static world.share.myapplication.neamparser.message.MessageHeader.FINISH;
import static world.share.myapplication.neamparser.message.MessageHeader.HEADER;

/**
 * 自定义NMEA消息
 * 一段完成NMEA的数据包含：
 * 起始头：&
 * 内容体：xxx,xxxx,xxx
 * 结束符:*
 * 校验和:xx
 * 结束序列:<CR><LF>
 * 通过发送语句给接收器，实现对接收机的控制
 *
 * @author wanxuedong  2021/6/7
 */
public class MessageProduct {

    /**
     * 获取CAS00自定义消息
     * 将当前配置信息保存到FLASH中，即使接收机完全断电，FLASH中的信息也不会丢失
     *
     * @return 返回CAS00消息字符串
     **/
    public String productCAS00() {
        return HEADER + CAS00 + ENDER + CheckUtil.getCheckDigit(CAS00) + FINISH;
    }

    /**
     * 获取CAS01自定义消息
     * 设置串口波特率
     *
     * @param baudRate 传入波特率特定数字，可选如下:
     *                 0=4800bps
     *                 1=9600bps
     *                 2=19200bps
     *                 3=38400bps
     *                 4=57600bps
     *                 5=115200bps
     * @return 返回CAS01消息字符串
     **/
    public String productCAS01(int baudRate) {
        String content = CAS01 + COMMA + baudRate;
        return HEADER + content + ENDER + CheckUtil.getCheckDigit(content) + FINISH;
    }

    /**
     * 获取CAS02自定义消息
     * 设置定位更新率
     *
     * @param interval 定位更新间隔，单位ms，可选如下：
     *                 1000=1Hz，每秒输出1个定位点
     *                 500=2Hz，每秒输出2个定位点
     *                 250=4Hz，每秒输出4个定位点
     *                 200=5Hz，每秒输出5个定位点
     *                 100=10Hz，每秒输出10个定位点
     * @return 返回CAS02消息字符串
     **/
    public String productCAS02(int interval) {
        String content = CAS02 + COMMA + interval;
        return HEADER + content + ENDER + CheckUtil.getCheckDigit(content) + FINISH;
    }

    /**
     * 获取CAS03自定义消息
     * 设置开始输入或者停止输出NMEA
     *
     * @param gga GGA输出频率，n范围0-9，表示每n次定位输出一次，0表示不输入该语句，空则表示原有配置
     * @param gll GLL输出频率，同GGA
     * @param gsa GSA输出频率，同GGA
     * @param gsv GSV输出频率，同GGA
     * @param rmc RMC输出频率，同GGA
     * @param vtg VTG输出频率，同GGA
     * @param zda ZDA输出频率，同GGA
     * @param ant ANT输出频率，同GGA
     * @param dhv DHV输出频率，同GGA
     * @param lps LPS输出频率，同GGA
     * @param utc UTC输出频率，同GGA
     * @param gst GST输出频率，同GGA
     * @return 返回CAS03消息字符串
     **/
    public String productCAS03(String gga, String gll, String gsa, String gsv, String rmc, String vtg,
                               String zda, String ant, String dhv, String lps, String utc, String gst) {
        String content = CAS03 + COMMA + gga + COMMA + gll + COMMA + gsa + COMMA + gsv + COMMA + rmc + COMMA + vtg +
                COMMA + zda + COMMA + ant + COMMA + dhv + COMMA + lps + COMMA + COMMA + COMMA + utc + COMMA + gst;
        return HEADER + content + ENDER + CheckUtil.getCheckDigit(content) + FINISH;
    }

    /**
     * 获取CAS04自定义消息
     * 配置工作模式，使之只输出某项配置
     *
     * @param mode 工作系统配置模式，可选如下：
     *             1=GPS
     *             2=BDS
     *             3=GPS+BDS
     *             4=GLONASS
     *             5=GPS+GLONASS
     *             6=BDS+GLONASS
     *             7=GPS+BDS+GLONASS
     * @return 返回CAS04消息字符串
     **/
    public String productCAS04(int mode) {
        String content = CAS04 + COMMA + mode;
        return HEADER + content + ENDER + CheckUtil.getCheckDigit(content) + FINISH;
    }

    /**
     * 获取CAS05自定义消息
     * 配置NMEA协议类型
     *
     * @param mode NMEA协议类型，可选如下：
     *             2=兼容NMEA4.1以上版本
     *             5=兼容中国交通运输信息中心的BDS/GPS双模协议，兼容NMEA2.3以上版本，兼容NMEA4.0版本，默认协议
     *             9=兼容单GPSNMEA0183协议，兼容NMEA2.2版本
     * @return 返回CAS05消息字符串
     **/
    public String productCAS05(int mode) {
        String content = CAS05 + COMMA + mode;
        return HEADER + content + ENDER + CheckUtil.getCheckDigit(content) + FINISH;
    }

    /**
     * 获取CAS06自定义消息
     * 查询接收机产品信息
     *
     * @param mode 查询产品的信息类型，可选如下：
     *             0=查询固件版本号
     *             1=查询硬件型号及序列号
     *             2=查询多模接收机的工作模式
     *             3=查询产品的客户编号
     *             5=查询升级代码信息
     * @return 返回CAS06消息字符串
     **/
    public String productCAS06(int mode) {
        String content = CAS06 + COMMA + mode;
        return HEADER + content + ENDER + CheckUtil.getCheckDigit(content) + FINISH;
    }

    /**
     * 获取CAS10自定义消息
     * 接收机重启
     *
     * @param mode 启动模式配置，可选如下：
     *             0=热启动。不使用初始化信息，备份存储中的所有数据有效
     *             1=温启动。不使用初始化信息，清除星历
     *             2=冷启动。不使用初始化信息，清除备份存储中除配置外的所有数据
     *             3=出厂启动。清除内存所有数据，并将接收机复位至出厂默认配置
     * @return 返回CAS10消息字符串
     **/
    public String productCAS10(int mode) {
        String content = CAS10 + COMMA + mode;
        return HEADER + content + ENDER + CheckUtil.getCheckDigit(content) + FINISH;
    }

    /**
     * 获取CAS12自定义消息
     * 接收机待机模式控制
     *
     * @param second 进入待机模式时间，单位秒，范围0-65535，到达时间后设备重启
     * @return 返回CAS12消息字符串
     **/
    public String productCAS12(int second) {
        String content = CAS12 + COMMA + second;
        return HEADER + content + ENDER + CheckUtil.getCheckDigit(content) + FINISH;
    }

    /**
     * 获取CAS20自定义消息
     * 在线升级指令
     *
     * @return 返回CAS12消息字符串
     **/
    public String productCAS20() {
        return HEADER + CAS00 + ENDER + CheckUtil.getCheckDigit(CAS00) + FINISH;
    }
}
