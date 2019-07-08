package com.kenji.im.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

public class AudioUtils {

    public static void play(String path, Context context) {
        SoundPool sp;
        //21-24之间可以
        if (Build.VERSION.SDK_INT >= 21) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .build();

            sp = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else {
            /**
             * 第一个参数：int maxStreams：SoundPool对象的最大并发流数
             * 第二个参数：int streamType：AudioManager中描述的音频流类型
             *第三个参数：int srcQuality：采样率转换器的质量。 目前没有效果。 使用0作为默认值。
             */
            sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }


        final AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        sp.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            if (status == 0) {
                int volume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                sp.play(sampleId, volume, volume, 1, 0, 1);
            }

        });
        sp.load(path, 1);


    }
}
