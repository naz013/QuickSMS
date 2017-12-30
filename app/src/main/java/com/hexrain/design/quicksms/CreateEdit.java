package com.hexrain.design.quicksms;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hexrain.design.quicksms.helpers.ColorSetter;
import com.hexrain.design.quicksms.helpers.Constants;
import com.hexrain.design.quicksms.helpers.Crypter;
import com.hexrain.design.quicksms.helpers.Database;

public class CreateEdit extends AppCompatActivity {

    private ColorSetter cSetter = new ColorSetter(CreateEdit.this);
    private EditText editText;
    private TextView textView2;

    private long id = 0;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(cSetter.getStyle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cSetter.colorStatus());
        }
        setContentView(R.layout.fragment_create_template);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.string_add_template);
        getSupportActionBar().setDisplayUseLogoEnabled(false);

        findViewById(R.id.background).setBackgroundColor(cSetter.getBackgroundStyle());

        FloatingActionButton mFab = findViewById(R.id.button_floating_action);
        mFab.setOnClickListener(v -> saveTemplate());

        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);

        editText = findViewById(R.id.edit);
        textView2 = findViewById(R.id.textView2);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();
                int left = 120 - text.length();
                textView2.setText(getString(R.string.string_characters_left) + " " + left);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        db = new Database(CreateEdit.this);
        db.open();
        if (id != 0){
            Cursor c = db.getTemplate(id);
            if (c != null && c.moveToFirst()){
                String text = c.getString(c.getColumnIndex(Constants.COLUMN_TEXT));
                editText.setText(new Crypter().decrypt(text));
                getSupportActionBar().setTitle(getString(R.string.string_edit_template));
            }
        }
    }

    private void saveTemplate() {
        String text = editText.getText().toString().trim();
        if (text.matches("")) {
            editText.setError(getString(R.string.empty_warming));
            return;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        String temp = new Crypter().encrypt(text);
        db = new Database(CreateEdit.this);
        db.open();
        if (id != 0){
            db.updateTemplate(id, temp, System.currentTimeMillis());
        } else {
            db.addTemplate(temp, System.currentTimeMillis());
        }
        Toast.makeText(CreateEdit.this, getString(R.string.string_saved), Toast.LENGTH_SHORT).show();
        finish();
    }
}
