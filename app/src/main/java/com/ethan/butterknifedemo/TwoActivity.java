package com.ethan.butterknifedemo;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ethan.annotations.BindView;

public class TwoActivity extends AppCompatActivity {

    @BindView(R.id.id_hello) TextView textView1;

    @BindView(R.id.id_hello) TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}