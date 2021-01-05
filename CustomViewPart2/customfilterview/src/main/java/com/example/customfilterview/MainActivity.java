package com.example.customfilterview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    CustomFilterView mCustomFilterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BaseFilterViewAdapter filterViewAdapter = new FilterViewAdapter(this);
        mCustomFilterView = findViewById(R.id.filterView);
        mCustomFilterView.setFilterViewAdapter(filterViewAdapter);
    }
}