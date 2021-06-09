package world.share.myapplication.neamparser.data;

/**
 * 卫星信息
 *
 * @author wanxuedong  2021/6/5
 */
public class SatellitesData {

    /**
     * 卫星编号，GPS卫星编号为PRN号，范围1-32，WAAS星编号为PRN号-87，范围33-64，GLONASS卫星编号为slot number+64,slot范围为1-24
     **/
    public String number;

    /**
     * 仰角，0-90,单位：度
     **/
    public String elevation;

    /**
     * 方位角，0-359,单位：度
     **/
    public String azimuth;

    /**
     * 信噪比，单位：DB
     **/
    public String ratio;

    public SatellitesData(String number, String elevation, String azimuth, String ratio) {
        this.number = number;
        this.elevation = elevation;
        this.azimuth = azimuth;
        this.ratio = ratio;
    }

}
