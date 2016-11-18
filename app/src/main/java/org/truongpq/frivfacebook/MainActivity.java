package org.truongpq.frivfacebook;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;

import org.truongpq.frivfacebook.dialogs.WebViewDialog;
import org.truongpq.frivfacebook.lock.ChangePassActivity;
import org.truongpq.frivfacebook.webview.AdvancedWebView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String NEWS_FEED_URL = "https://m.facebook.com/home.php";
    private static final String MESSAGE_URL = "https://m.facebook.com/messages";
    private static final String NOTIFICATION_URL = "https://m.facebook.com/notifications";
    private static final String SEARCH_URL = "https://m.facebook.com/search";
    public static final String DATA_SAVING_PREFERENCES = "dataSaving";
    public static final String SETTINGS_PREFERENCES = "settingsPreferences";

    private final List<String> HOSTNAMES = Arrays.asList("facebook.com", "*.facebook.com");
    private AdvancedWebView webview;
    private SwipeRefreshLayout swipeView;
    private FloatingActionButton fabJumpToTop;

    private SharedPreferences settingsPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);
        settingsPreferences = getSharedPreferences(SETTINGS_PREFERENCES, 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        TextView tv_version = (TextView) hView.findViewById(R.id.tv_version);
        tv_version.setText(getString(R.string.version) + BuildConfig.VERSION_NAME);

        initWebView();
        initView();
    }

    private void initWebView() {
        webview = (AdvancedWebView) findViewById(R.id.webview);
        webview.setListener(this, new AdvancedWebView.Listener() {
            @Override
            public void onPageStarted(String url, Bitmap favicon) {
                swipeView.setRefreshing(true);
            }

            @Override
            public void onPageFinished(String url) {
                swipeView.setRefreshing(false);
            }

            @Override
            public void onPageError(int errorCode, String description, String failingUrl) {

            }

            @Override
            public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

            }

            @Override
            public void onExternalPageRequest(String url) {
                Dialog dialog = new WebViewDialog(MainActivity.this, url);
                dialog.show();
                webview.goBack();
            }

            @Override
            public void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    // User scrolled down, hide the button
                    fabJumpToTop.hide();
                } else if (scrollY < oldScrollY) {
                    // User scrolled up, show the button
                    fabJumpToTop.show();
                }
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu) {

            }
        });
        webview.addPermittedHostnames(HOSTNAMES);
        registerForContextMenu(webview);

        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        loadUserAgentWebView();
        webview.loadUrl(getString(R.string.home_url));
    }

    private void initView() {
        fabJumpToTop = (FloatingActionButton) findViewById(R.id.fab_jump_to_top);
        fabJumpToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webview.scrollTo(0, 0);
            }
        });

        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webview.reload();
            }
        });
    }

    private void loadUserAgentWebView() {
        if (!settingsPreferences.getBoolean(DATA_SAVING_PREFERENCES, false)) {
            webview.getSettings().setUserAgentString(getString(R.string.user_agent));
        } else {
            webview.getSettings().setUserAgentString(getString(R.string.user_agent_data_saving));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (webview.canGoBack()) {
            webview.goBack();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                    .setMessage(getString(R.string.title_exit))
                    .setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news_feed) {
            webview.loadUrl(NEWS_FEED_URL);
        } else if (id == R.id.nav_message) {
            webview.loadUrl(MESSAGE_URL);
        } else if (id == R.id.nav_notification) {
            webview.loadUrl(NOTIFICATION_URL);
        } else if (id == R.id.nav_search) {
            webview.loadUrl(SEARCH_URL);
        } else if (id == R.id.nav_change_pass) {
            startActivity(new Intent(this, ChangePassActivity.class));
        } else if (id == R.id.nav_feedback) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + getPackageName()));
            startActivity(intent);
        } else if (id == R.id.nav_exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                    .setMessage(getString(R.string.title_exit))
                    .setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if (id == R.id.nav_setting) {
            final Dialog settingsDialog = new BottomSheetDialog(this);
            settingsDialog.setContentView(R.layout.bottom_dialog_settings);
            Switch switch_data_saving = (Switch) settingsDialog.findViewById(R.id.switch_data_saving);
            switch_data_saving.setChecked(settingsPreferences.getBoolean(DATA_SAVING_PREFERENCES, false));
            switch_data_saving.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    settingsPreferences.edit().putBoolean(DATA_SAVING_PREFERENCES, isChecked).apply();
                    loadUserAgentWebView();
                    webview.reload();
                }
            });
            settingsDialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();
        if (id == R.id.action_gift) {
            final Dialog dialog = new Dialog(this, R.style.MaterialDialogSheet);
            dialog.setContentView(R.layout.dialog_gift);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            NativeExpressAdView expressAdView = (NativeExpressAdView) dialog.findViewById(R.id.adView);
            expressAdView.loadAd(new AdRequest.Builder().build());
            expressAdView.setAdListener(new AdListener() {
                @Override
                public void onAdOpened() {
                    dialog.dismiss();
                    super.onAdOpened();
                }
            });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
