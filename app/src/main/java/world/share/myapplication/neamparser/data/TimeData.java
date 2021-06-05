package world.share.myapplication.neamparser.data;

/**
 * UTC时间
 *
 * @author mac  2021/6/5
 **/
public class TimeData {

    /**
     * 小时
     **/
    public int hour = -1;

    /**
     * 分钟
     **/
    public int minute = -1;

    /**
     * 秒钟
     **/
    public int second = -1;

    public TimeData() {

    }

    public TimeData(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    @Override
    public String toString() {
        return hour + ":" + minute + ":" + second;
    }
}
