
import org.apache.commons.lang.StringUtils;

public class IpUtil {
    /**
     * 将IP转化为
     */
    public static Long plainToHex(String ipStr) {
        if (ipStr != null && ipStr.trim().length() != 0) {
            long ipNum = 0;
            String[] arr = ipStr.split("\\.");
            for (String str : arr) {
                // 向左移8位
                ipNum = ipNum << 8;
                ipNum += Integer.parseInt(str);
            }
            return ipNum;
        }
        return null;
    }

    /**
     * 将IP转化为16进制数
     *
     * @return 原始IP
     */
    public static String hexToPlain(Long ipLong) {
        if (ipLong == null || 0 == ipLong.longValue()) {
            return null;
        }
        StringBuffer sb = new StringBuffer("");
        // 直接右移24位
        sb.append(String.valueOf((ipLong >>> 24)));
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append(String.valueOf((ipLong & 0x00FFFFFF) >>> 16));
        sb.append(".");
        // 将高16位置0，然后右移8位
        sb.append(String.valueOf((ipLong & 0x0000FFFF) >>> 8));
        sb.append(".");
        // 将高24位置0
        sb.append(String.valueOf((ipLong & 0x000000FF)));
        return "".equals(sb.toString().trim()) ? null : sb.toString();
    }

    /**
     * 返回点分十进制形式
     *
     * @param str
     *
     * @return
     */
    public static String toPlain(String str) {
        if (StringUtils.isNotBlank(str) && str.matches("\\d+")) {
            return hexToPlain(Long.valueOf(str));
        } else {
            return str;
        }
    }
}
