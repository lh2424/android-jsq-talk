package jsq.talk.jsq.TalkByServerActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import jsq.talk.jsq.BaseActivity;
import jsq.talk.jsq.R;
import jsq.talk.jsq.TalkBaseClass.SaveTalkDataClass;
import jsq.talk.jsq.TalkBaseClass.TalkDataClass;
import jsq.talk.jsq.UntilDataClass.PersonInfoClass;
import jsq.talk.jsq.UntilFuctionClass.OperationFuction;
import jsq.talk.jsq.UntilUiClass.XListView;
import talk.lib.appshow.TalkCoreClass;

/**
 * Created by lianghong on 2019/2/3.
 */

public class TalkToActivity extends BaseActivity implements XListView.IXListViewListener{

    private ClassDataListAdapter classDataListAdapter;
    private XListView list_temp0;
    private Button bt_temp0;
    private EditText edit_temp0;
    private BaseActivity baseContext;
    //对方的信息编号
    private String reciveId;
    //组装的内容,原始内容
    private String JsonText,editString;
    //翻页,一次翻20页
    private int pageNum = 20;
    public static final String TALKACTION = "com.TalkToActivity";

    @Override
    protected int getContentView() {
        return R.layout.activity_talk_to;
    }

    @Override
    protected void initHeadData() {
        setTitle("交流");
        list_temp0 = (XListView) findViewById(R.id.list_temp0);
        bt_temp0 = (Button) findViewById(R.id.bt_temp0);
        edit_temp0 = (EditText) findViewById(R.id.edit_temp0);
    }

    @Override
    protected void initMainView() {

        Intent intent = getIntent();
        reciveId = intent.getStringExtra("talkId");

        classDataListAdapter = new ClassDataListAdapter(this);
        list_temp0.setAdapter(classDataListAdapter);
        list_temp0.setPullRefreshEnable(true);
        list_temp0.setXListViewListener((XListView.IXListViewListener) this);
        list_temp0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                list_temp0.setSelection(classDataListAdapter.arrayMap.size());
            }
        });

        ArrayList<TalkDataClass> arrayList = getArrayListFromTalkData(pageNum);
        classDataListAdapter.setmMatchInfoData(arrayList);
        reloadListView(arrayList.size());

        initMainUIListener();

        //发送成功后的回调,如果不成功就不会到这里
        TalkCoreClass.getInstance(TalkToActivity.this).sendNetWorkCallbackInter = new TalkCoreClass.NetWorkCallbackInterface() {
            @Override
            public void showCallback(String result) {

                System.out.print("发送的内容:"+result);
                //存聊天具体页面
                SaveTalkDataClass.getInstance().putSocketParamArray(reciveId,JsonText,baseContext);
                //存外部列表
                SaveTalkDataClass.getInstance().putSocketMyMainListArray(reciveId,editString,baseContext);
                edit_temp0.setText("");

                //发送成功后,显示的页面当前长度+1
                int nowSize = classDataListAdapter.arrayMap.size();
                ArrayList<TalkDataClass> arrayList = getArrayListFromTalkData(nowSize+1);
                classDataListAdapter.setmMatchInfoData(arrayList);
                list_temp0.setSelection(classDataListAdapter.arrayMap.size());
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(TALKACTION);
        registerReceiver(TalkToActivityReceiver, filter);
    }

    private BroadcastReceiver TalkToActivityReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
//            String result = intent.getStringExtra("result");
            int nowSize = classDataListAdapter.arrayMap.size();
            ArrayList<TalkDataClass> arrayList = getArrayListFromTalkData(nowSize+1);
            classDataListAdapter.setmMatchInfoData(arrayList);
            list_temp0.setSelection(classDataListAdapter.arrayMap.size());
        }
    };

    //获取记录的缓存,可以自行存储修改
    private ArrayList<TalkDataClass> getArrayListFromTalkData(int pageSize)
    {
        ArrayList<TalkDataClass> arrayList = null;
        try {
            arrayList = SaveTalkDataClass.getInstance().getSocketParamArray(reciveId,baseContext);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (arrayList.size() > pageSize)
        {
            ArrayList<TalkDataClass> tempArray = new ArrayList<TalkDataClass>();
            for (int i = arrayList.size() - pageSize; i < arrayList.size(); i ++) {
                TalkDataClass talkDataClass = arrayList.get(i);
                tempArray.add(talkDataClass);
            }
            return tempArray;
        }
        return arrayList;
    }

    private void initMainUIListener() {
        myOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.bt_temp0:
                        if (OperationFuction.isNullString(edit_temp0.getText().toString()))
                        {
                            Toast.makeText(baseContext,"请输入完整的内容",Toast.LENGTH_LONG).show();
                            return;
                        }

                        //去掉一些特殊文字等
                        editString =  edit_temp0.getText().toString().replaceAll("\\s*", "");
                        //适当控制长度
                        if (editString.length()>2000)
                        {
                            Toast.makeText(baseContext,"输入的内容不能超过2000字",Toast.LENGTH_LONG).show();
                            return;
                        }

                        //这个发送的内容可以自行编辑组织,样例这里就发送json字串
                        JsonText = PersonInfoClass.getInstance().joinSendData(reciveId,editString,baseContext);

                        boolean sucessSend = TalkCoreClass.sendContextData(reciveId,JsonText,baseContext);
                        if (!sucessSend)
                        {
                            Toast.makeText(baseContext,"输入的内容非法或长度超限,没有发送成功",Toast.LENGTH_LONG).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        if (bt_temp0 != null) {
            bt_temp0.setOnClickListener(myOnClickListener);
        }
    }

    @Override
    public void onRefresh() {
        list_temp0.setRefreshTime("刚刚");
        list_temp0.stopRefresh();
        list_temp0.stopLoadMore();

        int nowSize = classDataListAdapter.arrayMap.size();
        ArrayList<TalkDataClass> arrayList = getArrayListFromTalkData(nowSize+pageNum);
        classDataListAdapter.setmMatchInfoData(arrayList);
        reloadListView(arrayList.size() - nowSize);
    }

    @Override
    public void onLoadMore() {
        list_temp0.setRefreshTime("刚刚");
        list_temp0.stopRefresh();
        list_temp0.stopLoadMore();
    }

    private void reloadListView(int line)
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                list_temp0.setSelection(line);
            }
        }, 500);
    }

    private class ClassDataListAdapter extends BaseAdapter {

        private LayoutInflater inflater = null;
        public ArrayList<TalkDataClass> arrayMap = new ArrayList<TalkDataClass>();

        public ClassDataListAdapter(BaseActivity context) {
            baseContext = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setmMatchInfoData(ArrayList<TalkDataClass> list) {
            if (list != null) {
                arrayMap = list;
                if (arrayMap != null) {
                    notifyDataSetChanged();
                }
            }
        }

        public int getCount() {
            return arrayMap.size();
        }

        public Object getItem(int position) {
            return arrayMap == null ? null : arrayMap.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ClassDataListAdapter.ClientTalkViewHolder clientTalkViewHolder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_list_data0, parent, false);
                clientTalkViewHolder = new ClassDataListAdapter.ClientTalkViewHolder();
                clientTalkViewHolder.im_temp0 = (ImageView) convertView.findViewById(R.id.im_temp0);
                clientTalkViewHolder.im_temp1 = (ImageView) convertView.findViewById(R.id.im_temp1);
                clientTalkViewHolder.tx_temp0 = (TextView) convertView.findViewById(R.id.tx_temp0);
                clientTalkViewHolder.tx_temp1 = (TextView) convertView.findViewById(R.id.tx_temp1);
                clientTalkViewHolder.edit_temp0 = (EditText) convertView.findViewById(R.id.edit_temp0);
                clientTalkViewHolder.edit_temp1 = (EditText) convertView.findViewById(R.id.edit_temp1);
                convertView.setTag(clientTalkViewHolder);
            } else {
                clientTalkViewHolder = (ClassDataListAdapter.ClientTalkViewHolder) convertView.getTag();
            }
            //	draw
            if (arrayMap.size() > 0) {

                TalkDataClass talkDataClass = arrayMap.get(position);

                baseContext.showImageUrl(talkDataClass.showImageUrl, clientTalkViewHolder.im_temp0, talkDataClass.showImage);
                clientTalkViewHolder.tx_temp0.setText(talkDataClass.showName);

                String talkText = TalkCoreClass.changeStringSign(talkDataClass.showTalkText);

                clientTalkViewHolder.edit_temp0.setText(talkText);
                clientTalkViewHolder.edit_temp0.setBackgroundResource(talkDataClass.showImageBack);

                baseContext.showImageUrl(talkDataClass.showImageUrl1, clientTalkViewHolder.im_temp1, talkDataClass.showImage1);
                clientTalkViewHolder.tx_temp1.setText(talkDataClass.showName1);

                String talkText1 = TalkCoreClass.changeStringSign(talkDataClass.showTalkText1);
                clientTalkViewHolder.edit_temp1.setText(talkText1);
                clientTalkViewHolder.edit_temp1.setBackgroundResource(talkDataClass.showImageBack1);
            }
            return convertView;
        }

        private class ClientTalkViewHolder {
            ImageView im_temp0, im_temp1;
            TextView tx_temp0, tx_temp1;
            EditText edit_temp0, edit_temp1;
        }
    }
}