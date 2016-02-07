package com.enricotj.tangent;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.enricotj.tangent.fragments.HomeFragment;
import com.enricotj.tangent.fragments.LoginFragment;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginListener, HomeFragment.OnLogoutListener {

    private LoginFragment mLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            Firebase.setAndroidContext(this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        Firebase firebase = new Firebase(Constants.FIREBASE_URL);
        AuthData auth = firebase.getAuth();
        if (auth == null || isExpired(auth)) {
            switchToLoginFragment();
        }
        else {
            switchToHomeFragment();
        }
    }

    private boolean isExpired(AuthData authData) {
        return (System.currentTimeMillis() / 1000) >= authData.getExpires();
    }

    @Override
    public void onLogin(String email, String password) {
        Firebase firebase = new Firebase(Constants.FIREBASE_URL);
        firebase.authWithPassword(email, password, new TangentAuthResultHandler());
    }

    @Override
    public void onLoginNewAccount(String email, String username, String password) {
        Firebase firebase = new Firebase(Constants.FIREBASE_URL);
        firebase.authWithPassword(email, password, new TangentAuthResultHandler(username));
    }

    @Override
    public void onLogout() {
        Firebase firebase = new Firebase(Constants.FIREBASE_URL);
        firebase.unauth();
        switchToLoginFragment();
    }

    class TangentAuthResultHandler implements Firebase.AuthResultHandler {
        private String username = null;
        public TangentAuthResultHandler() { }
        public TangentAuthResultHandler(String username) { this.username = username; }

        @Override
        public void onAuthenticated(AuthData authData) {
            if (username != null) {
                Map<String, String> map = new HashMap<>();
                map.put("username", username);
                Firebase users = new Firebase(Constants.FIREBASE_USERS);
                users.child(authData.getUid()).setValue(map);
                username = null;
            }
            switchToHomeFragment();
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            Log.e(Constants.TAG, "onAuthenticationError: " + firebaseError.getMessage());
            mLoginFragment.invalidLogin();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    private void switchToLoginFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mLoginFragment = new LoginFragment();
        ft.replace(R.id.fragment, mLoginFragment, "Login");
        ft.commit();
    }

    private void switchToHomeFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment homeFragment = new HomeFragment();
        ft.replace(R.id.fragment, homeFragment, "Home");
        ft.commit();
    }

    private void showLoginError(String message) {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("Login");
        loginFragment.onLoginError(message);
    }
}
