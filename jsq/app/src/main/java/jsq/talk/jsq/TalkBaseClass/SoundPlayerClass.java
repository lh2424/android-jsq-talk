package jsq.talk.jsq.TalkBaseClass;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by lianghong on 2019/7/22.
 */

public class SoundPlayerClass {

    public static SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);;
    public static String soundState = "soundOpenClose";

    public static void playSoundFromId(int soundId,Context context)
    {
        soundPool.load(context,soundId,1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(1,1, 1, 0, 0, 1);
            }
        });
    }
}
