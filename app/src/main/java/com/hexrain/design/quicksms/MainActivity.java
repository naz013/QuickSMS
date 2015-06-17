package com.hexrain.design.quicksms;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hexrain.design.quicksms.helpers.ColorSetter;
import com.hexrain.design.quicksms.helpers.Constants;
import com.hexrain.design.quicksms.helpers.CustomAdapter;
import com.hexrain.design.quicksms.helpers.Database;
import com.hexrain.design.quicksms.helpers.SharedPrefs;
import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

import java.io.File;


public class MainActivity extends ActionBarActivity {

    Toolbar toolbar;
    ColorSetter cSetter = new ColorSetter(MainActivity.this);
    SharedPrefs prefs = new SharedPrefs(MainActivity.this);
    FloatingActionButton mFab;
    CheckBox check;
    CustomAdapter customAdapter;
    ListView listView;
    TextView textView3;

    private static Database db;
    public static final String APP_UI_PREFERENCES = "settings";
    SharedPreferences appUISettings;

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
            appUISettings = getSharedPreferences(APP_UI_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor uiEd = appUISettings.edit();
            uiEd.putString(Constants.PREFERENCES_THEME, "1");
            uiEd.putBoolean(Constants.PREFERENCES_USE_DARK_THEME, false);
            uiEd.putBoolean(Constants.PREFERENCES_RATE_SHOWN, false);
            uiEd.commit();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayUseLogoEnabled(false);

        findViewById(R.id.background).setBackgroundColor(cSetter.getBackgroundStyle());

        mFab = (FloatingActionButton) findViewById(R.id.button_floating_action);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateEdit.class));
            }
        });
        mFab.setType(FloatingActionButton.TYPE_NORMAL);

        check = (CheckBox) findViewById(R.id.check);
        check.setVisibility(View.GONE);
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    prefs.saveBoolean(Constants.PREFERENCES_QUICK_SMS, true);
                    check.setVisibility(View.GONE);
                }
            }
        });

        textView3 = (TextView) findViewById(R.id.textView3);

        listView = (ListView) findViewById(R.id.list);
        listView.setEmptyView(textView3);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this, CreateEdit.class).putExtra("id", id));
            }
        });

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
        SwipeDismissAdapter adapter = new SwipeDismissAdapter(customAdapter, new OnDismissCallback() {
            @Override
            public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions) {
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
        mFab.setColorNormal(cSetter.colorSetter());
        mFab.setColorPressed(cSetter.colorStatus());

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
