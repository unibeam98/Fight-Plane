package com.example.administrator.myapplication;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;


public class MainActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题
         requestWindowFeature(Window.FEATURE_NO_TITLE);
        dafeijiGameView view =  new dafeijiGameView(this);

        setContentView(view);
    }


}

