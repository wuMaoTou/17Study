package com.maotou.multiimageuploaddemo;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by wuchundu on 2018/7/2.
 */

public class Util {
    public static String getObjectKeyPath(String uploadFilePath) {
        /**
         *
         9.车镇易卖
         /Uploads/ebuy/日期/文件名。
         如：/Uploads/ebuy/20170301/m184119v1326w.jpg
         */
        String pathname;
        if (!TextUtils.isEmpty(uploadFilePath)) {
            pathname = getFilename(uploadFilePath);
        } else {
            pathname = "";
        }
        return new StringBuffer().append("Uploads/ebuy/").append(getDate()).append("/").append(pathname).toString();
    }

    /**
     * 返回指定97至122 之的随机整数 的ASCII值返回字符  +  时分秒 +返回指定97至122 之的随机整数  +
     * 1000至2000随机整数  +  返回指定97至122 之的随机整数 的ASCII值返回字符  +  文件后缀
     *
     * @param path
     * @return
     */
    public static String getFilename(String path) {
        Random random = new Random();
        String as1 = (random.nextInt(122) % (122 - 97 + 1) + 97) + "";
        String a1 = "";
        for (byte b : as1.getBytes()) {
            char c = (char) (b + 49);
            a1 = String.valueOf(c) + a1;
        }
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
        String a2 = formatter.format(date);
        String a3 = (random.nextInt(122) % (122 - 97 + 1) + 97) + "";
        String a4 = (random.nextInt(2000) % (2000 - 1000 + 1) + 1000) + "";
        String as5 = (random.nextInt(122) % (122 - 97 + 1) + 97) + "";
        String a5 = "";
        for (byte b : as5.getBytes()) {
            char c = (char) (b + 49);
            a5 = String.valueOf(c) + a5;
        }
        String a6 = path.substring(path.lastIndexOf("."));
        StringBuilder sb = new StringBuilder();
        return sb.append(a1).append(a2).append(a3).append(a4).append(a5).append(a6).toString();
    }

    public static String getDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(date);
    }

    public static String sendPost(String url, HashMap<String, String> hashUrl) {
        //拼接hashmap参数字符串
        String hashString = "";
        Iterator iter = hashUrl.entrySet().iterator();
        while (iter.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            try {
                hashString += key + "=" + URLEncoder.encode(val, "UTF-8");
            } catch (Exception e) {
                // TODO: handle exception
            }
            if (iter.hasNext())
                hashString += "&";
        }

        //输入请求网络日志
        System.out.println("post_url=" + url);
        System.out.println("post_param=" + hashString);

        BufferedReader in = null;
        String result = "";
        HttpURLConnection conn = null;

        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(10000);//设置连接超时
            conn.setReadTimeout(10000);//设置读取超时
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //上传文件 content_type
//      conn.setRequestProperty("Content-Type", "multipart/form-data; boudary= 89alskd&&&ajslkjdflkjalskjdlfja;lksdf");
            conn.connect();
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
            osw.write(hashString);
            osw.flush();
            osw.close();
            if (conn.getResponseCode() == 200) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    result += inputLine;
                }
                System.out.println("post_result=" + result);
                in.close();
            }

        } catch (SocketTimeoutException e) {
            //连接超时、读取超时
            e.printStackTrace();
            return "POST_Exception";
        } catch (ProtocolException e) {
            e.printStackTrace();
            return "POST_Exception";
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("发送 POST 请求出现异常！" + e.getMessage() + "//URL=" + url);
            e.printStackTrace();
            return "POST_Exception";
        } finally {
            try {
                if (conn != null) conn.disconnect();
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

}
