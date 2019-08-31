package jsq.talk.jsq;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import jsq.talk.jsq.UntilFuctionClass.OperationFuction;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//习惯用的一种方式,可以根据自己的喜好用,不用造搬
public abstract class BaseActivity extends AppCompatActivity {

    public Context baseContext;
    public ViewGroup contentView;
    private TextView head_textview;
    private Button head_btn_left,head_btn_right;
    private ImageButton head_image_left,head_image_right;
    private RelativeLayout relative_temp99;
    public View.OnClickListener myOnClickListener;
    public static int screemWith,screemHeight;
    private SoundPool soundPool;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics dm =getResources().getDisplayMetrics();
        screemWith = dm.widthPixels;
        screemHeight = dm.heightPixels;
//        Log.i(tag, "屏幕尺寸1: 宽度 = "+display.getWidth()+"高度 = :"+display.getHeight());
        setContentView(R.layout.head_layout_public);
        
        baseContext= BaseActivity.this;
    
        initHeadViewData();
        contentView=(ViewGroup) findViewById(R.id.base_contentview);
        contentView.addView(View.inflate(this, getContentView(), null));
        initHeadData();
        initMainView();
    }

    private void initHeadViewData() {
    
        relative_temp99 = (RelativeLayout) findViewById(R.id.relative_temp99);
        head_textview = (TextView) findViewById(R.id.head_textview);
        head_btn_left = (Button)findViewById(R.id.head_btn_left);
        head_btn_right = (Button)findViewById(R.id.head_btn_right);
        head_image_left = (ImageButton)findViewById(R.id.head_image_left);
        head_image_right = (ImageButton)findViewById(R.id.head_image_right);

        head_image_left.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    /**
     * 设置背景图片
     * @param imageId
     */
    public void setBackImage(int imageId){
        if (imageId > 0)
        {
	        contentView.setBackgroundResource(imageId);
        }
    }

    /**
     * 设置中间标题
     * @param title
     */
    public void setTitle(String title){
        head_textview.setText(title);
    }

    /**
     * 设置左侧图片按钮显示与隐藏
     * @param rid
     */
    public void setLeftImgVisible(int rid) {
        if (rid > 0)
        {
            head_image_left.setVisibility(View.VISIBLE);
            head_image_left.setImageDrawable(getResources().getDrawable(rid));
            head_image_left.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
            head_btn_left.setVisibility(View.GONE);
        }
        else
        {
            head_image_left.setVisibility(View.GONE);
        }
    }

    /**
     * 设置右侧图片按钮显示与隐藏
     * @param rid
     */
    public void setRightImgVisible(int rid) {
        if (rid > 0)
        {
            head_image_right.setVisibility(View.VISIBLE);
            head_image_right.setBackground(getResources().getDrawable(rid));
            head_image_right.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onClickRightButton();
                }
            });
            head_btn_right.setVisibility(View.GONE);
        }
        else
        {
            head_image_right.setVisibility(View.GONE);
        }
    }

    /**
     * 设置左侧按钮显示与隐藏
     * @param title
     */
    public void setLeftBtnVisible(String title) {
        if (title == null)
        {
            head_btn_left.setVisibility(View.GONE);
        }
        else
        {
            head_btn_left.setText(title);
            head_btn_left.setVisibility(View.VISIBLE);
            head_btn_left.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onClickLeftButton();
                }
            });
            head_image_left.setVisibility(View.GONE);
        }
    }

    /**
     * 设置右侧按钮显示与隐藏
     * @param title
     */
    public void setRightBtnVisible(String title) {
        if (title == null)
        {
            head_btn_right.setVisibility(View.GONE);
        }
        else
        {
            head_btn_right.setText(title);
            head_btn_right.setVisibility(View.VISIBLE);
            head_btn_right.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onClickRightButton();
                }
            });
            head_image_right.setVisibility(View.GONE);
        }
    }

    //用了okhttp的方式展示网络链接图片
    public void showImageUrl(String pictureurl , ImageView imageView, int resourceId)
    {
        if (pictureurl.equals("0")|| OperationFuction.isNullString(pictureurl))
        {
            imageView.setImageDrawable(getResources().getDrawable(resourceId));
            return;
        }

        File cacheFile = new File(getExternalCacheDir().toString(),"cache");
        Cache cache = new Cache(cacheFile,1024 * 1024 * 100);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache).build();

        Request request = new Request.Builder().url(pictureurl).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e)
            {
                imageView.setImageDrawable(getResources().getDrawable(resourceId));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final byte[] Picture_bt = response.body().bytes();

                BaseActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        {
                            try {
                                if (Picture_bt != null)
                                {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(Picture_bt, 0, Picture_bt.length);
                                    imageView.setImageBitmap(bitmap);
                                }
                                else
                                {
                                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.default_head_head));
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                imageView.setImageDrawable(getResources().getDrawable(resourceId));
                            }
                        }
                    }
                });
            }
        });
    }


    /**
     * 设置右侧图片按钮响应
     */
    protected void onClickRightImage() {

    }

    /**
     * 设置左侧文字按钮响应
     */
    protected void onClickLeftButton() {

    }

    /**
     * 设置右侧文字按钮响应
     */
    protected void onClickRightButton() {

    }

    /**
     * 获取中间内容显示区
     * @return
     */
    protected abstract int getContentView();

    /*
     * 设置头部内容
     */
    protected abstract void initHeadData();

    /*
     * 设置UI内容
     */
    protected abstract void initMainView();
    
}
