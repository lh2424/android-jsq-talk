package jsq.talk.jsq.TalkBaseClass;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import jsq.talk.jsq.R;
import jsq.talk.jsq.UntilDataClass.PersonInfoClass;
import talk.lib.appshow.TalkCoreClass;


/**
 * Created by lianghong on 2019/2/4.
 */

//聊天详情显示的类,可以根据情况来调整修改,也可以自行增加类型显示一些
public class TalkDataClass {

    public String talkTextData = "";
    public String talkId = "";

    public String showName = "";
    public String showImageUrl = "";
    public int showImage = R.drawable.translate_png;
    public int showImageBack = R.drawable.translate_png;
    public String showTalkText = "";

    public String showName1 = "";
    public String showImageUrl1 = "";
    public int showImage1 = R.drawable.translate_png;
    public int showImageBack1 = R.drawable.translate_png;
    public String showTalkText1 = "";

    //根据接收生成类对象
    public TalkDataClass(String jsonString, Context context) {

        talkTextData = jsonString;
        try {
            JSONObject object = new JSONObject(talkTextData);
            talkId = object.getString("talkId");

            if (talkId.equals(TalkCoreClass.chageStringToHexString(PersonInfoClass.getInstance().myUserId)))
            {
                showName1 = object.getString("showName");
                showImageUrl1 = object.getString("showImageUrl");
                showTalkText1 = object.getString("showTalkText");
                showImageBack1 = R.drawable.chat_bg_0;
            }
            else
            {
                showName = object.getString("showName");
                showImageUrl = object.getString("showImageUrl");
                showTalkText = object.getString("showTalkText");
                showImageBack = R.drawable.chat_bg_1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
