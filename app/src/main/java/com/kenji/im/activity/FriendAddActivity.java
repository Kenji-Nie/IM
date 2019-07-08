package com.kenji.im.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.kenji.im.R;
import com.kenji.im.manager.ConnectionManager;
import com.kenji.im.manager.RosterManager;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;

import java.util.ArrayList;
import java.util.List;

public class FriendAddActivity extends Activity implements AdapterView.OnItemClickListener{


    private EditText et_search;
    private AbstractXMPPConnection connection;
    private ListView lv_results;
    private ArrayAdapter<CharSequence> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);
        connection = ConnectionManager.getConnection();

        et_search = findViewById(R.id.et_search);
        lv_results = findViewById(R.id.lv_results);
        lv_results.setOnItemClickListener(this);

    }

    public void search(View btn) {
        new SearchTask().execute();
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
        final String name = adapter.getItem(position).toString();
        try {
            final BareJid addToJid = JidCreate.bareFrom(Localpart.from(name), ConnectionManager.getConnection().getXMPPServiceDomain());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("添加好友");
            builder.setMessage("确定添加" + name + "为好友吗");
            builder.setPositiveButton("确定", (dialog, which) -> {
//                    Presence presence = new Presence(Presence.Type.subscribe);
//                    presence.setTo(addToJid);
//                    try {
//                        connection.sendStanza(presence);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                    }
                //添加好友
                RosterManager.getInstance(connection).addEntry(addToJid, name, "我的好友");
            });
            builder.setNegativeButton("取消", (dialog, which) -> {

            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SearchTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {


            String searchText = et_search.getText().toString();

            UserSearchManager searchManager = new UserSearchManager(connection);

            try {
                DomainBareJid searchService = JidCreate.domainBareFrom("search." + ConnectionManager.getDomain());
                Form searchFrom = searchManager.getSearchForm(searchService);
                Form answerForm = searchFrom.createAnswerForm();
                answerForm.setAnswer("Username", true);
//            answerForm.setAnswer("Name", true);
//            answerForm.setAnswer("Email", true);
                answerForm.setAnswer("search", searchText.trim());
                ReportedData reportedData = searchManager.getSearchResults(answerForm, searchService);
                List<ReportedData.Row> rows = reportedData.getRows();

                List<CharSequence> results = new ArrayList<>();

                for (ReportedData.Row row : rows) {
                    CharSequence username = row.getValues("Username").get(0);
                    results.add(username);
                }

                adapter = new ArrayAdapter<>(FriendAddActivity.this, R.layout.friend_search, R.id.tv_name, results);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean hasResult) {
            if (hasResult) {
                lv_results.setAdapter(adapter);
            } else {
                Log.d("search", "search failed");
            }
        }
    }


}
