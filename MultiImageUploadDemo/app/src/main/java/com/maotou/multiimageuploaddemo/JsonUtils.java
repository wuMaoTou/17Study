package com.maotou.multiimageuploaddemo;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zm on 2016/2/25.
 */
public class JsonUtils<T> {


    //参数string为你的文件名
    public static String readFileContent(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String content = "";
        StringBuilder sb = new StringBuilder();
        while(content != null){
            content = bf.readLine();
            if(content == null){
                break;
            }
            sb.append(content.trim());
        }
        bf.close();
        return sb.toString();
    }



    /**
     * 获取返回的response中的status
     *
     * @param response 返回体
     * @return
     */
    public static int getStatus(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int status = jsonObject.getInt("status");
            return status;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static int getStateForData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int status = jsonObject.getInt("state");
            return status;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取返回的response中的info
     *
     * @param response 返回体
     * @return
     */
    public static String getInfo(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String info = jsonObject.getString("info");
            return info;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "数据解析异常";
    }

    /**
     * 获取返回的response中的data
     *
     * @param response 返回体
     * @return
     */
    public static String getData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String data = jsonObject.getString("data");
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 根据dataname获取返回的response中的data
     *
     * @param response 返回体
     * @return
     */
    public static String getData(String response, String dataname) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getString(dataname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getCount(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String count = jsonObject.getString("count");
            return count;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getType(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String type = jsonObject.getString("type");
            return type;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getUrl(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String url = jsonObject.getString("url");
            return url;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static <T> T getJson(String jsonString, Class<T> clz) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, clz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String setJson(Object object) {
        try {
            Gson gson = new Gson();
            return gson.toJson(object);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isValidateImg(List<String> imglist) {
        for (int i = 0; i < imglist.size(); i++) {
            String url = imglist.get(i);
            if (!new File(url).exists()&& !url.startsWith("http")){
               return false;
            }
        }
        return true;
    }

    /**
     * 将json格式的字符串解析成Map对象 <li>
     * json格式：{"name":"admin","retries":"3fff","testname"
     * :"ddd","testretries":"fffffffff"}
     */
    public static HashMap<String, String> toHashMap(String object)  {
        HashMap<String, String> data = new HashMap<String, String>();
        try{
            object = object.substring(1);
            object = object.replaceAll(" ","");
            object = object.substring(0,object.length()-1);
            String[] split = object.split(",");
            for(int i=0;i<split.length;i++){
                String str = split[i];
                if(!str.startsWith("phone")){
                    String[] split1 = str.split("=");
                    data.put(split1[0],split1[1]);
                }else{
                    data.put("phone",str.substring(6));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }
}
