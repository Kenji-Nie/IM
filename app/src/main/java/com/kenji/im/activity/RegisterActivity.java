package com.kenji.im.activity;

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
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText mUsernmae;
    private EditText mPassword;
    private EditText mName;
    private EditText mEmail;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mUsernmae = findViewById(R.id.username_reg);
        mPassword = findViewById(R.id.password_reg);
        mName = findViewById(R.id.name_reg);
        mEmail = findViewById(R.id.email_reg);

    }


    public void onRegisterBtnClick(View btn) {
        new RegisterTask().execute();
    }


    class RegisterTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            String username = mUsernmae.getText().toString();
            String password = mPassword.getText().toString();
            String name = mName.getText().toString();
            String email = mEmail.getText().toString();
            Map<String, String> attributes = new HashMap<>();
            attributes.put("name", name);
            attributes.put("email", email);

            AbstractXMPPConnection connection = ConnectionManager.getConnection();
            AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.sensitiveOperationOverInsecureConnection(true);

            try {
                accountManager.createAccount(Localpart.from(username), password, attributes);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }


            return true;
        }

        @Override
        protected void onPostExecute(Boolean isRegister) {
            if (isRegister) {
                Log.d("register", "register success");
            } else {
                Log.d("register", "register failed");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("login", "logout");

    }
}
