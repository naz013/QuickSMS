package com.hexrain.design.quicksms;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.hexrain.design.quicksms.helpers.Crypter;
import com.hexrain.design.quicksms.helpers.Database;
import com.hexrain.design.quicksms.helpers.TemplateItem;

public class CreateEditActivity extends AppCompatActivity {

    @Nullable
    private ColorSetter cSetter;
    private EditText editText;
    private TextView textView2;

    @Nullable
    private TemplateItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cSetter = new ColorSetter(CreateEditActivity.this);
        setTheme(cSetter.getStyle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cSetter.colorStatus());
        }
        setContentView(R.layout.fragment_create_template);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.string_add_template);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
        }

        findViewById(R.id.background).setBackgroundColor(cSetter.getBackgroundStyle());

        FloatingActionButton mFab = findViewById(R.id.button_floating_action);
        mFab.setOnClickListener(v -> saveTemplate());

        Intent intent = getIntent();
        long id = intent.getLongExtra("id", 0);
        Database db = new Database(this);
        db.open();
        mItem = db.getTemplate(id);
        db.close();

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

        if (mItem != null) {
            editText.setText(Crypter.decrypt(mItem.getMessage()));
            getSupportActionBar().setTitle(getString(R.string.string_edit_template));
        }
    }

    private void saveTemplate() {
        String text = editText.getText().toString().trim();
        if (text.matches("")) {
            editText.setError(getString(R.string.empty_warming));
            return;
        }

        hideKeyboard();
        String temp = Crypter.encrypt(text);
        Database db = new Database(CreateEditActivity.this);
        db.open();
        if (mItem != null) {
            mItem.setMessage(temp);
            mItem.setDateTime(System.currentTimeMillis());
        } else {
            mItem = new TemplateItem(0, temp, System.currentTimeMillis());
        }
        db.saveTemplate(mItem);
        db.close();
        Toast.makeText(CreateEditActivity.this, getString(R.string.string_saved), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
