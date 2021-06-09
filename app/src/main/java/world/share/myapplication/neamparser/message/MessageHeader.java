package world.share.myapplication.neamparser.message;

/**
 * 定义NMEA消息标准头部
 *
 * @author wanxuedong  2021/6/7
 */
public class MessageHeader {

    /**
     * 起始符
     **/
    public static final String HEADER = "$";

    /**
     * 逗号分割符
     **/
    public static final String COMMA = ",";

    /**
     * 结束符
     **/
    public static final String ENDER = "*";

    /**
     * 回车与换行符
     **/
    public static final String FINISH = "<CR><LF>";

    /**
     * CASOO消息ID，语句头
     **/
    public static final String CAS00 = "PCAS00";

    /**
     * CAS01消息ID，语句头
     **/
    public static final String CAS01 = "PCAS01";

    /**
     * CASO2消息ID，语句头
     **/
    public static final String CAS02 = "PCAS02";

    /**
     * CASO3消息ID，语句头
     **/
    public static final String CAS03 = "PCAS03";

    /**
     * CASO4消息ID，语句头
     **/
    public static final String CAS04 = "PCAS04";

    /**
     * CASO5消息ID，语句头
     **/
    public static final String CAS05 = "PCAS05";

    /**
     * CASO6消息ID，语句头
     **/
    public static final String CAS06 = "PCAS06";

    /**
     * CAS10消息ID，语句头
     **/
    public static final String CAS10 = "PCAS10";

    /**
     * CAS12消息ID，语句头
     **/
    public static final String CAS12 = "PCAS12";

    /**
     * CASOO消息ID，语句头
     **/
    public static final String CAS20 = "PCAS20";

}
