package com.example.simplememoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText edit_memo_area;
    FloatingActionButton fab_save;
    private final String fileName = "memo.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_memo_area = findViewById(R.id.edit_memo_area);
        fab_save = findViewById(R.id.fab_save);

        Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if(multiplePermissionsReport.areAllPermissionsGranted()) {
                            Toast.makeText(MainActivity.this, "すべてのパーミッションが許可されました", Toast.LENGTH_SHORT).show();
                            appSetUP();
                        } else {
                            Toast.makeText(MainActivity.this, "パーミッションが許可されませんでした", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }

    private void appSetUP() {
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeMemo(edit_memo_area.getText().toString());
            }
        });
        readMemo();
    }

    private void writeMemo(String memo) {
        File appFileDir = getFilesDir();
        File memoFile = new File(appFileDir.getAbsolutePath(), fileName);
        Log.d("MyLog", memoFile.getAbsolutePath());
        if(memoFile.exists()) {
            Log.d("MyLog", "ファイルが既に存在しています");
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(memoFile, false);
            fileOutputStream.write(memo.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(MainActivity.this, "ファイルを保存しました",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "ファイルの保存に失敗しました", Toast.LENGTH_SHORT).show();
        }

        Log.d("MyLog", appFileDir.getAbsolutePath());
        if(Build.VERSION.SDK_INT >= 29) {
//            Environment.getExternalStorageDirectory()を使わない保存方法
        } else {
            //            Environment.getExternalStorageDirectory()を使える保存方法
        }
    }

    private void readMemo() {
        File appFileDir = getFilesDir();
        File memoFile = new File(appFileDir.getAbsolutePath(), fileName);
        if(!memoFile.exists()) {
            Toast.makeText(this, "ファイルが存在しません", Toast.LENGTH_SHORT).show();
            return;
        }
        FileInputStream fileInputStream = null;
        InputStreamReader streamReader = null;
        try {
            fileInputStream = new FileInputStream(memoFile);
            streamReader = new InputStreamReader(fileInputStream,"UTF-8");
            List<String> result = new ArrayList<>();
            int data;
            while((data = streamReader.read()) != -1) {
                char item = (char) data;
                result.add(String.valueOf(item));
            }
            String resultText =  String.join("", result);
            Log.d("MyLog", resultText);
            Toast.makeText(this, "ファイルを読み込みました", Toast.LENGTH_SHORT).show();
            edit_memo_area.setText(resultText);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "ファイルの読み込みに失敗しました", Toast.LENGTH_SHORT).show();
        }
    }
}