package sk.fei.mobv.project;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import sk.fei.mobv.project.model.User;
import sk.fei.mobv.project.settings.AccountGeneral;
import sk.fei.mobv.project.settings.ComplexPreferences;

import static sk.fei.mobv.project.settings.AccountGeneral.S_SERVER_API;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String PARAM_USER_PASS = "USER_PASS";

    private AccountManager mAccountManager;
    private String mAuthTokenType = AccountGeneral.ACCOUNT_TYPE;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_login);
        mAccountManager = AccountManager.get(getBaseContext());

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.ACCOUNT_TYPE;

        if (accountName != null) {
            ((TextView) findViewById(R.id.login)).setText(accountName);
        }

        findViewById(R.id.sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // The sign up activity returned that the user has successfully created an account
        int REQ_SIGNUP = 1;
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void submit() {
        final String userName = ((TextView) findViewById(R.id.login)).getText().toString();
        final String userPass = ((TextView) findViewById(R.id.password)).getText().toString();

        final String accountType = AccountGeneral.ACCOUNT_TYPE;

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {
                User user;
                Bundle data = new Bundle();
                try {
                    user = S_SERVER_API.userSignIn(userName, userPass);

                    data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, user.getSession_token());
                    data.putString(PARAM_USER_PASS, userPass);
                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getApplicationContext(), AccountGeneral.PREFS, MODE_PRIVATE);
                    complexPreferences.putObject("user", user);
                    complexPreferences.putObject(AccountManager.KEY_ACCOUNT_NAME, userName);
                    complexPreferences.commit();

                } catch (Exception e) {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    finishLogin(intent);
                }
            }
        }.execute();
    }

    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        final Account account = new Account(accountName, accountType);
        String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        String authtokenType = mAuthTokenType;
        // Creating the account on the device and setting the auth token we got
        // (Not setting the auth token will cause another call to the server to authenticate the user)
        mAccountManager.addAccountExplicitly(account, accountPassword, null);
        mAccountManager.setAuthToken(account, authtokenType, authtoken);
        mAccountManager.setPassword(account, accountPassword);

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);

        startMainActivity();
    }

    private void startMainActivity() {
        Intent main = new Intent(getBaseContext(), MainActivity.class);
        startActivity(main);
        finish();
    }
}
