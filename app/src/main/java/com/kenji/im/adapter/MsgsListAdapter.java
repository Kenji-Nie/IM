package com.kenji.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.kenji.im.R;
import com.kenji.im.bean.ChatMessage;
import com.kenji.im.utils.AudioUtils;
import com.kenji.im.utils.ImConstants;

import java.util.List;

public class MsgsListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<ChatMessage> msgs;

    public MsgsListAdapter(Context context, List<ChatMessage> msgs) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.msgs = msgs;
    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public Object getItem(int position) {
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = msgs.get(position);
        if ("in".equals(message.getMsgType().name()))
            convertView = inflater.inflate(R.layout.chat_msg_in, null);
        else
            convertView = inflater.inflate(R.layout.chat_msg_out, null);
        TextView tv_sender = convertView.findViewById(R.id.tv_sender);
        tv_sender.setText(message.getSender());
        TextView tv_date = convertView.findViewById(R.id.tv_date);
        tv_date.setText(message.getDate());
        TextView tv_content = convertView.findViewById(R.id.tv_content);
        tv_content.setText(message.getContent());

        if (message.getMsgModel() == ChatMessage.MsgModel.audio) {
            convertView.setOnClickListener(v -> {
                if (message.getStatus() == ChatMessage.MsgStatus.success) {
                    String path = ImConstants.AUDIO_DIR.getAbsolutePath() + "/" + message.getFileName();
                    AudioUtils.play(path, context);
                }
            });
        }

        return convertView;
    }
}
