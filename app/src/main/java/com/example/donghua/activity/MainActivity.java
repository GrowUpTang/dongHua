package com.example.donghua.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.donghua.view.DialogAreaChose;
import com.example.donghua.R;

public class MainActivity extends Activity implements View.OnClickListener {
    private PopupWindow popupWindow;
    private TextView textView, userName;
    private RelativeLayout relativeLayout, relativeLayoutGender;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView1);
        userName = findViewById(R.id.userName);
        imageView = findViewById(R.id.imageView1);

        relativeLayoutGender = findViewById(R.id.relativeLayoutGender);
        relativeLayoutGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showGender();
            }
        });

        relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DialogAreaChose dialogAreaChose = new DialogAreaChose(MainActivity.this, new DialogAreaChose.AreaChoseListener() {

                    @Override
                    public void onClick(String privince, String city, String district) {
                        String location = privince + city + district;
                        textView.setText(location);
                    }
                });
                dialogAreaChose.show();
            }
        });

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DialogAreaChose dialogAreaChose = new DialogAreaChose(MainActivity.this, new DialogAreaChose.AreaChoseListener() {

                    @Override
                    public void onClick(String privince, String city, String district) {
                        String location = privince + city + district;
                        textView.setText(location);
                    }
                });
                dialogAreaChose.show();
            }
        });
    }

    public void showGender() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentview = inflater.inflate(R.layout.neirong, null);
        contentview.setFocusable(true);
        contentview.setFocusableInTouchMode(true);
        popupWindow = new PopupWindow(contentview, 200, 100);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        TextView tt = (TextView) contentview.findViewById(R.id.nanselect);
        TextView nv = (TextView) contentview.findViewById(R.id.nvselect);
        tt.setOnClickListener(this);
        nv.setOnClickListener(this);

        contentview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    popupWindow.dismiss();

                    return true;
                }
                return false;
            }
        });
        popupWindow.setBackgroundDrawable(new Drawable() {
            @Override
            public void draw(Canvas canvas) {

            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {

            }

            @SuppressLint("WrongConstant")
            @Override
            public int getOpacity() {
                return 0;
            }
        });
        popupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
        popupWindow.showAsDropDown(textView);
//        popupWindow.showAtLocation(textView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupWindow.showAtLocation(textView, Gravity.CENTER, 0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.nanselect:
                userName.setText("male");
                popupWindow.dismiss();
                break;
            case R.id.nvselect:
                userName.setText("female");
                popupWindow.dismiss();
                break;
        }
    }
}