package com.hexrain.design.quicksms.helpers;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.hexrain.design.quicksms.R;

public class CustomAdapter extends CursorAdapter{

    TextView eventType;
    LayoutInflater inflater;
    Context cContext;
    Cursor c;

    @SuppressWarnings("deprecation")
    public CustomAdapter(Context context, Cursor c) {
        super(context, c);
        this.cContext = context;
        inflater = LayoutInflater.from(context);
        this.c = c;
        c.moveToFirst();
    }

    @Override
    public int getCount() {
        return c.getCount();
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return cursor.getLong(cursor.getColumnIndex("_id"));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        c.moveToPosition(position);

        if (convertView == null) {
            inflater = (LayoutInflater) cContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_layout, null);
        }

        ColorSetter cs = new ColorSetter(cContext);
        CardView card = (CardView) convertView.findViewById(R.id.card);
        card.setCardBackgroundColor(cs.getCardStyle());

        Typeface typeface = Typeface.createFromAsset(cContext.getAssets(), "Roboto-Light.ttf");

        eventType = (TextView) convertView.findViewById(R.id.textView);
        eventType.setTypeface(typeface);

        String message = c.getString(c.getColumnIndex(Constants.COLUMN_TEXT));
        eventType.setText(new Crypter().decrypt(message));

        return convertView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.list_item_layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}