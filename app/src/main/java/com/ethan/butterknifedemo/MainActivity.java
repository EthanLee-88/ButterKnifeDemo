package com.ethan.butterknifedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.butterknife.MyButterKnife;
import com.butterknife.Unbinder;
import com.ethan.annotations.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.id_hello) TextView textView1;
    @BindView(R.id.id_hello) TextView textView2;

    private Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Log.d("MainActivity", "TAG = ");
        unbinder = MyButterKnife.bind(this);
        textView1.setText("碧云天");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unBind();
    }
}