package com.kenji.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import com.kenji.im.R;
import com.kenji.im.adapter.MsgsListAdapter;
import com.kenji.im.bean.ChatMessage;
import com.kenji.im.manager.ConnectionManager;
import com.kenji.im.utils.ImConstants;
import com.kenji.im.utils.TimeUtils;
import com.kenji.im.views.RecordButton;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.*;
import org.json.JSONException;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.util.XmppStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends Activity implements IncomingChatMessageListener, RecordButton.OnRecordFinishedListener, FileTransferListener {

    AbstractXMPPConnection connection;
    private Chat chat;
    private EditText et_msg;
    private List<ChatMessage> messageList = new ArrayList<>();
    private String loggedUser;
    private String chatToJid;
    private MsgsListAdapter adapter;

    private final static int MESSAGE_IN_RECEIVED = 1;

    private FileTransferManager fileTransferManager;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_IN_RECEIVED:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        connection = ConnectionManager.getConnection();

        et_msg = findViewById(R.id.et_msg);
        ListView lv_msgs = findViewById(R.id.lv_msgs);
        adapter = new MsgsListAdapter(this, messageList);
        lv_msgs.setAdapter(adapter);

        RecordButton recordButton = findViewById(R.id.btn_record);
        recordButton.setOnRecordFinishedListener(this);


        loggedUser = XmppStringUtils.parseBareJid(connection.getUser().asEntityBareJidString());

        Intent intent = getIntent();
        chatToJid = intent.getStringExtra("chatToJid");

        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addIncomingListener(this);
        try {
            chat = chatManager.chatWith(JidCreate.entityBareFrom(chatToJid));
        } catch (Exception e) {
            e.printStackTrace();
        }

        fileTransferManager = FileTransferManager.getInstanceFor(connection);
        fileTransferManager.addFileTransferListener(this);
    }

    public void mSend(View btn) {
        String msg = et_msg.getText().toString();
        ChatMessage outMessage = new ChatMessage(loggedUser, msg, TimeUtils.getNow(), ChatMessage.MsgType.out);
        messageList.add(outMessage);
        adapter.notifyDataSetChanged();

        try {
            chat.send(outMessage.toJson());
        } catch (Exception e) {
            e.printStackTrace();
        }
        et_msg.setText("");

    }

    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        String msg = message.getBody();
        try {
            ChatMessage inMessage = ChatMessage.parseChatMessage(msg);
            inMessage.setMsgType(ChatMessage.MsgType.in);
            messageList.add(inMessage);
            handler.sendEmptyMessage(MESSAGE_IN_RECEIVED);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFinished(File audioFile, long duration) {
        if (audioFile == null)
            return;
        ChatMessage outMessage = new ChatMessage(loggedUser, TimeUtils.getNow(), ChatMessage.MsgType.out, duration, audioFile.getName());
        messageList.add(outMessage);
        adapter.notifyDataSetChanged();

        try {
            Log.d("EntityFullJid", chatToJid);
            OutgoingFileTransfer transfer = fileTransferManager.createOutgoingFileTransfer(JidCreate.entityFullFrom(chatToJid + "/" + connection.getConfiguration().getResource()));
            transfer.sendFile(audioFile, outMessage.toJson());
            updateMsgStauts(audioFile);

            chat.send(outMessage.toJson());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新文件发送状态为完毕
     *
     * @param audioFile
     */
    private void updateMsgStauts(File audioFile) {
        for (ChatMessage message : messageList) {
            if (audioFile.getName().equals(message.getFileName())) {
                message.setStatus(ChatMessage.MsgStatus.success);
                break;
            }
        }
    }

    @Override
    public void fileTransferRequest(FileTransferRequest request) {
        IncomingFileTransfer transfer = request.accept();
        if (!ImConstants.AUDIO_DIR.exists()) {
            ImConstants.AUDIO_DIR.mkdir();
        }
        File audioFile = new File(ImConstants.AUDIO_DIR, transfer.getFileName());

        try {
            transfer.receiveFile(audioFile);
            if (transfer.getStatus() == FileTransfer.Status.complete) {
                updateMsgStauts(audioFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
