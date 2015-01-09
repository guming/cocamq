package org.jinn.cocamq.test.serialized;


import java.io.*;

/**
 * Created by gumingcn on 2014/12/15.
 */
public class Test {

    public static void main(String[] args) {

        Person p = new Person();
        p.setId(1);
        p.setName("a");
        String temp2 = "{\"action\":\"edit\",\"redis_key_hash\":\"1\",\"DB_key_hash\":\"\"," +
                "\"time\":\"1406168332.35081900\",\"source\":\"web\",\"mars_cid\":\"\"," +
                "\"session_id\":\"\",\"info\":{\"cart_id\":\"6185\",\"user_id\":\"" + 10 + "\",\"brand_id\":\"7511\"," +
                "\"num\":2,\"warehouse\":\"as大劫案快解放但就是放得开束ash侃大山" +
                "ash看动画东方航空上帝会富士康解释都很费劲第三方还是开货到付款导师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                "ash看动画东方航空上帝会富士康解释都很费劲第三方还是开货到付款导师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                "都很费劲第三方还是开货到付款导师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                "ash看动画东方航空上帝会富士康解释都很费劲第三方还师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                "父花雕鸡开户行静安寺咁大噶就是个法华经爱就是大是大非带结发华东师范\",\"merchandise_id\":\"1001950\",\"channel\":\"te\"," +
                "\"cart_record_id\":\"8765\",\"size_id\":\"2756943\"}}";
        p.setAge(30);
        p.setDesc(temp2);

        try {
            int size=0;
            long start=System.currentTimeMillis();
            for (int i = 0; i <10000 ; i++) {
                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                ObjectOutputStream oos=new ObjectOutputStream(bos);
                oos.writeObject(p);
                byte[] result=bos.toByteArray();
                if (size==0) {
                    size=result.length;
                }
                ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(result));
                Object newPerson = oin.readObject(); // 没有强制转换到Person类型
                oin.close();
                result=null;
                newPerson=null;
            }
            System.out.println("jdk serialized time:"+(System.currentTimeMillis()-start)+",size:"+size);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.gc();
        PersonProto.Person.Builder builder= PersonProto.Person.newBuilder();
        builder.setId(1);
        builder.setAge(40);
        builder.setName("b");
        builder.setDesc(temp2);
        PersonProto.Person person= builder.build();
        try {
            int size=0;
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                person.writeTo(oos);
                oos.close();
                byte[] result=bos.toByteArray();
                if (size==0) {
                    size=result.length;
                }
                ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(result));
                PersonProto.Person person2=PersonProto.Person.parseFrom(oin);
            }
            System.out.println("protobuf serialized time:" + (System.currentTimeMillis() - start)+",size:"+size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
