package com.kenji.im.views;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.kenji.im.R;
import com.kenji.im.utils.ImConstants;
import com.kenji.im.utils.UUIDUtils;

import java.io.File;
import java.io.IOException;

public class RecordButton extends android.support.v7.widget.AppCompatButton {

    private Dialog dialog;
    private MediaRecorder recorder;
    private ImageView volumeIv;
    private MicVolumnPicker micVolumnPicker;

    private OnRecordFinishedListener onRecordFinishedListener;

    private long startTime;

    private File audioFile;

    private int[] volumeImgs = {
            R.drawable.mic1,
            R.drawable.mic2,
            R.drawable.mic3,
            R.drawable.mic4
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            volumeIv.setImageResource(volumeImgs[msg.what]);
        }
    };


    public RecordButton(Context context) {
        super(context);
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startRecord();
                break;
            case MotionEvent.ACTION_UP:
                stopRecord();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void stopRecord() {
        //关闭对话框
        if (dialog != null)
            dialog.dismiss();
        //关闭音量拾取
        if (micVolumnPicker != null) {
            micVolumnPicker.setRecoding(false);
        }
        //停止录音
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;

            if (System.currentTimeMillis() - startTime < 1000) {
                Toast.makeText(getContext(), "录音时间太短", Toast.LENGTH_SHORT).show();
                if(audioFile.exists())
                    audioFile.delete();
                return;
            }


        }

        if (onRecordFinishedListener != null) {
            onRecordFinishedListener.onFinished(audioFile, System.currentTimeMillis() - startTime);
        }

    }

    private void startRecord() {
        startTime = System.currentTimeMillis();
        //设置对话框
        dialog = new Dialog(getContext(), R.style.like_toast_dialog_style);
        volumeIv = new ImageView(getContext());
        volumeIv.setImageResource(R.drawable.mic2);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        dialog.addContentView(volumeIv, params);

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        if (!ImConstants.AUDIO_DIR.exists()) {
            ImConstants.AUDIO_DIR.mkdir();
        }

        //录音
        audioFile = new File(ImConstants.AUDIO_DIR, UUIDUtils.generte() + ".amr");
        recorder.setOutputFile(audioFile.getAbsolutePath());

        try {
            recorder.prepare();
            recorder.start();
            dialog.show();

            //启动音量拾取
            micVolumnPicker = new MicVolumnPicker();
            micVolumnPicker.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class MicVolumnPicker extends Thread {
        private boolean isRecoding = true;

        @Override
        public void run() {
            while (isRecoding) {
                SystemClock.sleep(200);
                if (recorder == null)
                    return;
                //音量分贝
                int amp = recorder.getMaxAmplitude();
                if (amp != 0) {
                    int volume = (int) (10 * Math.log(amp) / Math.log(10));
                    if (volume < 26)
                        handler.sendEmptyMessage(0);
                    else if (volume < 32)
                        handler.sendEmptyMessage(1);
                    else if (volume < 38)
                        handler.sendEmptyMessage(2);
                    else
                        handler.sendEmptyMessage(3);
                }

            }
        }

        public void setRecoding(boolean recoding) {
            isRecoding = recoding;
        }
    }

    public interface OnRecordFinishedListener {
        void onFinished(File audioFile, long duration);
    }

    public void setOnRecordFinishedListener(OnRecordFinishedListener onRecordFinishedListener) {
        this.onRecordFinishedListener = onRecordFinishedListener;
    }

}
