package world.share.myapplication.ch34xcache;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import world.share.myapplication.neamparser.constant.NMEAMessageID;
import world.share.myapplication.neamparser.formparse.NMEAMessageIdParser;

import static world.share.myapplication.ch34xcache.Ch34xDivider.LINE;
import static world.share.myapplication.neamparser.constant.FieldLength.GGA_LENGTH;
import static world.share.myapplication.neamparser.constant.FieldLength.GLL_LENGTH;
import static world.share.myapplication.neamparser.constant.FieldLength.GSA_LENGTH;
import static world.share.myapplication.neamparser.constant.FieldLength.GSV_LENGTH;
import static world.share.myapplication.neamparser.constant.FieldLength.RMC_LENGTH;
import static world.share.myapplication.neamparser.constant.FieldLength.VTG_LENGTH;


/**
 * 传入经过CH34X芯片串口转USB的NMEA串口数据
 * 并将大段的串口数据进行拆分成单个nmea数据段进行缓存并回调
 * 这里把一段nmea数据分成四个部分
 * 1.头部$号
 * 2.内容主体
 * 3.尾部*号
 * 4.尾部校验符号，俩个字符长度
 *
 * @author wanxuedong  2021/6/4
 */
public class CH34xCahce {

    /**
     * CH340Cahce单例对象
     **/
    private static CH34xCahce instance;

    /**
     * 是否已经开始一段nmea数据
     **/
    private boolean useHead = false;
    /**
     * 是否准备结束一段nmea数据
     **/
    private boolean useEnd = false;
    /**
     * nmea整个字符串缓存位置索引
     **/
    private int index = 0;

    /**
     * 一段完整NMEA数据最大长度
     **/
    private final int MAX_CACHE = 100;

    /**
     * 临时nmea缓存数据
     **/
    private char[] cachesChars = new char[MAX_CACHE];

    /**
     * *后面的校验符校验个数，校验数字只会有俩个
     **/
    private int endIndex = 0;

    /**
     * nmea数据回调最短时间，避免数据返回十分频繁
     * 如果串口数据给的速度小于1秒一条，就会处理成约1秒一条
     * 如果串口数据给的速度大于1秒一条，就会根据串口数据的速度调用回调
     **/
    private int intervalTime = 1000;

    /**
     * 用于数据回调放在主线程
     **/
    private Handler handler;

    /**
     * 缓存nmea回调接口集合
     **/
    private List<OnCH34xListener> onCh34xListeners = new ArrayList<>();

    /**
     * 经过处理后的nmea字符串
     **/
    private List<String> nmeaString = new ArrayList<>();

    /**
     * 最大缓存nmea集合大小,装满清除，重新添加数据
     **/
    private int MAX_CACHE_LENGTH = 60;

    /**
     * 给读取加锁，避免读取同时进行导致数据异常
     **/
    private Object object = new Object();

    /**
     * 是否在回调数据
     **/
    private boolean handle = false;

    /**
     * 是否可以输出数据
     **/
    private boolean start = false;

    /**
     * 用于解析NMEA协议的卫星类型字段
     **/
    private NMEAMessageIdParser messageIdParser;

    public static CH34xCahce getInstance() {
        if (instance == null) {
            synchronized (CH34xCahce.class) {
                if (instance == null) {
                    instance = new CH34xCahce();
                }
            }
        }
        return instance;
    }

    /**
     * 添加nmea数据处理回调
     *
     * @param listener 数据监听回调
     **/
    public void addListener(OnCH34xListener listener) {
        if (listener == null) {
            return;
        }
        onCh34xListeners.add(listener);
    }

    /**
     * 开始输出数据
     **/
    public void start() {
        if (start) {
            return;
        }
        start = true;
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (start) {
                    try {
                        Thread.sleep(intervalTime);
                        handler.sendMessage(handler.obtainMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 停止输出数据
     **/
    public void stop() {
        if (!start) {
            return;
        }
        start = false;
    }

    /**
     * 处理nmea格式字符串
     *
     * @param nmeaString nmea格式字符串
     **/
    public void receive(String nmeaString) {
        if (TextUtils.isEmpty(nmeaString)) {
            return;
        }
        char[] chars = nmeaString.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            handleChar(chars[i]);
        }
    }

    /**
     * 判断字符串属于一段nmea数据头部还是尾部还是内容体
     *
     * @param aChar nmea数据中的字符
     **/
    private void handleChar(char aChar) {
        if (Ch34xDivider.HEAD == aChar) {

            //重置nmea数据缓存和索引位置
            endIndex = -1;
            index = 0;
            cachesChars = new char[MAX_CACHE];
            useEnd = false;

            //nmea头部
            addChar(aChar);
            useHead = true;
            useEnd = false;
        } else if (Ch34xDivider.END == aChar) {
            //nmea尾部
            addChar(aChar);
            useHead = false;
            useEnd = true;

        } else {
            if (useHead) {
                //nmea内容主体部分数据
                addChar(aChar);
            }
            if (useEnd) {
                if (endIndex != 2) {
                    //nmea尾部校验符
                    endIndex++;
                    addChar(aChar);

                    if (endIndex == 2) {
                        //处理整个nmea缓存数据
                        push(cachesChars);
                    }
                }
            }

        }
    }

    /**
     * 添加字符到缓存数据
     *
     * @param c 传入的nmea字符，不可属于特殊字符
     **/
    private void addChar(char c) {
        if (c != LINE && index <= MAX_CACHE) {
            cachesChars[index++] = c;
        }
    }

    /**
     * 添加一组nmea数据
     *
     * @param chars 一组nmea完整数据
     **/
    private void push(char[] chars) {
        synchronized (object) {
            if (handle) {
                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (nmeaString.size() > MAX_CACHE_LENGTH) {
                nmeaString.clear();
            }
            //检查NMEA数据合法性
            String messageID = messageIdParser.parseMessageId(String.valueOf(chars));
            String[] original = String.valueOf(chars).split(",");
            switch (messageID) {
                case NMEAMessageID.GGA:
                    if (original.length != GGA_LENGTH) {
                        return;
                    }
                    break;
                case NMEAMessageID.GLL:
                    if (original.length != GLL_LENGTH) {
                        return;
                    }
                    break;
                case NMEAMessageID.GSA:
                    if (original.length != GSA_LENGTH) {
                        return;
                    }
                    break;
                case NMEAMessageID.GSV:
                    if (original.length != GSV_LENGTH) {
                        return;
                    }
                    break;
                case NMEAMessageID.RMC:
                    if (original.length != RMC_LENGTH) {
                        return;
                    }
                    break;
                case NMEAMessageID.VTG:
                    if (original.length != VTG_LENGTH) {
                        return;
                    }
                    break;
                default:
            }
            //检查完毕，添加进集合
            nmeaString.add(String.valueOf(chars));
        }
    }

    /**
     * nmea数据回调处理
     **/
    private void pull() {
        if (onCh34xListeners.size() == 0) {
            return;
        }
        synchronized (object) {
            handle = true;
            if (nmeaString.size() > 0) {
                String nmeaString = this.nmeaString.remove(0);
                if (TextUtils.isEmpty(nmeaString)) {
                    return;
                }
                for (int i = 0; i < onCh34xListeners.size(); i++) {
                    onCh34xListeners.get(i).location(nmeaString.trim());
                }
            }
            object.notify();
            handle = false;
        }
    }

    /**
     * 防止反射破解单例
     **/
    private CH34xCahce() {
        if (instance != null) {
            throw new RuntimeException();
        }
        messageIdParser = new NMEAMessageIdParser();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                pull();
            }
        };
    }

    /**
     * 防止序列化破解单例
     **/
    private Object readResolve() {
        return instance;
    }

}
