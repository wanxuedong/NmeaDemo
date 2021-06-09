package world.share.myapplication.neamparser.utils;

import android.text.TextUtils;

/**
 * 获取异或运算之和工具
 *
 * @author wanxuedong 2021/6/7
 */
public class CheckUtil {

    /**
     * 获取异或和校验字符串
     *
     * @param content 需要校验的字符串部分
     * @return 返回校验字符串异或之和的大写形式
     **/
    public static String getCheckDigit(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        //第一个字符是$，不需要参与运算,最后一个是*，也不需要
        char xor = content.charAt(0);
        for (int i = 1; i < content.length(); i++) {
            if (content.charAt(i) == '*') {
                break;
            } else {
                xor ^= content.charAt(i);
            }
        }
        //char类型是16位的，占俩个字节，其范围是0~65536
        //为了防止运算结果超出char的数值范围，取余数，范围0~65536
        int result = xor % 65536;
        //把result转成16进制
        String str = Integer.toString(result, 16);
        return str.toUpperCase();
    }

}
