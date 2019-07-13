package com.kenji.im.manager;

import com.kenji.im.bean.ChatRoom;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.*;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.*;

public class ChatRoomManager {

    public static final String CONFERENCE = "@conference.";

    /**
     * 创建一个房间
     *
     * @param connection
     * @param name
     * @param password
     * @param description
     * @return
     * @throws XmppStringprepException
     * @throws InterruptedException
     * @throws SmackException.NoResponseException
     * @throws MultiUserChatException.MucAlreadyJoinedException
     * @throws SmackException.NotConnectedException
     * @throws XMPPException.XMPPErrorException
     * @throws MultiUserChatException.NotAMucServiceException
     */
    public static MultiUserChat createRoom(AbstractXMPPConnection connection, String name, String password, String description) throws XmppStringprepException, InterruptedException, SmackException.NoResponseException, MultiUserChatException.MucAlreadyJoinedException, SmackException.NotConnectedException, XMPPException.XMPPErrorException, MultiUserChatException.NotAMucServiceException {
//    <field var="muc#roomconfig_roomname" type="text-single" label="房间名称"><value>liao</value></field><field var="muc#roomconfig_roomdesc" type="text-single" label="房间描述"><value>liao</value></field><field var="muc#roomconfig_changesubject" type="boolean" label="允许成员更改主题"><value>0</value></field><field var="muc#roomconfig_maxusers" type="list-single" label="最大房间成员人数"><option label="10"><value>10</value></option><option label="20"><value>20</value></option><option label="30"><value>30</value></option><option label="40"><value>40</value></option><option label="50"><value>50</value></option><option label="无"><value>0</value></option><value>30</value></field><field var="muc#roomconfig_presencebroadcast" type="list-multi" label="广播其存在的角色"><option label="审核者"><value>moderator</value></option><option label="参与者"><value>participant</value></option><option label="访客"><value>visitor</value></option><value>moderator</value><value>participant</value><value>visitor</value></field><field var="muc#roomconfig_publicroom" type="boolean" label="在目录中列出房间"><value>1</value></field><field var="muc#roomconfig_persistentroom" type="boolean" label="永久房间"><value>0</value></field><field var="muc#roomconfig_moderatedroom" type="boolean" label="房间需要审核"><value>0</value></field><field var="muc#roomconfig_membersonly" type="boolean" label="房间仅对成员开放"><value>0</value></field><field type="fixed"><value>注意：默认情况下，只有管理员才可以在仅用于邀请的房间中发送邀请。</value></field><field var="muc#roomconfig_allowinvites" type="boolean" label="允许成员邀请其他人"><value>0</value></field><field var="muc#roomconfig_passwordprotectedroom" type="boolean" label="需要密码才能进入房间"><value>0</value></field><field type="fixed"><value>如果需要密码才能进入房间，则必须在下面指定密码。</value></field><field var="muc#roomconfig_roomsecret" type="text-private" label="密码"/><field var="muc#roomconfig_whois" type="list-single" label="能够发现成员真实 JID 的角色"><option label="审核者"><value>moderators</value></option><option label="任何人"><value>anyone</value></option><value>anyone</value></field><field var="muc#roomconfig_allowpm" type="list-single" label="Allowed to Send Private Messages"><option label="任何人"><value>anyone</value></option><option label="审核者"><value>moderators</value></option><option label="参与者"><value>participants</value></option><option label="无"><value>none</value></option><value>anyone</value></field><field var="muc#roomconfig_enablelogging" type="boolean" label="记录房间聊天"><value>1</value></field><field var="x-muc#roomconfig_reservednick" type="boolean" label="仅允许注册昵称登录"><value>0</value></field><field var="x-muc#roomconfig_canchangenick" type="boolean" label="允许成员修改昵称"><value>1</value></field><field type="fixed"><value>允许用户注册房间</value></field><field var="x-muc#roomconfig_registration" type="boolean" label="允许用户注册房间"><value>1</value></field><field type="fixed"><value>可以指定该房间的管理员。请在每行提供一个 JID。</value></field><field var="muc#roomconfig_roomadmins" type="jid-multi" label="房间管理员"/><field type="fixed"><value>可以指定该房间的其他拥有者。请在每行提供一个 JID。</value></field><field var="muc#roomconfig_roomowners" type="jid-multi" label="房间拥有者">
        //群聊管理器
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        //房间jid
        String jid = name + CONFERENCE + connection.getXMPPServiceDomain();
        MultiUserChat muc = manager.getMultiUserChat(JidCreate.entityBareFrom(jid));
        muc.createOrJoin(Resourcepart.from(name));
        //对房间进行配置
        Form configForm = muc.getConfigurationForm();
        Form answerForm = configForm.createAnswerForm();
        //设置默认设置
        for (FormField formField : configForm.getFields())
            if (!formField.getType().equals(FormField.Type.hidden) && formField.getVariable() != null) {
                answerForm.setDefaultAnswer(formField.getVariable());
            }
        //其它配置
        List<EntityFullJid> owners = new ArrayList<>();
        owners.add(connection.getUser());
        //群主
        answerForm.setAnswer("muc#roomconfig_roomowners", owners);
        answerForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
        //房间是持久聊天室，会被保存
        answerForm.setAnswer("muc#roomconfig_persistentroom", true);
        //房间仅对成员开放
        answerForm.setAnswer("muc#roomconfig_membersonly", false);
        //允许占有者（群主）邀请其他人
        answerForm.setAnswer("muc#roomconfig_allowinvites", true);
        //允许加入的成员数
        answerForm.setAnswer("muc#roomconfig_maxusers", Arrays.asList("30"));
        //能够发现占有者真实JID的角色
//        answerForm.setAnswer("muc#roomconfig_whois", "anyone");
        //登录房间对话
        answerForm.setAnswer("muc#roomconfig_enablelogging", true);
        //允许注册的昵称登录
        answerForm.setAnswer("x-muc#roomconfig_reservednick", true);
        //允许使用者修改昵称
        answerForm.setAnswer("muc#roomconfig_changesubject", false);
        //允许用户注册房间
        answerForm.setAnswer("x-muc#roomconfig_registration", false);
        //进入是否需要密码
        answerForm.setAnswer("muc#roomconfig_roomsecret", password);
        //房间描述
        answerForm.setAnswer("muc#roomconfig_roomdesc", description);
        muc.sendConfigurationForm(answerForm);
        return muc;
    }

    /**
     * 加入群聊
     * @param connection
     * @param roomName
     * @param password
     * @param nickname
     * @throws XmppStringprepException
     * @throws XMPPException.XMPPErrorException
     * @throws MultiUserChatException.NotAMucServiceException
     * @throws SmackException.NotConnectedException
     * @throws InterruptedException
     * @throws SmackException.NoResponseException
     */
    public static void joinRoom(AbstractXMPPConnection connection, String roomName, String password, String nickname) throws XmppStringprepException, XMPPException.XMPPErrorException, MultiUserChatException.NotAMucServiceException, SmackException.NotConnectedException, InterruptedException, SmackException.NoResponseException {
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        String jid = roomName + CONFERENCE + connection.getXMPPServiceDomain();
        MultiUserChat muc = manager.getMultiUserChat(JidCreate.entityBareFrom(jid));

        //加入房间后可以看历史消息
        MucEnterConfiguration.Builder builder = muc.getEnterConfigurationBuilder(Resourcepart.from(nickname));
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 1, 1);
        MucEnterConfiguration configuration = builder.requestHistorySince(calendar.getTime())
                .withPassword(password)
                .timeoutAfter(5000)
                .build();

        muc.join(configuration);
    }


    /**
     * 获取用户群组
     * @param connection
     * @return
     * @throws XMPPException.XMPPErrorException
     * @throws SmackException.NotConnectedException
     * @throws InterruptedException
     * @throws SmackException.NoResponseException
     */
    public static List<ChatRoom> getJoinedRooms(AbstractXMPPConnection connection) throws XMPPException.XMPPErrorException, SmackException.NotConnectedException, InterruptedException, SmackException.NoResponseException {
        List<ChatRoom> chatRooms = new ArrayList<>();

        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        Set<EntityBareJid> rooms = manager.getJoinedRooms();

        for (EntityBareJid jid : rooms) {
            RoomInfo roomInfo = manager.getRoomInfo(jid);
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setJid(jid.toString());
            chatRoom.setName(roomInfo.getName());
            chatRoom.setOccupantsCount(roomInfo.getOccupantsCount());
            chatRoom.setDescription(roomInfo.getDescription());
            chatRooms.add(chatRoom);
        }
        return chatRooms;
    }

}
