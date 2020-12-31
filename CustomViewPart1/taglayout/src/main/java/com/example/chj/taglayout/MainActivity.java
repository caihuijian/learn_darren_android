package com.example.chj.taglayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TagLayout layout = new TagLayout(this);
        final List<String> stringOfViews = new ArrayList<>();
        stringOfViews.add("Java Book1");
        stringOfViews.add("Java Book2");
        stringOfViews.add("Java Book3");
        stringOfViews.add("Java Book1");
        stringOfViews.add("Java Book2");
        stringOfViews.add("Java Book3");
        stringOfViews.add("Java Book1");
        stringOfViews.add("Java Book2");
        stringOfViews.add("Java Book3");
        stringOfViews.add("Java Book1");
        stringOfViews.add("Java Book2");
        stringOfViews.add("Java Book3");
        stringOfViews.add("Java Book1");
        stringOfViews.add("Java Book2");
        stringOfViews.add("Java Book3");
        stringOfViews.add("Java Book1");
        stringOfViews.add("Java Book2");
        stringOfViews.add("Java Book3");
        stringOfViews.add("Java Book1");
        stringOfViews.add("Java Book2");
        stringOfViews.add("Java Book3");
        stringOfViews.add("Java Book1");
        stringOfViews.add("Java Book2");
        stringOfViews.add("Java Book3");
        stringOfViews.add("Java Book1");
        stringOfViews.add("Java Book2");
        stringOfViews.add("Java Book3");
        stringOfViews.add("C++ Book1");
        stringOfViews.add("C# Book2");
        stringOfViews.add("ACSS Book3");
        stringOfViews.add("哈十三点v是 1");
        stringOfViews.add("111111111111111111");
        stringOfViews.add("22");
        stringOfViews.add("33333333333");
        stringOfViews.add("44");
        stringOfViews.add("555553");
        stringOfViews.add("6666666661");
        stringOfViews.add("77777");
        stringOfViews.add("88888");
        stringOfViews.add("999");
        stringOfViews.add("111111111111111111");
        stringOfViews.add("22");
        stringOfViews.add("333333333");
        stringOfViews.add("44");
        stringOfViews.add("555553");
        stringOfViews.add("6666666661");
        stringOfViews.add("77777");
        stringOfViews.add("88888");
        stringOfViews.add("999");
        stringOfViews.add("流浪地球");
        stringOfViews.add("OverLord不死者之王");
        layout.setAdapter(new TagLayout.TagLayoutAdapter() {
            @Override
            public View getViewAtPosition(int index, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                TextView textView = (TextView) inflater.inflate(R.layout.tag_view_items, parent, false);
                textView.setText(stringOfViews.get(index));
                return textView;
            }

            @Override
            void itemClick(String textString) {
                Toast.makeText(MainActivity.this, textString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public int getCount() {
                return stringOfViews.size();
            }
        });
        addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
