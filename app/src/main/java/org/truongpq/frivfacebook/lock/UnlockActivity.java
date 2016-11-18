package org.truongpq.frivfacebook.lock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.takwolf.android.lock9.Lock9View;

import org.truongpq.frivfacebook.MainActivity;
import org.truongpq.frivfacebook.R;

public class UnlockActivity extends AppCompatActivity {
    public static final String PASS_PREFERENCES = "password";
    public static final String SETTINGS_PREFERENCES = "settingsPreferences";

    private Lock9View lock_view;
    private RelativeLayout layout;
    private NativeExpressAdView adView;

    private SharedPreferences settingsPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        settingsPreferences = getSharedPreferences(SETTINGS_PREFERENCES, 0);
        if (settingsPreferences.getString(PASS_PREFERENCES, null) == null) {
            startActivity(new Intent(this, CreatePassActivity.class));
            finish();
        }

        adView = (NativeExpressAdView) findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());

        initLockView();
    }

    private void initLockView() {
        layout = (RelativeLayout) findViewById(R.id.activity_unlock);
        lock_view = (Lock9View) findViewById(R.id.lock_view);
        lock_view.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {
                if (password.equals(settingsPreferences.getString(PASS_PREFERENCES, null))) {
                    startActivity(new Intent(UnlockActivity.this, MainActivity.class));
                    finish();
                } else {
                    Snackbar snackbar = Snackbar.make(layout, R.string.patterns_do_not_match, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });
    }
}
