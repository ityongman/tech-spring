package com.ityongman.common;

import java.util.Random;

public class CodeUtil {

    /**
     * 自定义进制字符集
     * 32个数: 8个数字(去除'0'和'1'), 24个小写字母(去除 'o'和'l')
     * NOTE: 数字'0'和'1'容易与小写字母'o'和'l'混淆
     */
    private static final char[] RADIX_CHAR_ARRAY_LOWERCASE = new char[] {
            'p', 'i', 'm', '8',
            'k', 'n', '2',
            'h', 'y', 't', 'b', 'g', '4',
            'v', 'f', '5',
            'd', 'r', 'c', '3',
            'x', 's', '7',
            'e', 'w', 'q', 'z', '9',
            'u', 'j', 'a', '6'
    };

    private static final int RADIX = RADIX_CHAR_ARRAY_LOWERCASE.length;

    /**
     * 唯一码的最小长度
     * 若 id值 较小, 映射得到的自定义进制字符串, 长度小于该值, 则填充随机字符串
     */
    private static final int CODE_MIN_LENGTH = 6; // 需要可以扩展, 这里暂时不做

    /**
     * 分隔字符
     * <li>分隔 id 映射得到的自定义进制字符串 与 填充的随机字符串</li>
     */
    private static final char DIVISION = 'o';

    /**
     * 规则
     * 1. 获取当前时间的末 4位数
     * 2. 根据Math.random()方法生成一个随机4位数
     * 3. 按照 随机数 + 时间末尾数 --> 拼接一个字符串, 比如: 2233 + 4455 --> 22334455
     * @return
     */

    /*
     * 27 bit, 2^27=134217728
     */
    public static final int CODE_BITS = 27;


    public static  String generateCode(Integer type) {
        long time = System.currentTimeMillis();
        String baseId = String.valueOf((int) ((Math.random() + 1) * 10000)).substring(1) + String.valueOf(time).substring(9);

        return  generateCode(type, Integer.parseInt(baseId));
    }

    private static String generateCode(Integer type , Integer id) {
        long gcId = ((long) type << CODE_BITS) | id;
        return generateCode(gcId);
    }

    private static String generateCode(long id) {
        // 32^32 = 2^5^32 = 2^160, 远大于 long (8字节, 2^8^8=2^64)
        char[] buf = new char[32];
        int charIdx = 32;
        while ((id / RADIX) > 0) {
            int ind = (int) (id % RADIX);
            // System.out.println("--> " + ind);
            buf[--charIdx] = RADIX_CHAR_ARRAY_LOWERCASE[ind];
            id /= RADIX;
        }

        int ind = (int) (id % RADIX);
        // System.out.println("--> " + ind);
        buf[--charIdx] = RADIX_CHAR_ARRAY_LOWERCASE[ind];

        String str = new String(buf, charIdx, (32 - charIdx));

        // -------------------- 映射得到的自定义进制字符串, 不到唯一码的最小长度, 填充随机字符串 --------------------

        if (str.length() < CODE_MIN_LENGTH) {
            StringBuilder sb = new StringBuilder();

            // 分隔 id 映射得到的自定义进制字符串 与 填充的随机字符串
            sb.append(DIVISION);

            Random ran = new Random();
            for (int i = 1; i < CODE_MIN_LENGTH - str.length(); i++) {
                sb.append(RADIX_CHAR_ARRAY_LOWERCASE[ran.nextInt(RADIX)]);
            }
            str += sb.toString();
        }

        return str;
    }

}
