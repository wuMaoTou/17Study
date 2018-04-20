package com.maotou.appservice.util;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lichun on 18-4-16.
 */
public class CommonUtils {

    public static String getNowTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(System.currentTimeMillis());
        return dateString;
    }

    /**
     * 获得随机字符串
     *
     * @return
     */
    public static String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    public static Map<String, String> xmlToMap(String xmlstr) {
        Map<String, String> map = new HashMap<String, String>();

        try {
            SAXReader reader = new SAXReader();
            InputStream ins = new ByteArrayInputStream(xmlstr.getBytes("UTF-8"));
            Document doc = reader.read(ins);
            Element root = doc.getRootElement();

            List<Element> list = root.elements();

            for (Element e : list) {
                map.put(e.getName(), e.getText());
            }
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取时间戳
     *
     * @return
     */
    public static long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static void createXml(Iterator it, StringBuilder xmlBuilder) {
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            xmlBuilder.append("<").append(k).append(">");
            xmlBuilder.append(v);
            xmlBuilder.append("</").append(k).append(">");
        }
    }

    public static String getFileMd5(File file) {
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        int length = 1024 * 2;
        byte[] buffer = new byte[length];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, length)) > 0) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());

        return bigInt.toString(16);
    }

    public static String getOsName() {
        return System.getProperty("os.name").toLowerCase();
    }
}
