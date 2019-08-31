package jsq.talk.jsq.TalkBaseClass;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lianghong on 2019/7/13.
 */

public class SaveTalkDataClass {

    private String socketData = "socketData";

    private String socketParamList = "SocketTalkListArray";
    private String socketTalkDeailList = "SocketTalkDeailArray";

    private static class Holder{
        private static SaveTalkDataClass INSTANCE = new SaveTalkDataClass();
    }
    private SaveTalkDataClass(){}
    public static SaveTalkDataClass getInstance(){
        return Holder.INSTANCE;
    }

    public void putParamFromKey(String keyParam,String valeParam, Context context)
    {
        SharedPreferences userInfo = context.getSharedPreferences(socketData, 0);
        SharedPreferences.Editor putInfo = userInfo.edit();
        putInfo.putString(keyParam, valeParam).commit();
    }

    public String getParamFromKey(String keyParam, Context context) {
        SharedPreferences getInfo = context.getSharedPreferences(socketData, 0);
        String strValue = getInfo.getString(keyParam, "0");
        return strValue;
    }

    public void moveAllTalkData(Context context)
    {
        SharedPreferences userInfo = context.getSharedPreferences(socketData, 0);
        SharedPreferences.Editor putInfo = userInfo.edit();
        putInfo.clear();
        putInfo.commit();
    }

    //获取和某个对象对话的聊天详情列表的,keyParam为关键字的索引
    public ArrayList<TalkDataClass> getSocketParamArray(String keyParam,Context context) throws UnsupportedEncodingException {
        //获取该对象聊天数组长度
        int tempInt = Integer.valueOf(getParamFromKey(socketTalkDeailList+keyParam,context));
        System.out.printf("SaveTalkDataClass:"+keyParam+" -- "+tempInt);
        ArrayList<TalkDataClass> arraryMap = new ArrayList<TalkDataClass>();
        for (int i = 0; i < tempInt; i++)
        {
            //对象的key拼接0到n作为聊天记录的数组获取
            String tempS = getParamFromKey(socketTalkDeailList+keyParam+i,context);
            TalkDataClass talkDataClass = new TalkDataClass(tempS,context);
            arraryMap.add(talkDataClass);
        }
        return arraryMap;
    }

    //存放和某个对象对话的聊天详情列表
    public void putSocketParamArray(String keyParam,String valeParam,Context context) {
        int tempInt = Integer.valueOf(getParamFromKey(socketTalkDeailList+keyParam,context));
        //对象的key拼接0到n作为聊天记录的数组保存,没有重复的存储方式
        putParamFromKey(socketTalkDeailList+keyParam+tempInt,valeParam,context);
        putParamFromKey(socketTalkDeailList+keyParam,tempInt+1+"",context);
        System.out.printf("SaveTalkDataClass:"+keyParam+" -- "+valeParam+" -- "+tempInt);
    }

    //存放聊天首页列表的更新信息,主动发送消息的保存方式
    public void putSocketMyMainListArray(String keyParam,String showTalkText,Context context)
    {
        int tempInt = Integer.valueOf(getParamFromKey(socketParamList,context));
        //如果原来就有聊天信息列表,就检测,没有就直接加
        int indexNum = -1;
        for (int i = 0; i < tempInt; i++) {
            String keyString = getParamFromKey(socketParamList + i, context);
            if (keyString.equals(keyParam)) {
                putParamFromKey(socketParamList+keyParam+"showTalkText", showTalkText, context);
                indexNum = i;
                break;
            }
        }

        if (indexNum == -1)
        {
            putParamFromKey(socketParamList, tempInt+1+"", context);
            putParamFromKey(socketParamList+tempInt, keyParam, context);

            putParamFromKey(socketParamList+keyParam+"talkId", keyParam, context);
            putParamFromKey(socketParamList+keyParam+"showTalkText", showTalkText, context);
        }

        System.out.printf("SaveTalkDataClass:"+keyParam+" -- "+tempInt);
    }

    //收到流消息存放聊天列表数据  keyParam=收到消息的id
    public void putSocketParamListArray(String keyParam,String valeParam,Context context) throws UnsupportedEncodingException {
        //  socketParamList  存放数组长度
        int tempInt = Integer.valueOf(getParamFromKey(socketParamList,context));
        int indexNum =-1;

        System.out.printf("SaveTalkDataClass:"+keyParam+"--"+tempInt);

        //如果原来就有聊天信息列表,就检测,没有就直接加
        for (int i = 0; i < tempInt; i++)
        {
            //socketParamList + i存放数组里面的  socketParamList+talkId,用这个做为键值存储内容
            String keyString = getParamFromKey(socketParamList + i, context);
            //重复的聊天对象覆盖更新
            if (keyString.equals(keyParam))
            {
                TalkDataClass talkDataClass = new TalkDataClass(valeParam,context);

                putParamFromKey(socketParamList+keyParam+"showName", talkDataClass.showName, context);
                putParamFromKey(socketParamList+keyParam+"showImageUrl", talkDataClass.showImageUrl, context);
                putParamFromKey(socketParamList+keyParam+"showTalkText", talkDataClass.showTalkText, context);
                putParamFromKey(socketParamList+keyParam+"notify", "1", context);
                indexNum = i;
                System.out.printf("SaveTalkDataClass:"+talkDataClass.showName+" -- "+indexNum+" -- "+talkDataClass.showImageUrl);
                break;
            }
        }
        //没有重复的聊天对象就加个对象
        if (indexNum == -1) {
            //数组长度加1  tempInt+1
            putParamFromKey(socketParamList, tempInt+1+"", context);
            //数组新增的位置刚好是tempInt
            putParamFromKey(socketParamList + tempInt, keyParam, context);

            TalkDataClass talkDataClass = new TalkDataClass(valeParam,context);
            putParamFromKey(socketParamList+keyParam+"talkId", keyParam, context);
            putParamFromKey(socketParamList+keyParam+"showName", talkDataClass.showName, context);
            putParamFromKey(socketParamList+keyParam+"showImageUrl", talkDataClass.showImageUrl, context);
            putParamFromKey(socketParamList+keyParam+"showTalkText", talkDataClass.showTalkText, context);
            putParamFromKey(socketParamList+keyParam+"notify", "1", context);

            System.out.printf("SaveTalkDataClass:"+talkDataClass.showName+" -- "+indexNum+" -- "+talkDataClass.showImageUrl);
        }
    }

    public void updataSocketParamListArray(String keyParam,Context context)
    {
        putParamFromKey(socketParamList+keyParam+"notify", "0", context);
    }

    //获取聊天列表数据
    public ArrayList<HashMap> getSocketParamListArray(Context context) {
        int tempInt = Integer.valueOf(getParamFromKey(socketParamList,context));
        ArrayList<HashMap> arraryMap = new ArrayList<HashMap>();
        if (tempInt > 0) {
            for (int i = 0; i < tempInt; i++) {
                String keyParam = getParamFromKey(socketParamList + i, context);
                HashMap map = new HashMap();
                map.put("talkId",getParamFromKey(socketParamList+keyParam+"talkId",context));
                map.put("showName",getParamFromKey(socketParamList+keyParam+"showName",context));
                map.put("showImageUrl",getParamFromKey(socketParamList+keyParam+"showImageUrl",context));
                map.put("showTalkText",getParamFromKey(socketParamList+keyParam+"showTalkText",context));
                map.put("notify",getParamFromKey(socketParamList+keyParam+"notify",context));

                System.out.printf("SaveTalkDataClass:"+map.get("talkId").toString()+" -- "+map.get("showName").toString()+" -- "+map.get("showImageUrl").toString());
                if (map.get("showImageUrl").toString().equals("0"))
                {
                    map.put("showName","");
                }
                arraryMap.add(map);
            }
        }
        return arraryMap;
    }
}
