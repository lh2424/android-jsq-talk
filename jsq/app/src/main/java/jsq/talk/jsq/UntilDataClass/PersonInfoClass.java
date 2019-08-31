package jsq.talk.jsq.UntilDataClass;

import android.content.Context;

import org.json.JSONObject;

import talk.lib.appshow.TalkCoreClass;

/**
 * Created by lianghong on 2019/8/14.
 */

public class PersonInfoClass {

    //我的编号id
    public int myUserId=-1;
    //昵称
    public String nickName = "";
    //我的头像链接
    public String pictureUrl = "http://thirdwx.qlogo.cn/mmopen/vi_32/ggSLjPmETLG07RQoN4uOnhRhXiax1heH98EQibWoiagbQoQrSWRTNZgKhIcWnMGPdw0IhbeE8ibGdaIPzm5tibnqXzg/132";
    //以上信息可以自己搭建改写

    private static class Holder{
        private static PersonInfoClass INSTANCE = new PersonInfoClass();
    }
    private PersonInfoClass(){}
    public static PersonInfoClass getInstance(){
        return PersonInfoClass.Holder.INSTANCE;
    }

    public String joinSendData(String reciveId,String showTalkText,Context context)
    {
        String talkId = TalkCoreClass.chageStringToHexString(myUserId);
        String showName = "登录id:"+myUserId;
        String showImageUrl = pictureUrl;

        String talkJsonText = "";
        //把发送的内容改成json字串,就可以带很多信息,后面的扩展红包和其他等等特殊信息
        try {
            JSONObject object = new JSONObject();
            object.put("talkId",talkId);
            object.put("reciveId",reciveId);
            object.put("showName",showName);
            object.put("showImageUrl",showImageUrl);
            object.put("showTalkText",showTalkText);
            talkJsonText = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return talkJsonText;
    }
}
