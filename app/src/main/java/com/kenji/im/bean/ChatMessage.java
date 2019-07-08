package com.kenji.im.bean;

import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatMessage {

    private String sender;
    private String content;
    private String date;

    private MsgType msgType;
    private MsgModel msgModel;

    private long duration;
    private String fileName;

    private MsgStatus status;


    public enum MsgType {
        in,
        out
    }

    public enum MsgModel {
        text,
        audio,
        pic
    }

    public enum MsgStatus {
        success,
        failed,
        wait

    }

    public ChatMessage() {
    }

    /**
     * 构造文字消息
     *
     * @param sender
     * @param content
     * @param date
     * @param msgType
     */
    public ChatMessage(String sender, String content, String date, MsgType msgType) {
        this.sender = sender;
        this.content = content;
        this.date = date;
        this.msgType = msgType;
        this.msgModel = MsgModel.text;
    }

    /**
     * 构造语音消息
     *
     * @param sender
     * @param date
     * @param msgType
     * @param duration
     * @param fileName
     */
    public ChatMessage(String sender, String date, MsgType msgType, long duration, String fileName) {
        this.sender = sender;
        this.date = date;
        this.msgType = msgType;
        this.duration = duration;
        this.fileName = fileName;
        this.content = (duration / 1000) + "\'" + (duration % 1000) + "\'语音消息";
        this.msgModel = MsgModel.audio;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }


    public MsgModel getMsgModel() {
        return msgModel;
    }

    public void setMsgModel(MsgModel msgModel) {
        this.msgModel = msgModel;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public MsgStatus getStatus() {
        return status;
    }

    public void setStatus(MsgStatus status) {
        this.status = status;
    }

    public String toJson() throws JSONException {
//        JSONObject object = new JSONObject();
//        object.put("sender", sender);
//        object.put("content", content);
//        object.put("date", date);
//        if (msgType != null)
//            object.put("msgType", msgType);
//        if (msgModel != null)
//            object.put("msgModel", msgModel);
//        object.put("duration", duration);
//        object.put("fileName", fileName);
//        if (status != null)
//            object.put("status", status);
//        return object.toString();
        return JSON.toJSONString(this);
    }

    public static ChatMessage parseChatMessage(String json) throws JSONException {
//        JSONObject object = new JSONObject(json);
//        ChatMessage message = new ChatMessage();
//        message.setSender(object.getString("sender"));
//        message.setContent(object.getString("content"));
//        message.setDate(object.getString("date"));
//        message.setMsgType(ChatMessage.MsgType.in);
//        if (!TextUtils.isEmpty(object.getString("msgModel")))
//            message.setMsgModel(ChatMessage.MsgModel.valueOf(object.getString("msgModel")));
//        message.setDuration(object.getInt("duration"));
//        message.setFileName(object.getString("fileName"));
//        if (!TextUtils.isEmpty(object.getString("status")))
//            message.setStatus(MsgStatus.valueOf(object.getString("status")));
//        return message;
        return JSON.parseObject(json, ChatMessage.class);
    }
}
