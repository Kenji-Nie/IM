package com.kenji.im.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.kenji.im.R;
import com.kenji.im.manager.ConnectionManager;
import org.jivesoftware.smack.AbstractXMPPConnection;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameView;
    private EditText mPasswordView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);

    }

    public void onLoginBtnClick(View btn) {
        new LoginTask().execute();
    }

    public void onRegisterBtnClick(View btn) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);

    }

    class LoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            String username = mUsernameView.getText().toString();
            String password = mUsernameView.getText().toString();

            AbstractXMPPConnection connection = ConnectionManager.getConnection();

            try {
                connection.login(username, password);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean isLogin) {
            if (isLogin) {
                Log.d("login", "login success");
                Intent intent = new Intent(LoginActivity.this, FriendActivity.class);
                startActivity(intent);
            } else {
                Log.d("login", "login failed");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("login", "logout");
        ConnectionManager.close();
    }
}
