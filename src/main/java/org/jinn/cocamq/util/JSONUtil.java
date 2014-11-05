package org.jinn.cocamq.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class JSONUtil {
    private static final ObjectMapper objectMapper =  new ObjectMapper();

    static {
//        objectMapper.configure(DeserializationConfig., false);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("action","add");
        map.put("time", "1406168332.35081900");
        map.put("redis_key_hash", "1");
        DecimalFormat df=new DecimalFormat("#.00000000");
        try {
            String temp=beanToJson(map);
            System.out.println(temp);
            Map map1=(Map)jsonToBean(temp,HashMap.class);
            for (Object key:map1.keySet()){
                if(key.toString().equals("time"))
                    System.out.println("time,key,value:"+df.format(Double.valueOf(map1.get(key).toString())));
                System.out.println(key+",key,value:"+map1.get(key));
            }
            if(Double.valueOf("1406168332.35081900")>
                    Double.valueOf("1406168332.35081890")){
                System.out.println(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String beanToJson(Object obj) throws Exception {
        try {
            String json =objectMapper.writeValueAsString(obj);
            return json;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public static Object jsonToBean(String json, Class<?> cls) throws Exception {
        try {
            Object vo = objectMapper.readValue(json, cls);
            return vo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

}
