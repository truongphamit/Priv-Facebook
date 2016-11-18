package org.truongpq.frivfacebook.lock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.takwolf.android.lock9.Lock9View;

import org.truongpq.frivfacebook.MainActivity;
import org.truongpq.frivfacebook.R;

public class ChangePassActivity extends AppCompatActivity {
    public static final String PASS_PREFERENCES = "password";
    public static final String SETTINGS_PREFERENCES = "settingsPreferences";

    private Lock9View lock_view_old;
    private Lock9View lock_view;
    private Lock9View lock_view_again;
    private TextView tv_step;
    private RelativeLayout layout;

    private SharedPreferences settingsPreferences;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        settingsPreferences = getSharedPreferences(SETTINGS_PREFERENCES, 0);

        initView();
    }

    private void initView() {
        layout = (RelativeLayout) findViewById(R.id.activity_change_pass);
        tv_step = (TextView) findViewById(R.id.tv_step);
        lock_view_old = (Lock9View) findViewById(R.id.lock_view_old);
        lock_view = (Lock9View) findViewById(R.id.lock_view);
        lock_view_again = (Lock9View) findViewById(R.id.lock_view_again);

        lock_view_old.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {
                if (password.equals(settingsPreferences.getString(PASS_PREFERENCES, null))) {
                    lock_view_old.setVisibility(View.GONE);
                    lock_view.setVisibility(View.VISIBLE);
                    tv_step.setText(getString(R.string.set_a_new_password));
                } else {
                    Snackbar snackbar = Snackbar.make(layout, R.string.patterns_do_not_match, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });

        lock_view.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {
                pass = password;
                lock_view.setVisibility(View.GONE);
                lock_view_again.setVisibility(View.VISIBLE);
                tv_step.setText(getString(R.string.enter_password_again));
            }
        });

        lock_view_again.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {
                if (password.equals(pass)) {
                    settingsPreferences.edit().putString(PASS_PREFERENCES, password).apply();
                    finish();
                } else {
                    Snackbar snackbar = Snackbar.make(layout, R.string.patterns_do_not_match, Snackbar.LENGTH_SHORT)
                            .setAction(getResources().getString(R.string.reset), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    lock_view.setVisibility(View.VISIBLE);
                                    lock_view_again.setVisibility(View.GONE);
                                    tv_step.setText(getString(R.string.set_a_new_password));
                                }
                            });
                    snackbar.setActionTextColor(Color.CYAN);
                    snackbar.show();
                }
            }
        });
    }
}
