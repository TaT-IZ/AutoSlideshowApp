package com.example.tatsuhiro.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    Timer mTimer;
    Handler mHandler = new Handler();

    Button mStartButton;
    Button mBackButton;
    Button mStartButtonPause;//保留
    Cursor cursor;
    private TimerTask mTimerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartButton = (Button) findViewById(R.id.mStartButton);
        mBackButton = (Button) findViewById(R.id.mBackButton);
        mStartButtonPause = (Button) findViewById(R.id.mStartButtonPause);


        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }

        // getContensinfoで呼び出されたら以下の処理を行う
        mStartButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (cursor.moveToNext()) {
                    // indexからIDを取得し、そのIDから画像のURIを取得する
                    setImageView();

                } else {//一番最初にもどる処理
                    cursor.moveToFirst();
                    setImageView();

                }
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (cursor.moveToPrevious()) {
                    setImageView();

                } else {//一番最初にもどる処理
                    cursor.moveToLast();
                    setImageView();
                }
            }
        });


        mStartButtonPause.setOnClickListener(new View.OnClickListener()

        {

            @Override
            public void onClick(View v)  {

                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (cursor.moveToNext()) {
                                    setImageView();
                                }
                            }
                        });
                    }
                }, 100, 2000);    // 最初に始動させるまで 100ミリ秒、ループの間隔を 秒 に設定
            }
        });
    }

    private void setImageView() {
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
        imageVIew.setImageURI(imageUri);
    }

    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            setImageView();//
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        Log.d("Android", "onDestroy");
    }
}

