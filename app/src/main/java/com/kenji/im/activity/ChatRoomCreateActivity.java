package com.kenji.im.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.kenji.im.R;
import com.kenji.im.manager.ChatRoomManager;
import com.kenji.im.manager.ConnectionManager;
import org.jivesoftware.smack.AbstractXMPPConnection;

public class ChatRoomCreateActivity extends Activity {
    private EditText et_name;
    private EditText et_password;
    private EditText et_desc;

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_create);
        et_name = findViewById(R.id.et_name);
        et_password = findViewById(R.id.et_password);
        et_desc = findViewById(R.id.et_desc);

    }

    public void create(View btn) {
        new CreateRoomTask().execute();
    }

    class CreateRoomTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            AbstractXMPPConnection connection = ConnectionManager.getConnection();
            String name = et_name.getText().toString();
            String password = et_password.getText().toString();
            String desc = et_desc.getText().toString();
            try {
                ChatRoomManager.createRoom(connection, name, password, desc);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(ChatRoomCreateActivity.this, "创建群聊成功！", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ChatRoomCreateActivity.this, "创建群聊失败!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
