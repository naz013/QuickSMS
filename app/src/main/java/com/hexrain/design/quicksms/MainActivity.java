package com.hexrain.design.quicksms;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hexrain.design.quicksms.helpers.ColorSetter;
import com.hexrain.design.quicksms.helpers.Constants;
import com.hexrain.design.quicksms.helpers.CustomAdapter;
import com.hexrain.design.quicksms.helpers.Database;
import com.hexrain.design.quicksms.helpers.SharedPrefs;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ColorSetter cSetter = new ColorSetter(MainActivity.this);
    private SharedPrefs prefs = new SharedPrefs(MainActivity.this);
    private CheckBox check;
    private CustomAdapter customAdapter;
    private RecyclerView listView;
    private FloatingActionButton mFab;

    private Database db;
    public static final String APP_UI_PREFERENCES = "settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(cSetter.getStyle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cSetter.colorStatus());
        }
        setContentView(R.layout.activity_main);

        getIntent().setAction("JustActivity Created");

        File settingsUI = new File("/data/data/" + getPackageName() + "/shared_prefs/" + APP_UI_PREFERENCES + ".xml");
        if(!settingsUI.exists()) {
            SharedPreferences appUISettings = getSharedPreferences(APP_UI_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor uiEd = appUISettings.edit();
            uiEd.putString(Constants.PREFERENCES_THEME, "1");
            uiEd.putBoolean(Constants.PREFERENCES_USE_DARK_THEME, false);
            uiEd.putBoolean(Constants.PREFERENCES_RATE_SHOWN, false);
            uiEd.apply();
        }

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayUseLogoEnabled(false);

        findViewById(R.id.background).setBackgroundColor(cSetter.getBackgroundStyle());

        mFab = findViewById(R.id.button_floating_action);
        mFab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CreateEdit.class)));

        check = findViewById(R.id.check);
        check.setVisibility(View.GONE);
        check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                prefs.saveBoolean(Constants.PREFERENCES_QUICK_SMS, true);
                check.setVisibility(View.GONE);
            }
        });

        TextView textView3 = findViewById(R.id.textView3);

        listView = findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setOnItemClickListener((parent, view, position, id) -> startActivity(new Intent(MainActivity.this, CreateEdit.class).putExtra("id", id)));

        if (!prefs.loadBoolean(Constants.PREFERENCES_QUICK_SMS)) check.setVisibility(View.VISIBLE);
    }

    private void showRate(){
        SharedPrefs sPrefs = new SharedPrefs(MainActivity.this);

        if (sPrefs.isString(Constants.PREFERENCES_RATE_SHOWN)) {
            if (!sPrefs.loadBoolean(Constants.PREFERENCES_RATE_SHOWN)) {
                int counts = sPrefs.loadInt(Constants.PREFERENCES_APP_RUNS);
                if (counts < 10) {
                    sPrefs.saveInt(Constants.PREFERENCES_APP_RUNS, counts + 1);
                } else {
                    sPrefs.saveInt(Constants.PREFERENCES_APP_RUNS, 0);
                    startActivity(new Intent(MainActivity.this, RateDialog.class));
                }
            }
        } else {
            sPrefs.saveBoolean(Constants.PREFERENCES_RATE_SHOWN, false);
            sPrefs.saveInt(Constants.PREFERENCES_APP_RUNS, 0);
        }
    }

    private void loadList(){
        db = new Database(MainActivity.this);
        db.open();
        customAdapter = new CustomAdapter(MainActivity.this, db.queryTemplates());
        SwipeDismissAdapter adapter = new SwipeDismissAdapter(customAdapter, (listView, reverseSortedPositions) -> {
            for (int position : reverseSortedPositions) {
                db = new Database(MainActivity.this);
                db.open();
                customAdapter = new CustomAdapter(MainActivity.this, db.queryTemplates());
                final long itemId = customAdapter.getItemId(position);
                db.deleteTemplate(itemId);
                loadList();
                Toast.makeText(MainActivity.this, getString(R.string.string_deleted),
                        Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setAbsListView(listView);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_exit){
            finish();
            return true;
        }
        if (id == R.id.action_feedback){
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "feedback.cray@gmail.com" });
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Quick SMS");
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            return true;
        }
        if (id == R.id.action_more){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://search?q=pub:Nazar Suhovich"));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Couldn't launch market", Toast.LENGTH_LONG).show();
            }
        }
        if (id == R.id.action_rate){
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Couldn't launch market", Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showRate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cSetter.colorStatus());
        }

        toolbar.setBackgroundColor(cSetter.colorSetter());
        mFab.setBackgroundColor(cSetter.colorSetter());
        mFab.setRippleColor(cSetter.colorStatus());

        String action = getIntent().getAction();
        if(action == null || !action.equals("JustActivity Created")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            getIntent().setAction(null);
        }

        loadList();
    }
}
