package jsq.talk.jsq;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import jsq.talk.jsq.TalkBaseClass.NotificationHelper;
import jsq.talk.jsq.TalkBaseClass.SaveTalkDataClass;
import jsq.talk.jsq.TalkBaseClass.SoundPlayerClass;
import jsq.talk.jsq.TalkByServerActivity.TalkToActivity;
import jsq.talk.jsq.UntilDataClass.PersonInfoClass;
import jsq.talk.jsq.UntilFuctionClass.OperationFuction;
import talk.lib.appshow.TalkCoreClass;

public class MainActivity extends BaseActivity{

    private Button bt_temp0,bt_temp1;
    private ListView list_temp0;
    private EditText edit_temp0,edit_temp1;
    private ClassDataListAdapter classDataListAdapter;

    @Override
    public void onResume() {
        super.onResume();

        reloadTalkIndexList();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initHeadData() {

        setTitle("聊天列表");

        setLeftBtnVisible("注销");
        reloadRightButton();

        edit_temp0 = (EditText) findViewById(R.id.edit_temp0);
        edit_temp1 = (EditText) findViewById(R.id.edit_temp1);
        bt_temp0 = (Button) findViewById(R.id.bt_temp0);
        bt_temp1 = (Button) findViewById(R.id.bt_temp1);
        list_temp0 = (ListView) findViewById(R.id.list_temp0);
    }

    @Override
    protected void initMainView() {

        classDataListAdapter = new ClassDataListAdapter(baseContext);
        list_temp0.setAdapter(classDataListAdapter);
        list_temp0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {

                if (!TalkCoreClass.getInstance(MainActivity.this).isLoginSucceed())
                {
                    Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (classDataListAdapter.arraryTalkData.size() >0) {
                    HashMap map = classDataListAdapter.arraryTalkData.get(position);
                    String talkId = map.get("talkId").toString();
                    SaveTalkDataClass.getInstance().updataSocketParamListArray(talkId,baseContext);
                    baseContext.startActivity(new Intent().putExtra("talkId",talkId).setClass(baseContext, TalkToActivity.class));
                }
            }
        });

        initMainUIListener();

        TalkCoreClass.getInstance(MainActivity.this).starSocketServer(PersonInfoClass.getInstance().myUserId);

        TalkCoreClass.getInstance(MainActivity.this).receviedNetWorkCallbackInter = new TalkCoreClass.NetWorkCallbackInterface() {
            @Override
            public void showCallback(String result) {

                System.out.print("收到的内容:"+result);
                //保存和处理收到的信息,仅供参考
                operationSocketDataFromRecevied(result);
            }
        };

        String tempUserId = SaveTalkDataClass.getInstance().getParamFromKey("myUserId",MainActivity.this);

        if (!tempUserId.equals("0"))
        {
            int tempInt = Integer.valueOf(tempUserId);
            //登录函数
            TalkCoreClass.getInstance(MainActivity.this).loginAccount(tempInt);
            //本地缓存
            PersonInfoClass.getInstance().myUserId = tempInt;
            edit_temp0.setText(tempUserId);
            edit_temp0.setEnabled(false);
            bt_temp0.setText("已登录");
            bt_temp0.setEnabled(false);
        }
    }

    //自己处理接收到的函数
    private void operationSocketDataFromRecevied(String result)
    {
        //获取声音状态,获取到就发声音
        String soundState = SaveTalkDataClass.getInstance().getParamFromKey(SoundPlayerClass.soundState,MainActivity.this);
        if (soundState.equals("1"))
        {
            SoundPlayerClass.playSoundFromId(R.raw.notify,MainActivity.this);
        }

        //保存进聊天列表和聊天详情里,解析收到的json
        try {
            JSONObject object = new JSONObject(result);
            //保存给聊天列表的SharedPreferences,可以用其他保存方式
            SaveTalkDataClass.getInstance().putSocketParamListArray(object.get("talkId").toString(),result,MainActivity.this);
            //保存进聊天详情数据的SharedPreferences,可以用其他保存方式
            SaveTalkDataClass.getInstance().putSocketParamArray(object.get("talkId").toString(),result,MainActivity.this);
            //提示到推送栏目
            NotificationHelper.showNotificationView(object.get("showName").toString(),object.get("showTalkText").toString(),MainActivity.this);
            //刷新列表
            reloadTalkIndexList();
            //通知到聊天详情
            Intent intent = new Intent();
            intent.putExtra("result",result);
            MainActivity.this.sendBroadcast(intent.setAction(TalkToActivity.TALKACTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取存储列表数据,刷新界面
    private void reloadTalkIndexList()
    {
        ArrayList<HashMap> arrary = SaveTalkDataClass.getInstance().getSocketParamListArray(baseContext);
        classDataListAdapter.setmMatchInfoData(arrary);
    }

    //封装的左键响应,看BaseActivity
    @Override
    protected void onClickLeftButton() {
        super.onClickLeftButton();

        if (!TalkCoreClass.getInstance(MainActivity.this).isLoginSucceed())
        {
            showTitleView("请先登录");
        }
        else
        {
            TalkCoreClass.getInstance(MainActivity.this).exitAccount();

            SaveTalkDataClass.getInstance().moveAllTalkData(MainActivity.this);
            showTitleView("退出登录成功");
            edit_temp0.setEnabled(true);
            bt_temp0.setEnabled(true);
            bt_temp0.setText("登录");
            reloadTalkIndexList();
        }
    }

    //封装的右键响应,看BaseActivity
    @Override
    protected void onClickRightButton() {
        super.onClickRightButton();

        String soundState = SaveTalkDataClass.getInstance().getParamFromKey(SoundPlayerClass.soundState,baseContext);
        if (soundState.equals("0"))
        {
            SaveTalkDataClass.getInstance().putParamFromKey(SoundPlayerClass.soundState,"1",baseContext);
        }
        else
        {
            SaveTalkDataClass.getInstance().putParamFromKey(SoundPlayerClass.soundState,"0",baseContext);
        }
        reloadRightButton();
    }

    //判断是否开启声音
    private void reloadRightButton()
    {
        String soundState = SaveTalkDataClass.getInstance().getParamFromKey(SoundPlayerClass.soundState,baseContext);
        if (soundState.equals("0"))
        {
            setRightImgVisible(R.drawable.btn_audio_close);
        }
        else
        {
            setRightImgVisible(R.drawable.btn_audio_open);
        }
    }

    //listview的适配
    private class ClassDataListAdapter extends BaseAdapter {

        private LayoutInflater inflater = null;
        public ArrayList<HashMap> arraryTalkData = new ArrayList<HashMap>();

        public ClassDataListAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setmMatchInfoData(ArrayList<HashMap> list) {
            if (list != null) {
                arraryTalkData = list;
                notifyDataSetChanged();
            }
        }

        public int getCount() {
            return arraryTalkData.size();
        }

        public Object getItem(int position) {
            return arraryTalkData == null ? null : arraryTalkData.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ClassDataListAdapter.ClientTalkViewHolder clientTalkViewHolder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_list_data1, parent, false);
                clientTalkViewHolder = new ClassDataListAdapter.ClientTalkViewHolder();
                clientTalkViewHolder.im_temp0 = (ImageView) convertView.findViewById(R.id.im_temp0);
                clientTalkViewHolder.im_temp1 = (ImageView) convertView.findViewById(R.id.im_temp1);
                clientTalkViewHolder.tx_temp0 = (TextView) convertView.findViewById(R.id.tx_temp0);
                clientTalkViewHolder.tx_temp1 = (TextView) convertView.findViewById(R.id.tx_temp1);
                convertView.setTag(clientTalkViewHolder);
            }
            else
            {
                clientTalkViewHolder = (ClassDataListAdapter.ClientTalkViewHolder) convertView.getTag();
            }
            //	draw
            if (arraryTalkData.size() > 0)
            {
                HashMap map = arraryTalkData.get(position);
                clientTalkViewHolder.tx_temp0.setText(map.get("showName").toString());
                String talkText = TalkCoreClass.changeStringSign(map.get("showTalkText").toString());
                clientTalkViewHolder.tx_temp1.setText(talkText);
                showImageUrl(map.get("showImageUrl").toString(), clientTalkViewHolder.im_temp0, R.drawable.default_head_head);

                if (map.get("notify").toString().equals("1"))
                {
                    clientTalkViewHolder.im_temp1.setImageDrawable(getResources().getDrawable(R.drawable.hongdian));
                }
                else
                {
                    clientTalkViewHolder.im_temp1.setImageDrawable(getResources().getDrawable(R.drawable.ic_into_view));
                }
            }
            return convertView;
        }

        private class ClientTalkViewHolder {
            ImageView im_temp0;
            ImageView im_temp1;
            TextView tx_temp0;
            TextView tx_temp1;
        }
    }

    //提示框公用
    private void showTitleView(String title)
    {
        CharSequence items[] = { "确定"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.default_head_head);
        builder.setTitle(title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        builder.create().show();
    }

    //响应按钮
    private void initMainUIListener() {
        myOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch(v.getId()) {
                    case R.id.bt_temp0:
                        if (OperationFuction.isNullString(edit_temp0.getText().toString()))
                        {
                            showTitleView("请输入登录编号");
                            return;
                        }

                        //你的登录编号,不同业务场景必须传入一个唯一表示身份的识别id
                        int tempInt = Integer.valueOf(edit_temp0.getText().toString());

                        if (tempInt< 1 || tempInt > 2147483647)
                        {
                            showTitleView("超出范围:1~2147483647");
                            return;
                        }

                        //登录函数
                        TalkCoreClass.getInstance(MainActivity.this).loginAccount(tempInt);
                        //本地缓存
                        PersonInfoClass.getInstance().myUserId = tempInt;
                        SaveTalkDataClass.getInstance().putParamFromKey("myUserId",tempInt+"",MainActivity.this);

                        showTitleView("登录成功");
                        bt_temp0.setText("已登录");
                        bt_temp0.setEnabled(false);
                        break;
                    case R.id.bt_temp1:
                        if (!TalkCoreClass.getInstance(MainActivity.this).isLoginSucceed())
                        {
                            showTitleView("请先登录");
                            return;
                        }
                        if (OperationFuction.isNullString(edit_temp1.getText().toString()))
                        {
                            showTitleView("请输入对方编号");
                            return;
                        }

                        int tempInt1 = Integer.valueOf(edit_temp1.getText().toString());
                        if (tempInt1< 1 || tempInt1 > 2147483647)
                        {
                            showTitleView("超出范围:1~2147483647");
                            return;
                        }
                        String hexString = TalkCoreClass.chageStringToHexString(tempInt1);
                        baseContext.startActivity(new Intent().putExtra("talkId",hexString).setClass(baseContext, TalkToActivity.class));
                        break;
                    default:
                        break;
                }
            }
        };

        if (bt_temp0 != null) {
            bt_temp0.setOnClickListener(myOnClickListener);
        }
        if (bt_temp1 != null) {
            bt_temp1.setOnClickListener(myOnClickListener);
        }
    }
}
