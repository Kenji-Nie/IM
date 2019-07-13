package com.kenji.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import com.kenji.im.R;
import com.kenji.im.adapter.FriendsExpandableListAdapter;
import com.kenji.im.manager.ConnectionManager;
import com.kenji.im.manager.RosterManager;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;

import java.util.Collection;

public class FriendActivity extends AppCompatActivity implements RosterListener, ExpandableListView.OnChildClickListener {

    private AbstractXMPPConnection connection;

    private RosterManager rosterManager;

    private Roster roster;

    private FriendsExpandableListAdapter adapter;

    private static final int MENU_ADD_FRIEND = 1;
    private static final int MENU_CHATROOM_OPERATE = 2;

    private static final int MESSAGE_ALERT_REQUEST_FRIEND = 1;

    private static final int MESSAGE_RELOAD_FRIENDS = 2;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_ALERT_REQUEST_FRIEND:
                    alertInviterDialog((Jid) msg.obj);
                    break;
                case MESSAGE_RELOAD_FRIENDS:
                    adapter.setData(roster.getGroups());
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        connection = ConnectionManager.getConnection();

        roster = Roster.getInstanceFor(connection);
        roster.addRosterListener(this);
        ExpandableListView elv = findViewById(R.id.elv_friendList);
        adapter = new FriendsExpandableListAdapter(this, roster.getGroups());
        elv.setAdapter(adapter);
        //注册监听器，需要将adapter中的isChildSelectable返回true
        elv.setOnChildClickListener(this);

        rosterManager = RosterManager.getInstance(connection);

        //消息监听器
        connection.addAsyncStanzaListener(packet -> {
            if (packet instanceof Presence) {
                Presence presence = (Presence) packet;
                Jid inviterJid = presence.getFrom();
                if (presence.getType() == Presence.Type.subscribe) {
//                        已经添加对方为好友
                    RosterEntry rosterEntry = roster.getEntry(inviterJid.asBareJid());
                    if (rosterEntry != null && TextUtils.equals("to", rosterEntry.getType().name())) {
                        Presence p = new Presence(Presence.Type.subscribed);
                        p.setTo(inviterJid);
                        connection.sendStanza(p);
                        return;
                    }
                    Message message = handler.obtainMessage(MESSAGE_ALERT_REQUEST_FRIEND, inviterJid);
                    handler.sendMessage(message);
                }
            }
        }, stanza -> true);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, MENU_ADD_FRIEND, 1, "添加好友");
        menu.add(2, MENU_CHATROOM_OPERATE, 2, "群聊一会");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ADD_FRIEND:
                Intent intentAF = new Intent(this, FriendAddActivity.class);
                startActivity(intentAF);
                break;
            case MENU_CHATROOM_OPERATE:
                Intent intentCO = new Intent(this, ChatRoomOperateActivity.class);
                startActivity(intentCO);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void alertInviterDialog(final Jid inviterJid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加好友请求");
        builder.setMessage("是否同意添加" + inviterJid.toString() + "为好友?");
        builder.setPositiveButton("确定", (dialog, which) -> {
            try {
                Presence p = new Presence(Presence.Type.subscribed);
                p.setTo(inviterJid);
                connection.sendStanza(p);
                rosterManager.addEntry(inviterJid.asBareJid(), inviterJid.getLocalpartOrNull().toString(), "我的好友");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        builder.setNegativeButton("拒绝", (dialog, which) -> {
            Presence presence = new Presence(Presence.Type.unsubscribed);
            try {
                connection.sendStanza(presence);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void entriesAdded(Collection<Jid> addresses) {
        handler.sendEmptyMessage(MESSAGE_RELOAD_FRIENDS);
    }

    @Override
    public void entriesUpdated(Collection<Jid> addresses) {
        handler.sendEmptyMessage(MESSAGE_RELOAD_FRIENDS);
    }

    @Override
    public void entriesDeleted(Collection<Jid> addresses) {
        handler.sendEmptyMessage(MESSAGE_RELOAD_FRIENDS);
    }

    @Override
    public void presenceChanged(Presence presence) {
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        RosterEntry rosterEntry = (RosterEntry) adapter.getChild(groupPosition, childPosition);
        BareJid jid = rosterEntry.getJid();
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chatToJid", jid.toString());
        startActivity(intent);
        return true;
    }
}
